package com.example.ocr

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ScreenCaptureService : Service() {

    companion object {
        const val CHANNEL_ID = "ScreenCaptureServiceChannel"
        const val NOTIFICATION_ID = 1
        const val ACTION_INIT_CAPTURE = "com.example.ocr.ACTION_INIT_CAPTURE"
        const val ACTION_HANDLE_CAPTURE = "com.example.ocr.ACTION_HANDLE_CAPTURE"
        const val EXTRA_RESULT_CODE = "result_code"
        const val EXTRA_RESULT_INTENT = "result_intent"
    }

    private lateinit var mediaProjectionManager: MediaProjectionManager
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private var isTextSelectionHandled = false

    override fun onCreate() {
        super.onCreate()
//        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        createNotificationChannel()
        startFfs(NOTIFICATION_ID, createNotification())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Screen Capture Service"
            val descriptionText = "Channel for screen capture service notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {
//            ACTION_INIT_CAPTURE -> initScreenCapture()
//            ACTION_HANDLE_CAPTURE -> handleScreenCapture(intent)
        }
        return START_NOT_STICKY
    }

    private fun startFfs(notificationId : Int,notification: Notification){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                notificationId,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
            )
        } else {
            startForeground(notificationId, notification)
        }
    }

    private fun createNotification(): Notification {


        val intent = Intent(this, ScreenCaptureActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Easy Copy")
            .setContentText("Copy Made Simple!")
            .setSmallIcon(R.drawable.baseline_document_scanner_24)
            .setColor(ContextCompat.getColor(this, R.color.blue))
            .setStyle(NotificationCompat.BigTextStyle().bigText("Copy Made Simple!"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setOngoing(true)
            .addAction(R.drawable.baseline_document_scanner_24, "Capture Screen", pendingIntent)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun handleScreenCapture(intent: Intent) {
        val resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, 0)
        val data = intent.getParcelableExtra<Intent>(EXTRA_RESULT_INTENT)
        data?.let {
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data)
            mediaProjection?.registerCallback(object : MediaProjection.Callback() {
                override fun onStop() {
                    virtualDisplay?.release()
                    imageReader?.close()
                    stopSelf()
                }
            }, null)
            setupVirtualDisplay()
        }
    }

    private fun setupVirtualDisplay() {
        val metrics = resources.displayMetrics
        val density = metrics.densityDpi
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)
        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "ScreenCapture",
            width,
            height,
            density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface,
            null,
            null
        )

        imageReader?.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage()
            image?.use {
                if (!isTextSelectionHandled) {
                    isTextSelectionHandled = true

                    val planes = it.planes
                    val buffer = planes[0].buffer
                    val pixelStride = planes[0].pixelStride
                    val rowStride = planes[0].rowStride
                    val rowPadding = rowStride - pixelStride * width

                    val bitmap = Bitmap.createBitmap(
                        width + rowPadding / pixelStride,
                        height,
                        Bitmap.Config.ARGB_8888
                    )
                    bitmap.copyPixelsFromBuffer(buffer)

                    val uri: Uri? = saveBitmapToFile(bitmap, applicationContext)
                    uri?.let {
                        val resultIntent = Intent(this, TextSelectionActivity::class.java).apply {
                            putExtra(TextSelectionActivity.CAPTURED_IMAGE_URI, it.toString())
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        startActivity(resultIntent)
                    }
                }
            }
        }, null)
    }



    private fun saveBitmapToFile(bitmap: Bitmap, context: Context): Uri? {
        val filename = "captured_image_${System.currentTimeMillis()}.png"
        val file = File(context.cacheDir, filename)
        return try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
                Uri.fromFile(file)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun releaseResources() {
        virtualDisplay?.release()
        virtualDisplay = null

        imageReader?.close()
        imageReader = null

//        mediaProjection?.unregisterCallback(mediaProjectionCallback)
        mediaProjection?.stop()
        mediaProjection = null

        isTextSelectionHandled = false
    }

    override fun onDestroy() {
        releaseResources()
        stopForeground(true)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}