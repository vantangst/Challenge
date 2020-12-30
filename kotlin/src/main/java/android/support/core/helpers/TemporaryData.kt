package android.support.core.helpers

import androidx.annotation.WorkerThread
import java.util.concurrent.TimeUnit

open class TemporaryData<K, V>(
    private val timeout: Long = 60,
    private val timeUnit: TimeUnit = TimeUnit.SECONDS
) {
    private val mCache = hashMapOf<K, V>()
    private val mTime = hashMapOf<K, Long>()

    private fun isValid(key: K): Boolean {
        if (!hasContent(key)) return false
        return System.currentTimeMillis() - (mTime[key] ?: 0) < timeUnit.toMillis(timeout)
    }

    @WorkerThread
    operator fun get(key: K): V? {
        return if (isValid(key)) loadFromCache(key) else null
    }

    @WorkerThread
    operator fun set(key: K, value: V) {
        saveToCache(key, value)
        mTime[key] = System.currentTimeMillis()
    }

    @WorkerThread
    protected open fun saveToCache(key: K, value: V) {
        mCache[key] = value
    }

    @WorkerThread
    protected open fun loadFromCache(key: K): V? {
        return mCache[key]
    }

    @WorkerThread
    protected open fun hasContent(key: K) = mCache.containsKey(key)

    @WorkerThread
    fun getOrLoad(key: K, function: () -> V) =
        this[key] ?: function().apply { this@TemporaryData[key] = this }
}
