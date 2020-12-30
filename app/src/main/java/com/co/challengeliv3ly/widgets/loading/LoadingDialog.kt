package com.co.challengeliv3ly.widgets.loading

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.core.base.BaseDialog
import com.co.challengeliv3ly.R


class LoadingDialog(context: Context) : BaseDialog(context, android.R.style.Theme_Dialog) {

    private var mView: RotateLoadingView? = null

    init {
        setCancelable(false)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_loading)
        mView = findViewById(R.id.spin_loading)
    }

    override fun show() {
        super.show()
        mView!!.start()
    }

    override fun hide() {
        mView!!.stop()
        super.hide()
    }

    override fun dismiss() {
        mView!!.stop()
        super.dismiss()
    }
}
