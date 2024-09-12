package com.example.ocr

import android.graphics.*
import android.graphics.drawable.Drawable

class CanvasBitmapDrawable(val bitmap: Bitmap, private val onInvalidate: (bitmap: Bitmap) -> Unit = {}) : Drawable() {

    private val highlightPaint = Paint().apply {
        color = Color.parseColor("#3399FF")
        style = Paint.Style.FILL
        alpha = 80
    }


    private val dimHighlightPaint = Paint().apply {
        color = Color.parseColor("#8099CCFF")
        style = Paint.Style.FILL
        alpha = 80
    }



    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private val overlayBitmap: Bitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    private val overlayCanvas: Canvas = Canvas(overlayBitmap)
    private val backupBitmap: Bitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    private val backupCanvas: Canvas = Canvas(backupBitmap)

    init {
        backupCanvas.drawBitmap(bitmap, 0f, 0f, null)
        overlayCanvas.drawBitmap(bitmap, 0f, 0f, null)
    }

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(overlayBitmap, 0f, 0f, null)
    }

    override fun setAlpha(alpha: Int) {
        highlightPaint.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        highlightPaint.colorFilter = colorFilter
        invalidateSelf()
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    fun drawHighlight(boundingBox: Rect?) {
        boundingBox?.let {
            overlayCanvas.drawRect(it, highlightPaint)
            updateImage()
        }
    }

    fun drawDimHighlight(boundingBox: Rect?) {
        boundingBox?.let {
            overlayCanvas.drawRect(it, dimHighlightPaint)
            updateImage()
        }
    }



    fun clearHighlight(boundingBox: Rect?) {
        boundingBox?.let {
            val originalSection = Bitmap.createBitmap(backupBitmap, it.left, it.top, it.width(), it.height())
            overlayCanvas.drawBitmap(originalSection, it.left.toFloat(), it.top.toFloat(), null)
            originalSection.recycle()
            overlayCanvas.drawRect(it, dimHighlightPaint)
            updateImage()
        }
    }

    private fun updateImage() {
        invalidateSelf()
        onInvalidate(overlayBitmap)
    }
}
