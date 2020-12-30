package com.co.challengeliv3ly.interceptor

import android.support.core.di.Inject
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*

@Inject(true)
class LanguageInterceptor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request().newBuilder()
            .header("Lang", Locale.getDefault().language)
            .build()
        return chain.proceed(request)
    }
}
