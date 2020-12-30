package com.co.challengeliv3ly.utils

import android.content.Context
import com.co.challengeliv3ly.R
import com.co.challengeliv3ly.extensions.getColorRandom
import com.co.challengeliv3ly.models.ShapeColorModel
import com.co.challengeliv3ly.models.ShapeType

object ResourceValidation {

    fun isColorIsLink(text: String) : Boolean{
        return (text.contains("http"))
    }

    fun getImageResourceAccordingWithShapeType(type: Int) : Int {
        var imageResource = R.drawable.bg_circle_gray
        if (type == ShapeType.TRIANGLE.value) {
            imageResource = R.drawable.ic_triangle
        } else if (type == ShapeType.SQUARE.value) {
            imageResource = R.drawable.bg_squares
        }
        return imageResource
    }

    fun isColorResponseListValid(listColor: List<ShapeColorModel>?) : Boolean {
        return (!listColor.isNullOrEmpty() && listColor.first().getColor().isNotEmpty())
    }

    fun getShapeColor(context: Context, listColor: List<ShapeColorModel>?) : String {
        return if (isColorResponseListValid(listColor)) {
            listColor!!.first().getColor()
        } else {
            context.getColorRandom()
        }
    }
}