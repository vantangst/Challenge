package com.co.challengeliv3ly.models.support

import java.io.Serializable

open class TextSelector(private val mText: String) : Selector(), Serializable {

    override fun toString(): String {
        return mText
    }
}
