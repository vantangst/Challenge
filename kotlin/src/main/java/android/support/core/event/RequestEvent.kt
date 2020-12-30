package android.support.core.event

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

open class RefreshEvent<T>(
    private val owner: LifecycleOwner,
    /**
     * In milliseconds
     */
    private val timeRate: Long = 0
) : MediatorLiveData<T>() {
    private var mOnActivated: (() -> Unit)? = null
    private var mActivated = false
    private var mLastCalled = 0L

    open fun addEvent(event: ForwardEvent<out Any, out Any>, function: ((Any?) -> Unit)? = null) {
        event.observe(owner) {
            if (function != null) function(it) else onEvent(it)
        }
    }

    open fun addEvent(event: LiveData<out Any>, function: ((Any?) -> Unit)? = null) {
        addSource(event) {
            if (function != null) function(it) else onEvent(it)
        }
    }

    /**
     * Listen events (Network, Location,...) and call refresh if needed
     * @param data list of LiveData - Check if data is not set value or not loaded yet
     * then call refresh to notify observers
     */
    fun addEvent(event: ForwardEvent<out Any, out Any>, vararg data: LiveData<out Any?>, function: ((Any?) -> Unit)? = null) {
        event.observe(owner) {
            val shouldRefresh = data.fold(false) { acc, item -> acc || item.value == null }
            if (shouldRefresh) if (function != null) function(it) else onEvent(it)
        }
    }

    protected open fun onEvent(eventValue: Any?) {
        call()
    }

    fun onActivated(function: RefreshEvent<T>.() -> Unit): MediatorLiveData<T> {
        return MediatorLiveData<T>().also { next ->
            next.addSource(this) {
                activate(function)
                next.value = it
            }
        }
    }

    private fun activate(function: RefreshEvent<T>.() -> Unit) {
        synchronized(this) {
            if (!mActivated) {
                mActivated = true
                function(this)
            }
        }
    }

    fun callIfNotActivated() {
        if (!mActivated) call()
    }

    fun call() {
        if (timeRate > 0) {
            val current = System.currentTimeMillis()
            if (current - mLastCalled < timeRate) return
            mLastCalled = current
        }
        value = value
    }
}

class RequestEvent<T>(owner: LifecycleOwner) : RefreshEvent<T>(owner) {
    private var mStoreValue: T? = null

    override fun onEvent(eventValue: Any?) {
        postValue(mStoreValue)
    }

    override fun setValue(value: T?) {
        synchronized(this) { mStoreValue = value }
        super.setValue(value)
    }

    /**
     * Put value into temporary and waiting for event call to forward
     * @param value Value in refreshing
     */
    infix fun put(value: T) {
        mStoreValue = value
    }
}