package com.co.challengeliv3ly.widgets.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.co.challengeliv3ly.R


abstract class LightWeightImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private lateinit var url: String
    private var imageView: ImageView = this

    open fun setImageUrl(url: String) {
        this.url = url
        val builder = Glide.with(this)
        load(
            if (url.isEmpty()) builder.load(R.drawable.box_gray_corner)
            else builder.load(url).fitCenter()
        )
    }

    fun setImageUrl(url: String, onCallBack: (() -> Unit)) {
        Glide.with(this)
            .asBitmap()
            .load(url)
            .fitCenter()
            .error(R.drawable.box_gray_corner)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    imageView.setImageBitmap(resource)
                    onCallBack.invoke()
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    override fun setImageResource(res: Int) {
        load(Glide.with(this).load(res))
    }

    fun setImageBitmapGlide(bm: Bitmap?) {
        load(Glide.with(this).load(bm))
    }

    private fun load(builder: RequestBuilder<Drawable>) {
        var nextBuilder = builder
        nextBuilder = nextBuilder.transition(DrawableTransitionOptions.withCrossFade())
        nextBuilder = transformImage(nextBuilder)
        nextBuilder.into(this)
    }

    private fun transformImage(nextBuilder: RequestBuilder<Drawable>): RequestBuilder<Drawable> {
        val transforms = mutableListOf<Transformation<Bitmap>>()
        onTransformImage(transforms)
        if (transforms.isNotEmpty()) return nextBuilder.transform(*transforms.toTypedArray())
        return nextBuilder
    }

    protected open fun onTransformImage(transforms: MutableList<Transformation<Bitmap>>) {
    }

    open fun setPhotoBitmap(bitmap: Bitmap?) {
        val builder = Glide.with(this)
        if (bitmap == null) return
        load(builder.load(bitmap))
    }

    open fun bitmap(): Bitmap? {
        return if (this::url.isInitialized) BitmapFactory.decodeFile(url)
        else null
    }
}