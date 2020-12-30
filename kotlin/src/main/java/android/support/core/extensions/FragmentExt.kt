package android.support.core.extensions

import android.os.Bundle
import android.support.core.design.widget.MenuHostFragment
import androidx.fragment.app.Fragment
import java.io.Serializable

fun Fragment.findMenuNavController() = MenuHostFragment.findNavController(this)!!

private fun Fragment.isParentVisible(): Boolean {
    var parent = parentFragment
    while (parent != null) {
        if (parent.isHidden) return false
        parent = parent.parentFragment
    }
    return true
}

fun Fragment.isVisibleInParent() = !isHidden && userVisibleHint

fun Fragment.isVisibleOnScreen() = isVisibleInParent() && isParentVisible()

fun Fragment.dispatchHidden(hidden: Boolean) = findChildVisible()?.onHiddenChanged(hidden)

fun Fragment.findChildVisible(): Fragment? {
    var childVisible = childFragmentManager.primaryNavigationFragment
    if (childVisible == null) {
        childVisible = childFragmentManager.fragments.find { it.isVisibleInParent() }
    }
    return childVisible
}

fun Fragment.addArgs(newArgs: Bundle) {
    var args = arguments
    if (args == null) args = Bundle()
    args.putAll(newArgs)
    arguments = args
}

fun Fragment.addArgs(vararg newArgs: Pair<String, Serializable>) {
    var args = arguments
    if (args == null) args = Bundle()
    newArgs.forEach { args.putSerializable(it.first, it.second) }
    arguments = args
}