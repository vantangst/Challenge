package com.co.challengeliv3ly.extensions

import android.graphics.*

fun Canvas.drawRectCorner(left: Float, top: Float, right: Float, bottom: Float, rx: Float, ry: Float, paint: Paint) {
    val path = Path()
    val width = right - left
    val height = bottom - top
    path.moveTo(right, top + ry)
    path.rQuadTo(0f, -ry, -rx, -ry)
    path.rLineTo(-(width - (2 * rx)), 0f)
    path.rQuadTo(-rx, 0f, -rx, ry)
    path.rLineTo(0f, (height - (2 * ry)))
    path.rQuadTo(0f, ry, rx, ry)
    path.rLineTo((width - (2 * rx)), 0f)
    path.rQuadTo(rx, 0f, rx, -ry)
    path.rLineTo(0f, -(height - (2 * ry)))
    path.close()
    drawPath(path, paint)
}

fun Canvas.drawRectTopCorner(left: Float, top: Float, right: Float, bottom: Float, rx: Float, ry: Float, paint: Paint) {
    val path = Path()
    path.moveTo(left, top + ry)
    path.quadTo(left, top + ry / 2, left + rx / 2, top)
    path.lineTo(right - rx, top)
    path.quadTo(right - rx / 2, top, right, top + ry / 2)
    path.lineTo(right, bottom)
    path.lineTo(left, bottom)
    path.lineTo(left, top + ry)
    drawPath(path, paint)
}

fun Canvas.translateToCenter(viewWidth: Int, viewHeight: Int, bitmapWidth: Int, bitmapHeight: Int) {
    val deltaWidth = Math.max(0, bitmapWidth - viewWidth).toFloat()
    val deltaHeight = Math.max(0, bitmapHeight - viewHeight).toFloat()
    translate(-deltaWidth / 2, -deltaHeight / 2)
}

fun Canvas.drawTextCenter(text: String, viewWidth: Int, viewHeight: Int, paint: Paint) {
    val x = viewWidth / 2 - paint.measureText(text) / 2
    val y = viewHeight / 2 - (paint.descent() + paint.ascent()) / 2
    drawText(text, x, y, paint)
}

fun Canvas.drawTextCenter(text: String, bound: RectF, paint: Paint) {
    val textWidth = paint.measureText(text)
    val offsetStart = (bound.left + bound.right) / 2 - textWidth / 2
    val offsetTop = (bound.top + bound.bottom) / 2 - (paint.descent() + paint.ascent()) / 2
    this.drawText(text, offsetStart, offsetTop, paint)
}

fun Canvas.drawTextCenter(text: String, bound: Rect, paint: Paint) {
    val textWidth = paint.measureText(text)
    val offsetStart = (bound.left + bound.right) / 2 - textWidth / 2
    val offsetTop = (bound.top + bound.bottom) / 2 - (paint.descent() + paint.ascent()) / 2
    this.drawText(text, offsetStart, offsetTop, paint)
}

fun Canvas.drawBitmapCircle(bitmap: Bitmap, radius: Int, paint: Paint = Paint().apply {
    isAntiAlias = true
    isFilterBitmap = true
    isDither = true
    color = Color.GRAY
}) {
    drawARGB(0, 0, 0, 0)
    drawCircle(radius.toFloat(), radius.toFloat(), radius.toFloat(), paint)

    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    val src = Rect(0, 0, radius * 2, radius * 2)
    drawBitmap(bitmap, src, src, paint)
}