package android.support.core.event

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import android.support.core.base.BaseFragment
import androidx.fragment.app.Fragment

abstract class ForwardEvent<T : Any, K : Any> {
    private val mOwners = hashMapOf<LifecycleOwner, MutableMap<(T?) -> Unit, Notify<*>>>()

    open fun observe(owner: LifecycleOwner, function: (T?) -> Unit) {
        if (!mOwners.containsKey(owner)) {
            mOwners[owner] = hashMapOf()
        }
        mOwners[owner]!![function] = Notify(owner, function).apply {
            val event = registry(this)
            onDestroy = {
                unRegistry(event)
                mOwners.remove(owner)
            }
        }
    }

    protected abstract fun registry(notify: Notify<T?>): K

    protected abstract fun unRegistry(event: K)

    class Notify<T>(owner: LifecycleOwner, private val function: (T?) -> Unit) {
        private var isCalled: Boolean = false
        private var mValue: T? = null
        internal var onDestroy: (() -> Unit)? = null
        private var mLifecycle = when (owner) {
            is BaseFragment -> owner.viewLife
            is Fragment -> owner.viewLifecycleOwner
            else -> owner
        }.lifecycle

        init {
            mLifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun onDestroy() {
                    mLifecycle.removeObserver(this)
                    onDestroy?.invoke()
                }

                @OnLifecycleEvent(Lifecycle.Event.ON_START)
                fun onStart() {
                    if (isCalled) {
                        function(mValue)
                        synchronized(isCalled) {
                            isCalled = false
                            mValue = null
                        }
                    }
                }
            })
        }

        fun call(value: T?) {
            if (mLifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                function(value)
            } else {
                synchronized(isCalled) {
                    isCalled = true
                    mValue = value
                }
            }
        }
    }
}


