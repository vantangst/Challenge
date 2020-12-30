package com.co.challengeliv3ly.app

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.core.base.BaseDialog
import androidx.fragment.app.Fragment
import com.co.challengeliv3ly.R

open class AppDialog : BaseDialog {
    constructor(context: Context) : super(context, R.style.AppDesign_Dialog)
    constructor(fragment: Fragment) : super(fragment, R.style.AppDesign_Dialog)

    var isSlideShow: Boolean = false
        set(value) {
            field = value
            if (value) window!!.attributes.windowAnimations = R.style.AppDesign_DialogAnimation
        }

    init {
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestFullScreen()
    }

}