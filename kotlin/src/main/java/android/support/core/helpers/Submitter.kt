package android.support.core.helpers

import androidx.lifecycle.MutableLiveData
import android.support.core.base.BaseViewModel
import android.support.core.event.SingleLiveEvent
import android.support.core.extensions.map
import kotlinx.coroutines.CoroutineScope

@Suppress("UNCHECKED_CAST")
abstract class Submitter<T> {
    @Transient
    private val mSubmit = MutableLiveData<T>()

    fun submit() {
        mSubmit.value = this as T
    }

    fun submit(function: T.() -> Unit) {
        function(this as T)
        mSubmit.value = this
    }

    fun <V> map(
        viewModel: BaseViewModel,
        loading: MutableLiveData<Boolean>? = viewModel.loading,
        error: SingleLiveEvent<out Throwable>? = viewModel.error,
        function: suspend CoroutineScope.(T) -> V?
    ) = mSubmit.map(viewModel, loading, error) { function(it!!) }
}
