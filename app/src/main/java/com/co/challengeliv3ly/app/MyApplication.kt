package com.co.challengeliv3ly.app

import android.app.Application
import android.support.core.extensions.appModules

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appModules(AppModule::class)
    }
}