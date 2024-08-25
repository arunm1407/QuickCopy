package com.example.ocr

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ocr.databinding.ActivityMainBinding
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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gestureDetector: GestureDetector
    private lateinit var photoBitmap: Bitmap
    private val selectedBoundingBoxes = LinkedHashSet<Rect>()
    private val selectedTexts = LinkedHashSet<String>()
    private val recognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }
    private var overlayBitmap: Bitmap? = null
    private var overlayCanvas: Canvas? = null
    private var scannedText: Text? = null

    private val contentLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                with(binding) {
                    photoBitmap = uri.toBitmap(this@MainActivity) ?: return@with
                    initializeOverlay(photoBitmap)
                    img.setImageBitmap(photoBitmap)
                    processImage(photoBitmap)
                    selectedBoundingBoxes.clear()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupGestureDetector()
        setupObserver()
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
                            clearHighlight(boundingBox)
                        } else {
                            selectedTexts.add(textToToggle)
                            boundingBox?.let {
                                selectedBoundingBoxes.add(it)
                                showToast("Selected: $textToToggle")

                                drawHighlight(boundingBox)
                            }
                        }

                        return true
                    }
                }
                return super.onSingleTapConfirmed(e)
            }
        })
    }

    private fun initializeOverlay(bitmap: Bitmap) {
        overlayBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        overlayCanvas = Canvas(overlayBitmap!!)
    }

    private fun drawHighlight(boundingBox: Rect?) {
        boundingBox?.let {
            val paint = Paint().apply {
                color = Color.parseColor("#3399FF")
                style = Paint.Style.FILL
                alpha = 80
            }
            overlayCanvas?.drawRect(boundingBox, paint)
            updateImage()
        }
    }

    private fun clearHighlight(boundingBox: Rect?) {
        boundingBox?.let {
            val paint = Paint().apply {
                color = Color.TRANSPARENT
                xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            }
            overlayCanvas?.drawRect(boundingBox, paint)
            updateImage()
        }
    }

    private fun updateImage() {
        val combinedBitmap = Bitmap.createBitmap(photoBitmap.width, photoBitmap.height, photoBitmap.config)
        val canvas = Canvas(combinedBitmap)
        canvas.drawBitmap(photoBitmap, 0f, 0f, null)
        overlayBitmap?.let { canvas.drawBitmap(it, 0f, 0f, null) }

        binding.img.setImageBitmap(combinedBitmap)
    }

    private fun updateImageWithHighlights(bitmap: Bitmap, boundingBoxes: List<Rect>) {
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val paint = Paint().apply {
            color = Color.parseColor("#ADD8E6")
            style = Paint.Style.FILL
        }

        boundingBoxes.forEach { box ->
            canvas.drawRect(box, paint)
        }

        binding.img.setImageBitmap(mutableBitmap)
    }

    private fun updateImageWithHighlights(photoBitmap: Bitmap, highlightColor: String ) {
        val mutableBitmap = photoBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)

        val paint = Paint().apply {
            color = Color.parseColor(highlightColor)
            style = Paint.Style.FILL
        }

        groupBoundingBoxes().forEach { group ->
            canvas.drawRect(group, paint)
        }
        binding.img.setImageBitmap(mutableBitmap)
    }

    private fun groupBoundingBoxes(): List<Rect> {
        val groupedRect = mutableListOf<Rect>()

        selectedBoundingBoxes.forEach { box ->
            var merged = false
            for (group in groupedRect) {
                if (Rect.intersects(group, box) || isCloseEnough(group, box)) {
                    group.union(box)
                    merged = true
                    break
                }
            }
            if (!merged) {
                groupedRect.add(Rect(box))
            }
        }

        return groupedRect
    }

    private fun isCloseEnough(rect1: Rect, rect2: Rect): Boolean {
        val proximityThreshold = 10
        return rect1.intersects(
            rect2.left - proximityThreshold,
            rect2.top - proximityThreshold,
            rect2.right + proximityThreshold,
            rect2.bottom + proximityThreshold
        )
    }

    private fun processImage(photoBitmap: Bitmap) {
        val image = InputImage.fromBitmap(photoBitmap, 0)

        recognizer.process(image)
            .addOnSuccessListener { scannedText ->
                this.scannedText = scannedText
//                drawDetectedTextBounds( scannedText)
//                updateImageWithHighlights(photoBitmap,"#8099CCFF")
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
