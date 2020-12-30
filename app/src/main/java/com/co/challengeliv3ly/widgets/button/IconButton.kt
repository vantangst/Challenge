package com.co.challengeliv3ly.widgets.button

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import androidx.core.view.setPadding
import com.co.challengeliv3ly.R
import com.co.challengeliv3ly.extensions.loadAttrs
import com.co.challengeliv3ly.extensions.setContentView
import com.co.challengeliv3ly.extensions.show
import kotlinx.android.synthetic.main.button_icon.view.*

class IconButton : CardView {

    private var mResId: Int = 0
    private var mTitle: String? = ""
    private var mShowIconRight: Boolean = false
    private var mTitleTextCapAll: Boolean = false
    private var mIconSize: Int = 0
    private var mPaddingItem: Int = 0

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
        setContentView(R.layout.button_icon)
        loadAttrs(attrs)
        setupViews()
    }

    private fun setupViews() {
        setSizeImage()
        setPaddingItem()
        txtTitle.isAllCaps = mTitleTextCapAll
        showIcon()
        setTitle(mTitle)
        mShowIconRight show ivRight
        setOnClickListener { itemCard.performClick() }
    }

    fun setTitle(title:String?){
        txtTitle.text = title
    }

    fun setIcon(url:String){
        url.isNotEmpty() show ivIcon
        ivIcon.setImageUrl(url)
    }

    private fun showIcon() {
        ivIcon.setImageResource(mResId)
        (mResId!=0) show ivIcon
    }

    private fun loadAttrs(attrs: AttributeSet?) {
        if (attrs == null) return
        context.loadAttrs(attrs, R.styleable.IconButton) {
            mResId = it.getResourceId(R.styleable.IconButton_iconSrc, 0)
            mTitle = it.getString(R.styleable.IconButton_buttonTitle)
            mShowIconRight = it.getBoolean(R.styleable.IconButton_showIconRight, true)
            mTitleTextCapAll = it.getBoolean(R.styleable.IconButton_titleTextCapAll, false)
            mIconSize = it.getDimensionPixelSize(R.styleable.IconButton_iconSize, 0)
            mPaddingItem = it.getDimensionPixelSize(R.styleable.IconButton_buttonPadding, 0)
        }
    }

    private fun setSizeImage() {
        if (mIconSize == 0) return
        ivIcon.layoutParams.width = mIconSize
        ivIcon.layoutParams.height = mIconSize
        ivIcon.requestLayout()
        ivRight.layoutParams.width = mIconSize - 7
        ivRight.layoutParams.height = mIconSize - 7
        ivRight.requestLayout()
    }

    private fun setPaddingItem() {
        if (mPaddingItem == 0) return
        itemCard.setPadding(mPaddingItem)
    }

}