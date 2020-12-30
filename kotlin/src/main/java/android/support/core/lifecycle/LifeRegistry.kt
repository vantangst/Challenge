package android.support.core.lifecycle

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class LifeRegistry(provider: LifecycleOwner) : LifecycleRegistry(provider) {

    var isCreated = false
        private set

    private var isUsedToDestroyed = false

    var isReCreated: Boolean = false
        private set

    fun create(): LifeRegistry {
        synchronized(this) {
            handleLifecycleEvent(Event.ON_CREATE)
            isCreated = true
            if (isUsedToDestroyed) isReCreated = true
        }
        return this
    }

    fun start(): LifeRegistry {
        synchronized(this) {
            if (!isCreated) create()
            handleLifecycleEvent(Event.ON_START)
        }
        return this
    }

    fun resume(): LifeRegistry {
        synchronized(this) {
            if (!isCreated) create().start()
            handleLifecycleEvent(Event.ON_RESUME)
        }
        return this
    }

    fun pause(): LifeRegistry {
        synchronized(this) {
            if (currentState == State.RESUMED)
                handleLifecycleEvent(Event.ON_PAUSE)
        }
        return this
    }

    fun stop(): LifeRegistry {
        synchronized(this) {
            isReCreated = false
            when (currentState) {
                State.STARTED ->
                    handleLifecycleEvent(Event.ON_STOP)
                State.RESUMED -> {
                    handleLifecycleEvent(Event.ON_PAUSE)
                    handleLifecycleEvent(Event.ON_STOP)
                }
                else -> {
                }
            }
        }
        return this
    }

    fun destroy(): LifeRegistry {
        synchronized(this) {
            if (!isCreated) return this
            when (currentState) {
                State.CREATED -> {
                    handleLifecycleEvent(Event.ON_DESTROY)
                }
                State.STARTED -> {
                    handleLifecycleEvent(Event.ON_STOP)
                    handleLifecycleEvent(Event.ON_DESTROY)
                }
                State.RESUMED -> {
                    handleLifecycleEvent(Event.ON_PAUSE)
                    handleLifecycleEvent(Event.ON_STOP)
                    handleLifecycleEvent(Event.ON_DESTROY)
                }
                else -> {
                }
            }
            isCreated = false
            isUsedToDestroyed = true
        }
        return this
    }
}