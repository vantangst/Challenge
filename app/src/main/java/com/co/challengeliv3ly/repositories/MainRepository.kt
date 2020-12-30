package com.co.challengeliv3ly.repositories

import android.content.Context
import android.support.core.di.Repository
import com.co.challengeliv3ly.datasource.ApiService
import com.co.challengeliv3ly.datasource.AppCache
import com.co.challengeliv3ly.extensions.callRaw
import com.co.challengeliv3ly.models.ShapeColorModel
import com.co.challengeliv3ly.models.ShapeType

class MainRepository(
    private val apiService: ApiService,
    private val appCache: AppCache,
    private val context: Context
) : Repository {

    fun getCircleColor(type: Int): List<ShapeColorModel> {
        return if (type == ShapeType.SQUARE.value) {
            apiService.getSquaresColor().callRaw()
        } else {
            apiService.getCircleColor().callRaw()
        }
    }
}