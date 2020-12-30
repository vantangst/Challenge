package com.co.challengeliv3ly.viewmodels

import android.support.core.extensions.load
import androidx.lifecycle.MutableLiveData
import com.co.challengeliv3ly.app.AppViewModel
import com.co.challengeliv3ly.datasource.AppCache
import com.co.challengeliv3ly.models.UserModel

class SplashViewModel(appCache: AppCache) : AppViewModel() {
    val user = MutableLiveData<UserModel>().load { appCache.user }
}
