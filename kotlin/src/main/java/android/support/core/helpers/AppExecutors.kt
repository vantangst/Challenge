package android.support.core.helpers

import androidx.lifecycle.MutableLiveData
import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AppExecutors {
    companion object {
        val sInstance by lazy { AppExecutors() }

        fun onNetwork(function: () -> Unit) {
            sInstance.mNetworkIO.execute(function)
        }

        fun onDisk(function: () -> Unit) {
            sInstance.mDiskIO.execute(function)
        }

        fun onUI(function: () -> Unit) {
            sInstance.mMainThread.execute(function)
        }

        fun <T> loadInBackGround(function: () -> T): Concurrent<T> {
            return sInstance.doInBackground(function)
        }
    }

    private val mDiskIO = Executors.newSingleThreadExecutor()
    private val mNetworkIO = Executors.newFixedThreadPool(3)
    private val mMainThread = MainThreadExecutor()

    fun diskIO(): ExecutorService {
        return mDiskIO
    }

    fun networkIO(): ExecutorService {
        return mNetworkIO
    }

    fun mainThread(): MainThreadExecutor {
        return mMainThread
    }

    fun <T> doInBackground(function: () -> T) = Concurrent(this, function)

    fun load(function: () -> Unit) = Loader(this, function)
}

class MainThreadExecutor : Executor {
    private val mHandler = Handler(Looper.getMainLooper())

    override fun execute(runnable: Runnable) {
        mHandler.post(runnable)
    }
}

@Suppress("unchecked_cast")
class Concurrent<T>(private val appExecutors: AppExecutors, private val background: () -> T) {
    private var mLoading: MutableLiveData<Boolean>? = null

    fun notifyLoadingTo(loading: MutableLiveData<Boolean>): Concurrent<T> {
        mLoading = loading
        return this
    }

    fun postOnUi(function: (T) -> Unit) {
        mLoading?.postValue(true)
        appExecutors.diskIO().execute {
            val value = background()
            mLoading?.postValue(false)
            appExecutors.mainThread().execute { function(value) }
        }
    }
}

class Loader(private val appExecutors: AppExecutors, private val load: () -> Unit) {
    fun then(function: () -> Unit) {
        appExecutors.diskIO().execute {
            load()
            appExecutors.mainThread().execute(function)
        }
    }
}