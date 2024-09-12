package com.example.ocr.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.example.ocr.R
import com.example.ocr.databinding.CustomSwitchTextViewBinding

class CustomSwitchTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: CustomSwitchTextViewBinding =
        CustomSwitchTextViewBinding.inflate(LayoutInflater.from(context), this)

    init {
        applyStyle()
        setupAttributes(attrs)
    }

    private fun applyStyle() {
        setPadding(5.dpToPx(), 5.dpToPx(), 5.dpToPx(), 5.dpToPx())
        binding.customTextView.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        background = ContextCompat.getDrawable(context, R.drawable.ripple_background)
        binding.customTextView.background =ContextCompat.getDrawable(context, R.drawable.ripple_background)

    }

    private fun setupAttributes(attrs: AttributeSet?) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CustomIconTextView, 0, 0)
            try {
                binding.customTextView.text = typedArray.getString(R.styleable.CustomIconTextView_android_text)

                val drawableStartId = typedArray.getResourceId(R.styleable.CustomIconTextView_android_drawableStart, 0)
                if (drawableStartId != 0) {
                    binding.customTextView.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(context, drawableStartId),
                        null, null, null
                    )
                }
            } finally {
                typedArray.recycle()
            }
        }
    }

    fun setSwitchChecked(checked: Boolean) {
        binding.customSwitch.isChecked = checked
    }

    fun isSwitchChecked(): Boolean = binding.customSwitch.isChecked

    fun setOnSwitchCheckedChangeListener(listener: (Boolean) -> Unit) {
        binding.customSwitch.setOnCheckedChangeListener { _, isChecked ->
            listener(isChecked)
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}