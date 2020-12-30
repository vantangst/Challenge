package com.co.challengeliv3ly.datasource

import android.content.Context
import android.support.core.di.Inject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.co.challengeliv3ly.models.UserModel
import kotlin.reflect.KClass

@Inject(true)
class AppCache(context: Context) {
    private val mShared = context.getSharedPreferences("test:cache", Context.MODE_PRIVATE)
    private val mParser = Gson()
    private val mCached = HashMap<String, Any?>()
    private val mContext = context

    private inline fun <reified T> get(): T = get(T::class.java.name)
    private inline fun <reified T : Any> put(value: T?, liveData: LiveData<T>? = null) =
        put(T::class.java.name, value, liveData)

    private inline fun <reified T : Any> put(
        key: String,
        value: T?,
        liveData: LiveData<T>? = null
    ) {
        mCached[key] = value
        mShared.edit().putString(key, mParser.toJson(value)).apply()
        if (value != null) (liveData as? MutableLiveData)?.postValue(value)
    }

    private fun <T> set(key: String, value: T?) {
        mShared.edit().putString(key, mParser.toJson(value)).apply()
    }

    private inline fun <reified T> set(value: T?) = set(T::class.java.name, value)

    private inline fun <reified T> get(key: String): T = with(T::class.java) {
        if (mCached[key] != null) return@with mCached[key] as T
        return mParser.fromJson(mShared.getString(key, ""), this)
    }

    fun remove(clazz: KClass<*>) {
        mShared.edit().remove(clazz.java.name).apply()
    }

    fun remove(key: String) {
        mShared.edit().remove(key).apply()
    }

    var user: UserModel? = null
        get() = get()
        set(value) {
            field = value
            put(value)
        }

    var deviceToken: String? = null
        get() = get()
        set(value) {
            field = value
            put(value)
        }

    fun logout() {
        user = null
    }

}

