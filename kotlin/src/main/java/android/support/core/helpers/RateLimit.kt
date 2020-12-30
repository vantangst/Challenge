package android.support.core.helpers

import android.os.SystemClock

import java.util.concurrent.TimeUnit

class RateLimit<KEY>(timeout: Long, timeUnit: TimeUnit) {
    private val mTimestamps: HashMap<KEY, Long> = HashMap()
    private val mTimeout: Long = timeUnit.toMillis(timeout)

    fun shouldFetch(key: KEY): Boolean {
        val lastFetched = mTimestamps[key]
        val timeNow = now()
        if (lastFetched == null) {
            mTimestamps[key] = timeNow
            return true
        }
        if (timeNow - lastFetched >= mTimeout) {
            mTimestamps[key] = timeNow
            return true
        }
        return false
    }

    private fun now(): Long {
        return SystemClock.uptimeMillis()
    }

    fun reset(key: KEY) {
        mTimestamps.remove(key)
    }
}
