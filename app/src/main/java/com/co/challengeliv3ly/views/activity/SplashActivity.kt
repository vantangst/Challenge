package com.co.challengeliv3ly.views.activity

import android.os.Bundle
import android.support.core.annotations.LayoutId
import android.support.core.extensions.observe
import com.co.challengeliv3ly.R
import com.co.challengeliv3ly.app.AppActivity
import com.co.challengeliv3ly.viewmodels.SplashViewModel

@LayoutId(R.layout.activity_splash)
class SplashActivity : AppActivity<SplashViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.user.observe(this) {
            mHandler.postDelayed({
                MainActivity.showClose(this)
            }, 500)
        }
    }
}
