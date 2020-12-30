package com.co.challengeliv3ly.interceptor

import android.support.core.di.Inject
import com.co.challengeliv3ly.datasource.AppCache
import okhttp3.Interceptor
import okhttp3.Response

@Inject(true)
class TokenInterceptor(private val appCache: AppCache) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (!appCache.user?.token.isNullOrEmpty()) {
            request = request.newBuilder()
                .header("Authorization", "Bearer ${appCache.user?.token}")
                .build()
        }
        return chain.proceed(request)
    }
}
