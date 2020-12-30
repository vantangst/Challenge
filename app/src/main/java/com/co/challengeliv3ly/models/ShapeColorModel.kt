package com.co.challengeliv3ly.models

data class ShapeColorModel(
    val hex: String?,
    val imageUrl: String?
) {
    fun getColor(): String {
        return if (hex.isNullOrEmpty()) imageUrl ?: "" else "#$hex"
    }
}