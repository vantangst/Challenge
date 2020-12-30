package com.co.challengeliv3ly.views.dialogs

import android.content.Context
import android.support.core.base.BaseDialog
import androidx.annotation.StringRes
import com.co.challengeliv3ly.R
import kotlinx.android.synthetic.main.dialog_app_info.*

class AppInfoDialog(context: Context) : BaseDialog(context) {
    var onCloseListener: (() -> Unit)? = null

    init {
        setContentView(R.layout.dialog_app_info)
        setCancelable(false)
        btnClose.setOnClickListener {
            onCloseListener?.invoke()
            dismiss()
        }
    }

    fun setTextClose(@StringRes text: Int) {
        btnClose.text = context.getString(text)
    }

    fun title(@StringRes title: Int) {
        tvTitleDialog.text = context.getString(title)
    }

    fun title(title: String) {
        tvTitleDialog.text = title
    }

    fun showWithMessage(@StringRes mess: Int) {
        tvMessageDialog.text = context.getString(mess)
        show()
    }

    fun showWithMessage(mess: String?) {
        if (mess.isNullOrEmpty()) {
            showWithMessage("An error occurred, please try again!")
        } else {
            tvMessageDialog.text = mess
        }

        if (!isShowing) {
            show()
        }
    }
}