package com.co.challengeliv3ly.datasource

import android.support.core.di.Inject
import android.support.core.event.SingleLiveEvent

@Inject(true)
class AppEvent {
    val hidePhotoBackground = SingleLiveEvent<Boolean>()
}