package com.dicoding.prodswing.ui.custom_view

import android.content.Context
import android.util.AttributeSet
import android.view.View.OnClickListener
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import com.dicoding.prodswing.R
import com.dicoding.prodswing.util.hideKeyboard
import com.google.android.material.button.MaterialButton


class ProgressMaterialButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val button: MaterialButton
    private val progressIndicator: ProgressBar
    private var buttonText: String

    init {
        inflate(context, R.layout.progress_material_button, this)

        button = findViewById(R.id.button)
        progressIndicator = findViewById(R.id.progress_indicator)

        val params = progressIndicator.layoutParams as LayoutParams
        params.addRule(CENTER_IN_PARENT, TRUE)
        progressIndicator.layoutParams = params
        progressIndicator.isVisible = false
        isFocusableInTouchMode = true

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressMaterialButton)
        buttonText = typedArray.getString(R.styleable.ProgressMaterialButton_text).orEmpty()
        button.backgroundTintList = typedArray.getColorStateList(R.styleable.ProgressMaterialButton_backgroundTint) ?: context.getColorStateList(R.color.button_tint)
        button.text = buttonText
        button.minWidth = typedArray.getDimensionPixelSize(R.styleable.ProgressMaterialButton_minWidth, 0)
        button.maxWidth = typedArray.getDimensionPixelSize(R.styleable.ProgressMaterialButton_maxWidth, 0)
        button.cornerRadius = typedArray.getDimensionPixelSize(R.styleable.ProgressMaterialButton_cornerRadius, 0)
        typedArray.recycle()
    }

    fun setClickListener(listener: OnClickListener?) {
        listener?.let {
            val safeClickListener = OnClickListener { view ->
                if (button.isEnabled) {
                    context.hideKeyboard(view)
                    requestFocus()
                    listener.onClick(view)
                }
            }

            setOnClickListener(safeClickListener)
            button.setOnClickListener(safeClickListener)
            progressIndicator.setOnClickListener(safeClickListener)
        }
    }

    fun setLoading(isLoading: Boolean) {
        button.text = if (isLoading) null else buttonText
        progressIndicator.isVisible = isLoading
        button.isEnabled = !isLoading
    }
}