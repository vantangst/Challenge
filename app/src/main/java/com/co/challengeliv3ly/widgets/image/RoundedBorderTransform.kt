package com.co.challengeliv3ly.widgets.image

import android.graphics.*


class RoundedBorderTransform(
        private val roundingRadius: Int,
        private val borderSize: Float = 3f,
        private val borderColor: Int = Color.GRAY

) : DrawableTransform(roundingRadius) {
    override val id = "com.co.renting.widgets.image.RoundedBorderTransform"

    override fun onDraw(canvas: Canvas, toTransform: Bitmap) {
        val rect = RectF(
                borderSize / 2,
                borderSize / 2,
                toTransform.width.toFloat() - borderSize / 2,
                toTransform.height.toFloat() - borderSize / 2
        )
        val clipPath = Path()
        clipPath.addRoundRect(rect, roundingRadius.toFloat(), roundingRadius.toFloat(), Path.Direction.CW)
        canvas.clipPath(clipPath)
        canvas.drawBitmap(
                toTransform, null, RectF(
                borderSize, borderSize,
                toTransform.width.toFloat() - borderSize,
                toTransform.height.toFloat() - borderSize
        ), null
        )
        canvas.drawRoundRect(rect, roundingRadius.toFloat(), roundingRadius.toFloat(), Paint()
                .apply {
                    strokeWidth = borderSize
                    color = borderColor
                    style = Paint.Style.STROKE
                    isAntiAlias = true
                })
    }

}