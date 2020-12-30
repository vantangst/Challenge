package com.co.challengeliv3ly.widgets.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.AttributeSet
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.co.challengeliv3ly.R
import com.co.challengeliv3ly.extensions.loadAttrs

class AppImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LightWeightImageView(context, attrs, defStyleAttr) {
    private var mImgBorderColor: Int = Color.GRAY
    private var mImgBorderSize: Int = 0
    private var mCenterCrop: Boolean = false
    private var mImgRound: Int = 0

    init {
        context.loadAttrs(attrs, R.styleable.AppImageView) {
            val resId = it.getResourceId(R.styleable.AppImageView_imgSrc, 0)
            mImgRound = it.getDimensionPixelSize(R.styleable.AppImageView_imgRound, 0)
            mImgBorderSize = it.getDimensionPixelSize(R.styleable.AppImageView_imgBorderSize, 0)
            mImgBorderColor = it.getColor(R.styleable.AppImageView_imgBorderColor, Color.GRAY)
            mCenterCrop = it.getBoolean(R.styleable.AppImageView_imgCenterCrop, false)
            if (resId != 0) setImageResource(resId)
        }
    }

    override fun onTransformImage(transforms: MutableList<Transformation<Bitmap>>) {
        if (mCenterCrop) transforms.add(CenterCrop())
        if (mImgBorderSize > 0 && mImgRound > 0)
            transforms.add(RoundedBorderTransform(mImgRound, mImgBorderSize.toFloat(), mImgBorderColor))
        else if (mImgRound > 0) transforms.add(RoundedCorners(mImgRound))
    }
}