package com.co.challengeliv3ly.widgets.button

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.core.extensions.showKeyboard
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.co.challengeliv3ly.R
import com.co.challengeliv3ly.extensions.setContentView
import kotlinx.android.synthetic.main.button_loading.view.*

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    var text: Int
        get() = 0
        set(value) {
            btnLoading.setText(value)
        }
    var isLoading: Boolean
        get() = !btnLoading.isEnabled
        set(value) {
            btnLoading.isEnabled = !value
            progressLoading.visibility = if (value) View.VISIBLE else View.INVISIBLE
        }

    init {
        setContentView(R.layout.button_loading)
        loadAndroidAttrs(attrs, defStyleAttr)
    }

    @SuppressLint("ResourceType")
    private fun loadAndroidAttrs(attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        val typed =
            context.obtainStyledAttributes(attrs, R.styleable.LoadingButton, defStyleAttr, 0)
        btnLoading.setBackgroundResource(
            typed.getResourceId(
                R.styleable.LoadingButton_android_background,
                0
            )
        )
        btnLoading.text = typed.getString(R.styleable.LoadingButton_android_text)
        btnLoading.setTextColor(
            typed.getColor(
                R.styleable.LoadingButton_android_textColor,
                Color.WHITE
            )
        )
        setBackgroundResource(0)
        typed.recycle()
    }

    override fun setOnClickListener(l: OnClickListener?) {
        btnLoading.setOnClickListener {
            l?.onClick(it)
            context.showKeyboard(false)
        }
    }

}