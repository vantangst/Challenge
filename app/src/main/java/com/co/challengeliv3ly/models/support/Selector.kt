package com.co.challengeliv3ly.models.support

open class Selector {
    @Transient
    var isSelected: Boolean = false

    init {
        isSelected = false
    }

    fun toggle() {
        isSelected = !isSelected
    }
}