package android.support.core.lifecycle

import androidx.lifecycle.*


class LifeRegister(private val registry: LifecycleRegistry) {
    companion object {
        fun of(owner: LifecycleOwner) = LifeRegister(owner.lifecycle as LifecycleRegistry)
    }

    fun onCreate(function: () -> Unit) {
        registry.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun onEvent() {
                function()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                registry.removeObserver(this)
            }
        })
    }

    fun onDestroy(function: () -> Unit) {
        registry.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onEvent() {
                registry.removeObserver(this)
                function()
            }
        })
    }

    fun onStart(function: () -> Unit) {
        registry.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onEvent() {
                function()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                registry.removeObserver(this)
            }
        })
    }

    fun onStop(function: () -> Unit) {
        registry.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onEvent() {
                function()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                registry.removeObserver(this)
            }
        })
    }

    fun onResume(function: () -> Unit) {
        registry.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onEvent() {
                function()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                registry.removeObserver(this)
            }
        })
    }

    fun onPause(function: () -> Unit) {
        registry.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onEvent() {
                function()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                registry.removeObserver(this)
            }
        })
    }
}