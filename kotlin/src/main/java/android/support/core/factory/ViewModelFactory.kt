package android.support.core.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.support.core.di.DependenceContext

@Suppress("UNCHECKED_CAST")
class ViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DependenceContext.sInstance.lookup(modelClass)!!.instance as T
    }

    companion object {
        var sInstance = ViewModelFactory()
    }
}
