package com.co.challengeliv3ly.app

import android.support.core.base.BaseViewModel
import android.support.core.event.LoadingEvent

abstract class AppViewModel : BaseViewModel() {

    val customizeLoading = LoadingEvent()
}