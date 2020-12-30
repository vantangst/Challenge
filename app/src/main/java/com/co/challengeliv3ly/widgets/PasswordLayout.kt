package com.co.challengeliv3ly.widgets

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.Gravity
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import com.co.challengeliv3ly.R
import com.co.challengeliv3ly.extensions.getAppResourceId
import com.co.challengeliv3ly.extensions.loadAttrs

class PasswordLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val backgroundStateList get() = background as GradientDrawable
    private var mIconColor: Int = Color.BLACK
    private lateinit var mIcon: ImageButton
    private val mIconPadding = resources.getDimensionPixelSize(R.dimen.size_5)

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        context.loadAttrs(attrs, R.styleable.PasswordLayout) {
            mIconColor = it.getColor(R.styleable.PasswordLayout_iconColor, Color.BLACK)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        fixLayoutOfChild()
        addToggleView()
        getEditText().showPassword(true).onFocus {
            updateFocused(it)
        }.setBackgroundResource(android.R.color.transparent)
    }

    private fun updateFocused(it: Boolean) {
        backgroundStateList.state = if (it) intArrayOf(android.R.attr.state_focused) else intArrayOf()
    }

    private fun fixLayoutOfChild() {
        (getEditText().layoutParams as LayoutParams).apply {
            width = 0
            weight = 1f
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mIcon.setSize(measuredHeight, measuredHeight)
    }

    private fun addToggleView() {
        mIcon = ImageButton(context).also { view ->
            view.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            view.scaleType = ImageView.ScaleType.CENTER_INSIDE
            view.setImageResource(R.drawable.activator_password)
            view.setBackgroundResource(context.getAppResourceId(R.attr.selectableItemBackgroundBorderless))
            view.setPaddingAll(mIconPadding)
            view.setColorFilter(mIconColor)
            view.setOnClickListener {
                isActivated = !isActivated
                val edtText = getEditText()
                edtText.showPassword(!isActivated)
                    .seekCursorToLast()
                updateFocused(edtText.hasFocus())
            }
        }
        addView(mIcon)
    }

    private fun getEditText() = getChildAt(0) as EditText
}

private fun EditText.onFocus(function: (Boolean) -> Any): EditText {
    setOnFocusChangeListener { _, hasFocus ->
        function(hasFocus)
    }
    return this
}

private fun ImageButton.setPaddingAll(padding: Int) {
    setPadding(padding, padding, padding, padding)
}

private fun ImageButton.setSize(w: Int, h: Int) {
    layoutParams.apply {
        width = w
        height = h
    }
}

private fun EditText.seekCursorToLast(): EditText {
    setSelection(length())
    return this
}

private fun EditText.showPassword(show: Boolean): EditText {
    transformationMethod = if (show) PasswordTransformationMethod.getInstance()
    else HideReturnsTransformationMethod.getInstance()
    return this
}
