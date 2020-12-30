package com.co.challengeliv3ly.models

data class ShapeModel(
    val type: Int,
    var color: String,
    var xPosition: Float,
    var yPosition: Float,
    var size: Int = 10
)

enum class ShapeType (val value: Int) {
    CIRCLE(1), TRIANGLE(2), SQUARE(3)
}