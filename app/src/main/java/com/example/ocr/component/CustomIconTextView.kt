package com.example.ocr.component

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.example.ocr.R

class CustomIconTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        applyStyle()
        setupAttributes(attrs)
    }

    private fun applyStyle() {
        // Set clickable, focusable, padding, and default text color
        isClickable = true
        isFocusable = true
        setPadding(16.dpToPx(), 16.dpToPx(), 16.dpToPx(), 16.dpToPx())
        setTextColor(ContextCompat.getColor(context, android.R.color.black))
        compoundDrawablePadding = 16.dpToPx()

        // Apply ripple effect as background
        background = ContextCompat.getDrawable(context, R.drawable.ripple_background)
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomIconTextView,
            0, 0
        ).apply {
            try {
                // Set text from XML
                val text = getString(R.styleable.CustomIconTextView_android_text)
                setText(text)

                // Set drawableStart (icon) from XML
                val drawableStart = getResourceId(R.styleable.CustomIconTextView_android_drawableStart, 0)
                val startDrawable = if (drawableStart != 0) {
                    ContextCompat.getDrawable(context, drawableStart)
                } else {
                    null
                }

                // Set static drawableEnd (icon)
                val endDrawable = ContextCompat.getDrawable(context, R.drawable.ic_forward)

                // Set drawables
                setCompoundDrawablesWithIntrinsicBounds(startDrawable, null, endDrawable, null)
            } finally {
                recycle()
            }
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}
