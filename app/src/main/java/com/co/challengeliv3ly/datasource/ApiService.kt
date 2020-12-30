package com.co.challengeliv3ly.datasource

import com.co.challengeliv3ly.extensions.AppConst
import com.co.challengeliv3ly.models.ShapeColorModel
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("${AppConst.Api.API_VERSION}colors/random?format=json")
    fun getCircleColor(): Call<List<ShapeColorModel>>

    @GET("${AppConst.Api.API_VERSION}patterns/random?format=json")
    fun getSquaresColor(): Call<List<ShapeColorModel>>

}