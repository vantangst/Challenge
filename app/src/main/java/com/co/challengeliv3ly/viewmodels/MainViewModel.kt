package com.co.challengeliv3ly.viewmodels

import android.support.core.event.SingleLiveEvent
import android.support.core.extensions.map
import com.co.challengeliv3ly.app.AppViewModel
import com.co.challengeliv3ly.repositories.MainRepository

class MainViewModel(mainRepository: MainRepository) : AppViewModel() {
    val getShapeColor = SingleLiveEvent<Int>()
    val getShapeColorSuccess = getShapeColor.map(this, refreshLoading) {
        mainRepository.getCircleColor(it!!)
    }

}