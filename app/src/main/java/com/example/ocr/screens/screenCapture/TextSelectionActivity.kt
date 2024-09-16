package com.example.ocr.screens.screenCapture

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ocr.R
import com.example.ocr.base.SharedPreference
import com.example.ocr.component.CanvasBitmapDrawable
import com.example.ocr.databinding.ActivityMainBinding
import com.example.ocr.screens.GestureAction
import com.example.ocr.screens.TextCopyMetaData
import com.example.ocr.screens.settings.SettingsFragment
import com.example.ocr.util.buildString
import com.example.ocr.util.copyToClipboard
import com.example.ocr.util.getReadAndWritePermission
import com.example.ocr.util.onVibrationClick
import com.example.ocr.util.shareText
import com.example.ocr.util.showToast
import com.example.ocr.util.toBitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class TextSelectionActivity : AppCompatActivity() {

    companion object {
        const val CAPTURED_IMAGE_URI = "capturedBitmapUri"
        private const val EMPTY = ""
    }

    @Inject
    lateinit var sharedPreference: SharedPreference
    private lateinit var binding: ActivityMainBinding
    private lateinit var gestureDetector: GestureDetector
    private val selectedBoundingBoxes = LinkedHashSet<Rect>()
    private val selectedTexts = LinkedHashSet<String>()
    private val recognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }
    private lateinit var drawable: CanvasBitmapDrawable
    private var scannedText: Text? = null



    private val easyCopyMetadata: TextCopyMetaData by lazy {
        val prefs = sharedPreference
        val doubleTapActionStr = prefs.getString(SettingsFragment.DOUBLE_TAP, EMPTY) ?: EMPTY
        val onDoubleTapAction = GestureAction.getEnumFromString(doubleTapActionStr)
        val onLongPressActionStr = prefs.getString(SettingsFragment.LONG_PRESS, EMPTY) ?: EMPTY
        val onLongPressAction = GestureAction.getEnumFromString(onLongPressActionStr)
        val shouldSelectAllText =
            prefs.getBoolean(SettingsFragment.SELECT_ALL_TEXT_BY_DEFAULT, false)
        val shouldPreviewTextZone = prefs.getBoolean(SettingsFragment.TEXT_PREVIEW_ZONE, false)
        val isSelectionOrderManual =
            prefs.getString(SettingsFragment.SELECTION_ORDER)?.startsWith("Dynamic") ?: false

         TextCopyMetaData(
            onDoubleTap = onDoubleTapAction,
            onLongPress = onLongPressAction,
            shouldSelectAllText = shouldSelectAllText,
            shouldPreviewTextZone = shouldPreviewTextZone,
            isSelectionOrderManual = isSelectionOrderManual
        )
    }


    private fun processTheBitmap(bitmap: Bitmap?) {
        bitmap ?: return
        drawable = CanvasBitmapDrawable(bitmap) { bitmap ->
            binding.img.setImageBitmap(bitmap)
        }
        binding.img.scaleType = ImageView.ScaleType.FIT_XY
        binding.img.setImageBitmap(bitmap)
        binding.img.invalidate()
        processImage(bitmap)
        selectedBoundingBoxes.clear()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent.getStringExtra(CAPTURED_IMAGE_URI)?.let { uri ->
            val photoBitmap =
                BitmapFactory.decodeStream(contentResolver.openInputStream(Uri.parse(uri)))
            processTheBitmap(bitmap = photoBitmap)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupGestureDetector()
        setupObserver()
    }

    private val contentLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                processTheBitmap(uri.toBitmap(this@TextSelectionActivity))
            }
        }


    private fun setupObserver() {
        with(binding) {
            btnCapture.onVibrationClick {
                if (getReadAndWritePermission()) {
                    contentLauncher.launch("image/*")
                }
            }
            btnCopy.onVibrationClick {
                if (scannedText?.buildString().isNullOrEmpty()) {
                    showToast("Please scan an image")
                    return@onVibrationClick
                }
                showToast("Copied to clipboard")
                copyToClipboard(selectedTexts.joinToString())
            }

            btnShare.onVibrationClick {
                if (scannedText?.buildString().isNullOrEmpty()) {
                    showToast("Please scan an image")
                    return@onVibrationClick
                }
                showToast("Sending...")
                shareText(scannedText.toString())
            }
            img.setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
                true
            }
        }
    }


    private fun setupGestureDetector() {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                val x = e.x.toInt()
                val y = e.y.toInt()

                scannedText?.let { text ->
                    val touchedLine = text.textBlocks.flatMap { it.lines }
                        .find { it.boundingBox?.contains(x, y) == true }

                    touchedLine?.let { line ->
                        val textToToggle = line.text
                        val boundingBox = line.boundingBox

                        if (selectedTexts.contains(textToToggle)) {
                            selectedTexts.remove(textToToggle)
                            selectedBoundingBoxes.remove(boundingBox)
                            showToast("Deselected: $textToToggle")
                            drawable.clearHighlight(boundingBox)

                        } else {
                            selectedTexts.add(textToToggle)
                            boundingBox?.let {
                                selectedBoundingBoxes.add(it)
                                showToast("Selected: $textToToggle")
                                drawable.drawHighlight(boundingBox)
                            }
                        }

                        return true
                    }
                }
                return super.onSingleTapConfirmed(e)
            }
        })
    }

    private fun processImage(photoBitmap: Bitmap) {
        val image = InputImage.fromBitmap(photoBitmap, 0)
        recognizer.process(image)
            .addOnSuccessListener { scannedText ->
                this.scannedText = scannedText
                drawDetectedTextBounds(scannedText)
                if (easyCopyMetadata.shouldPreviewTextZone) {
                    selectedBoundingBoxes.forEach { rect ->
                        drawable.drawDimHighlight(rect)
                    }
                }
                binding.scannedTv.text = scannedText.text
            }
            .addOnFailureListener { exception ->
                Log.e("arun", exception.toString())
            }
    }


    private fun drawDetectedTextBounds(scannedText: Text) {
        selectedBoundingBoxes.clear()
        scannedText.textBlocks.forEach { block ->
            block.lines.forEach { line ->
                line.boundingBox?.let { boundingBox ->
                    selectedBoundingBoxes.add(boundingBox)
                }
            }
        }
    }
}

