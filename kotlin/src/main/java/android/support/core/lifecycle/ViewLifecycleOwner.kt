package android.support.core.lifecycle

import androidx.lifecycle.LifecycleOwner

class ViewLifecycleOwner : LifecycleOwner {
    private val mRegistry = LifeRegistry(this)

    override fun getLifecycle(): LifeRegistry {
        return mRegistry
    }
}