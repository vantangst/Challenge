package android.support.core.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Parcelable
import android.support.core.annotations.SharedOf
import android.support.core.factory.ViewModelFactory
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.io.Serializable


private fun Intent.put(args: Array<out Pair<String, Any>>): Intent {
    args.forEach {
        when {
            it.second is Serializable -> putExtra(it.first, it.second as Serializable)
            it.second is Parcelable -> putExtra(it.first, it.second as Parcelable)
            else -> throw RuntimeException("Not support this type ${it.second.javaClass.name}")
        }
    }
    return this
}

inline fun <reified T : ViewModel> Fragment.viewModel(sharedOf: SharedOf) =
    lazy {
        val factory = ViewModelFactory.sInstance
        val provider = when (sharedOf) {
            SharedOf.NONE -> ViewModelProvider(this, factory)
            SharedOf.PARENT -> {
                if (parentFragment != null)
                    ViewModelProvider(parentFragment!!, factory)
                else
                    ViewModelProvider(activity!!, factory)
            }
            else -> ViewModelProvider(activity!!, factory)
        }
        provider.get(T::class.java)
    }

inline fun <reified T : ViewModel> FragmentActivity.viewModel() = lazy {
    val factory = ViewModelFactory.sInstance
    ViewModelProvider(this, factory).get(T::class.java)
}