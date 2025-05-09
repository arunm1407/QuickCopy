package com.example.ocr.util

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.app.Notification
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.text.Text
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.InputStream

/**
 * Created by Arun @ak - 14213  on 20/08/24.
 */

fun Activity.getScreenCapturePermission(requestCode : Int) {
    val projectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    val intent = projectionManager.createScreenCaptureIntent()
    startActivityForResult(intent, requestCode)
}


fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.shareText(message: String) {
    Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, message)
        type = "text/plain"
    }.also { intent ->
        startActivity(intent)
    }
}



fun Text.buildString(): String {
    return buildString {
        textBlocks.forEach { block ->
            block.lines.forEach { line ->
                append(line.text).append("\n")
            }
            append("\n")
        }
    }
}

fun Context.copyToClipboard(content: String) {
    val clipboardManager = ContextCompat.getSystemService(this, ClipboardManager::class.java)!!
    val clip = ClipData.newPlainText("clipboard", content)
    clipboardManager.setPrimaryClip(clip)
}

fun Context.vibrate(duration: Long = 50) {
    val vibrator = getSystemService(Vibrator::class.java)
    if (vibrator != null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    duration, VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }
}

fun View.onVibrationClick(action: () -> Unit) {
    setOnClickListener {
        this.context.vibrate()
        action()
    }
}

 fun Bitmap.cropBitmap( bounds: Rect): Bitmap {
    return Bitmap.createBitmap(
        this,
        bounds.left.coerceAtLeast(0),
        bounds.top.coerceAtLeast(0),
        bounds.width().coerceAtMost(width),
        bounds.height().coerceAtMost(height)
    )
}

fun Activity.getReadAndWritePermission(): Boolean {
    val permissions = mutableListOf<String>()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(android.Manifest.permission.READ_MEDIA_IMAGES)
        permissions.add(android.Manifest.permission.CAMERA)
    } else {
        permissions.add(android.Manifest.permission.CAMERA)
        permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    if (permissions.isEmpty()) {
        showToast("Permission not found")
        return false
    }

    val listPermissionsNeeded = permissions.filter {
        ContextCompat.checkSelfPermission(
            this,
            it
        ) != PackageManager.PERMISSION_GRANTED
    }

    if (listPermissionsNeeded.isNotEmpty()) {
        ActivityCompat.requestPermissions(
            this,
            listPermissionsNeeded.toTypedArray(),
            101
        )
        return false
    }
    return true
}

fun Uri.toBitmap(context: Context): Bitmap? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(this)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun AppCompatActivity.sanitizeTheScreen(){
    window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            )
}


fun AppCompatActivity.fullScreen() {
    window.setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    )
    window.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
    )
    window.setBackgroundDrawableResource(android.R.color.transparent)
}

 fun setClickListeners(vararg viewActions: Pair<View, () -> Unit>) {
    viewActions.forEach { (view, action) ->
        view.setOnClickListener { action() }
    }
}

fun NotificationManagerCompat.checkPermissionAndNotify(
    context: Context,
    id: Int,
    notification: Notification,
    onEnd: () -> Unit
) = runCatching {
    if (ActivityCompat.checkSelfPermission(context, POST_NOTIFICATIONS) == PERMISSION_GRANTED) {
        notify(id, notification)
        onEnd()
    }
}

fun <T> LifecycleOwner.collectFlowWithLifecycle(
    flow: Flow<T>,
    collector: suspend (T) -> Unit,
    state: Lifecycle.State = Lifecycle.State.STARTED,
) {
    lifecycleScope.launch {
        flow.flowWithLifecycle(lifecycle, state)
            .onEach { collector(it) }
            .launchIn(this)
    }
}

fun Context.shortToast(message: String, noGravity: Boolean = false) {
    toast(message, Toast.LENGTH_SHORT, noGravity)
}

fun Context.longToast(message: String, noGravity: Boolean = false) {
    toast(message, Toast.LENGTH_LONG, noGravity)
}

fun Context.shortToast(resId: Int, noGravity: Boolean = false) {
    shortToast(this.getString(resId), noGravity)
}

fun Context.longToast(resId: Int, noGravity: Boolean = false) {
    longToast(this.getString(resId), noGravity)
}

fun Context.toast(message: String, toastDuration: Int, noGravity: Boolean) {
    val warningToast = Toast.makeText(this, message, toastDuration)
    if (!noGravity) {
        warningToast.setGravity(Gravity.CENTER, 0, 0)
    }
    warningToast.show()
}
