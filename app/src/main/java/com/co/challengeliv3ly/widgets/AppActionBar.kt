package com.co.challengeliv3ly.widgets

import android.content.Context
import android.os.Build
import android.support.core.extensions.showKeyboard
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.co.challengeliv3ly.R
import com.co.challengeliv3ly.annotations.ActionBarOptions
import com.co.challengeliv3ly.extensions.hide
import com.co.challengeliv3ly.extensions.setContentView
import com.co.challengeliv3ly.widgets.image.AppImageView
import kotlinx.android.synthetic.main.app_action_bar.view.*


class AppActionBar : ConstraintLayout {
    private var mTitle: String? = null
    private var mShowLeft: Boolean = true
    private var mIsHome: Boolean = false
    private var mShowRight: Boolean = false
    var mBackgroundColor: Int = 0

    private var mDefaultElevation: Float = 0f
    private var mDefaultTranslateZ: Float = 0f

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        this.setContentView(R.layout.app_action_bar)
        saveDefault()
        loadAttrs(attrs)
        setupViews()
    }

    private fun loadAttrs(attrs: AttributeSet?) {
        if (attrs == null) return
        val types = context.obtainStyledAttributes(attrs, R.styleable.AppActionBar)
        mTitle = types.getString(R.styleable.AppActionBar_title)
        mShowLeft = types.getBoolean(R.styleable.AppActionBar_showLeft, false)
        mIsHome = types.getBoolean(R.styleable.AppActionBar_isHome, false)
        mShowRight = types.getBoolean(R.styleable.AppActionBar_showRight, false)
        mBackgroundColor =
            types.getColor(
                R.styleable.AppActionBar_android_background,
                ContextCompat.getColor(context, R.color.white)
            )
        types.recycle()
    }

    private fun saveDefault() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDefaultElevation = elevation
            mDefaultTranslateZ = translationZ
        }
    }

    private fun setupViews() {
//        showElevation(true)
        showTitle(mTitle)
        showLeftButton(mShowLeft)
        setBackgroundColor(mBackgroundColor)
        showRightButton(mShowRight)
    }

    fun setHome (status: Boolean) {
        mIsHome = status
        showLeftButton(mShowLeft)
    }

    fun isHome() : Boolean {
        return mIsHome
    }

    private fun showElevation(b: Boolean) {
        if (b) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                elevation = resources.getDimension(R.dimen.size_10)
                translationY = resources.getDimension(R.dimen.size_10)
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                elevation = mDefaultElevation
                translationZ = mDefaultTranslateZ
            }
        }
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(
//                resources.getDimensionPixelSize(R.dimen.actionBarSize),
//                MeasureSpec.EXACTLY
//        ))
//    }

    fun showTitle(text: String?) {
        if (text == null) {
            hideTitle()
            return
        }
        if (text.length > 25) showTitleLong(text)
        else showTitleShort(text)
    }

    private fun showTitle(text: Int) {
        if (text == 0) {
            hideTitle()
            return
        }
        val textStr = context!!.getString(text)
        if (textStr.length > 25) showTitleLong(text)
        else showTitleShort(text)
    }

    private fun hideTitle() {
        abTitleShort.hide()
        abTitleLong.hide()
    }

    private fun showTitleShort(text: String?) {
        abTitleLong.hide()
        showText(abTitleShort, text)
    }

    private fun showTitleShort(text: Int) {
        abTitleLong.hide()
        showText(abTitleShort, text)
    }

    private fun showTitleLong(text: String?) {
        abTitleShort.hide()
        showText(abTitleLong, text)
    }

    private fun showTitleLong(text: Int) {
        abTitleShort.hide()
        showText(abTitleLong, text)
    }

    private fun showLeftButton(b: Boolean) {
        abLeft.visibility = if (b) View.VISIBLE else View.GONE
        if (mIsHome) {
            abLeft.setImageResource(R.drawable.ic_baseline_menu_white_24)
        } else {
            abLeft.setImageResource(R.drawable.ic_arrow_back_white_24dp)
        }
    }

    private fun showRightButton(b: Boolean) {
        abRight.visibility = if (b) View.VISIBLE else View.GONE
    }

    fun setupWithOptions(actionBarOptions: ActionBarOptions) {
        show(actionBarOptions.visible)
        if (!actionBarOptions.visible) return
        showTitle(actionBarOptions.title)
        mIsHome = actionBarOptions.isHome
        showLeftButton(actionBarOptions.left)
        showRightButton(actionBarOptions.right)
    }

    private fun show(visible: Boolean) {
        visibility = if (visible) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun showText(view: TextView, text: String?) {
        if (text != null && text.isNotEmpty()) {
            view.visibility = View.VISIBLE
            view.text = text
        } else {
            view.visibility = View.GONE
        }
    }

    private fun showText(view: TextView, @StringRes text: Int) {
        if (text != 0) {
            view.visibility = View.VISIBLE
            view.setText(text)
        } else {
            view.visibility = View.GONE
        }
    }

    private fun showText(view: Button, @StringRes text: Int) {
        if (text != 0) {
            view.visibility = View.VISIBLE
            view.setText(text)
        } else {
            view.visibility = View.GONE
        }
    }

    private fun showIcon(view: ImageButton, @DrawableRes icon: Int) {
        if (icon != 0) {
            view.visibility = View.VISIBLE
            view.setImageResource(icon)
        } else {
            view.visibility = View.GONE
        }
    }

    fun setOnLeftClickListener(function: ((View) -> Unit)?) {
        abLeft.setOnClickListener {
            function?.invoke(it)
            context.showKeyboard(false)
        }
    }

    fun setOnRightClickListener(function: ((View) -> Unit)?) {
        abRight.setOnClickListener {
            function?.invoke(it)
            context.showKeyboard(false)
        }
    }

    fun getBtnRight(): AppImageView {
        return abRight
    }
}