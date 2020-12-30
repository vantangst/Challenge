package com.co.challengeliv3ly.widgets

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.inputmethod.InputMethodManager

import androidx.appcompat.widget.AppCompatEditText

import java.util.ArrayList
import java.util.Timer
import java.util.TimerTask

class SearchView : AppCompatEditText {
    private val mTextChangedListeners = ArrayList<TextWatcher>()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
    }

    override fun addTextChangedListener(watcher: TextWatcher?) {
        super.addTextChangedListener(watcher)
        if (watcher != null) mTextChangedListeners.add(watcher)
    }

    override fun removeTextChangedListener(watcher: TextWatcher?) {
        if (watcher != null) mTextChangedListeners.remove(watcher)
        super.removeTextChangedListener(watcher)
    }

    fun setOnSearchListener(eventListener: (String) -> Unit) {
        addTextChangedListener(object : TextWatcher {
            internal var timer: Timer? = null
            internal var handler = Handler(Looper.getMainLooper())

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (timer != null) timer!!.cancel()
                timer = Timer()
                timer!!.schedule(object : TimerTask() {
                    override fun run() {
                        handler.post { eventListener.invoke(s.toString()) }
                    }
                }, TIME_DELAY)
            }

            override fun afterTextChanged(s: Editable) { }
        })
    }

    fun showKeyboard(context: Context){
        isFocusableInTouchMode = true
        requestFocus()

        val imm: InputMethodManager = context.getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    fun setTextSilent(text: String) {
        for (textChangedListener in mTextChangedListeners) {
            super.removeTextChangedListener(textChangedListener)
        }
        setText(text)
        requestFocus(text.length)
        for (textChangedListener in mTextChangedListeners) {
            super.addTextChangedListener(textChangedListener)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(null)
    }

    companion object {
        private const val TIME_DELAY: Long = 1000
    }
}
