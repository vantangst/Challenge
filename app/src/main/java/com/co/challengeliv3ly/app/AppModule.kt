package com.co.challengeliv3ly.app

import android.support.core.di.Provide
import android.support.core.factory.TLSSocketFactory
import com.google.gson.GsonBuilder
import com.co.challengeliv3ly.BuildConfig
import com.co.challengeliv3ly.datasource.ApiService
import com.co.challengeliv3ly.interceptor.LanguageInterceptor
import com.co.challengeliv3ly.interceptor.Logger
import com.co.challengeliv3ly.interceptor.LoggingInterceptor
import com.co.challengeliv3ly.interceptor.TokenInterceptor
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppModule {
    @Provide
    fun provideLoggingInterceptor(): LoggingInterceptor = LoggingInterceptor.Builder()
        .loggable(BuildConfig.DEBUG)
        .setLevel(Logger.Level.BODY)
        .log(Platform.INFO)
        .request("Request")
        .response("Response")
        .addHeader("Content-Type", "application/json")
        .addHeader("Accept", "application/json")
        .build()

    @Provide
    fun provideOkHttpClient(
        loggingInterceptor: LoggingInterceptor,
        token: TokenInterceptor
    ): OkHttpClient {
        val tslFactory = TLSSocketFactory()
        return OkHttpClient.Builder()
            .sslSocketFactory(tslFactory, tslFactory.systemDefaultTrustManager())
            .addInterceptor(loggingInterceptor)
            .addInterceptor(token)
            .addInterceptor(LanguageInterceptor())
            .build()
    }

    @Provide
    fun provideGsonConvertFactory() = GsonConverterFactory
        .create(
            GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy-MM-dd hh:mm:ss")
                .create()
        )!!

    @Provide
    fun provideRetrofitBuilder(
        gsonConverterFactory: GsonConverterFactory,
        client: OkHttpClient
    ) = Retrofit.Builder()
        .addConverterFactory(gsonConverterFactory)
        .client(client)!!

    @Provide
    fun provideApiService(retrofitBuilder: Retrofit.Builder) = retrofitBuilder
        .baseUrl(BuildConfig.END_POINT)
        .build()
        .create(ApiService::class.java)!!
}