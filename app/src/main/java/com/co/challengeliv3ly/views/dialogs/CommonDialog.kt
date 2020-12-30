package com.co.challengeliv3ly.views.dialogs

import android.content.Context
import android.support.core.base.BaseDialog
import androidx.annotation.StringRes
import com.co.challengeliv3ly.R
import kotlinx.android.synthetic.main.dialog_common.*

class CommonDialog(context: Context) : BaseDialog(context) {

    var positiveClickListener: (() -> Unit)? = null
    var negativeClickListener: (() -> Unit)? = null


    init {
        setContentView(R.layout.dialog_common)
        requestWidthMatchParent()
        btnNegative.setOnClickListener {
            negativeClickListener?.invoke()
            dismiss()
        }
        btnPositive.setOnClickListener {
            positiveClickListener?.invoke()
            dismiss()
        }
    }

    fun setTitleDialog(@StringRes title: Int) {
        if (title != 0) tvTitle.setText(title)
    }

    fun setTitleDialog(title: String) {
        if (title.isNotEmpty()) tvTitle.text = title
    }

    fun setTextNegative(@StringRes textNegative: Int) {
        if (textNegative != 0) btnNegative.setText(textNegative)
    }

    fun setTextNegative(textNegative: String) {
        if (textNegative.isNotEmpty()) btnNegative.text = textNegative
    }

    fun setTextPositive(@StringRes textPositive: Int) {
        if (textPositive != 0) btnPositive.setText(textPositive)
    }

    fun setTextPositive(textPositive: String) {
        if (textPositive.isNotEmpty()) btnPositive.text = textPositive
    }

    fun setMessageDialog(@StringRes message: Int) {
        if (message != 0) tvMessage.setText(message)
    }

    fun setMessageDialog(message: String) {
        if (message.isNotEmpty()) tvMessage.text = message
    }

    fun show(onCallBack: (() -> Unit)?) {
        positiveClickListener = onCallBack
        super.show()
    }
}