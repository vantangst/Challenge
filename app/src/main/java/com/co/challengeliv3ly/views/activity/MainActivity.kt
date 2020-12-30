package com.co.challengeliv3ly.views.activity

import android.os.Bundle
import android.support.core.annotations.LayoutId
import android.support.core.functional.Dispatcher
import android.support.core.functional.close
import android.support.core.functional.open
import android.support.core.functional.openClearTop
import com.co.challengeliv3ly.R
import com.co.challengeliv3ly.app.AppActivity
import com.co.challengeliv3ly.viewmodels.MainViewModel


@LayoutId(R.layout.activity_main)
class MainActivity : AppActivity<MainViewModel>() {
    companion object {
        fun showClose(dispatcher: Dispatcher) {
            dispatcher.open<MainActivity>().close()
        }

        fun showClearTop(dispatcher: Dispatcher) {
            dispatcher.openClearTop<MainActivity>()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}
