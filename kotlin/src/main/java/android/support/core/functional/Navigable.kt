package android.support.core.functional

import android.os.Bundle
import androidx.fragment.app.Fragment

private const val KEY_NAVIGATE_ARGS = "android-support-nav:fragment:navigate:args"
private const val KEY_NAVIGATE_CHILD_ID = "android-support-nav:fragment:navigate:childId"

interface Navigable {

    fun handleNavigateArguments(args: Bundle) {
        if (navigateIfNeeded(args, this::navigateTo)) else onNewArguments(args)
    }

    fun onNewArguments(args: Bundle) {
    }

    fun navigateTo(desId: Int, args: Bundle?) {
        throw RuntimeException("Not implement")
    }
}

private fun navigateIfNeeded(arguments: Bundle, function: (childId: Int, navArgs: Bundle?) -> Unit): Boolean {
    if (!arguments.containsKey(KEY_NAVIGATE_CHILD_ID)) return false
    val childId = arguments.getInt(KEY_NAVIGATE_CHILD_ID)
    val navArgs = arguments.getBundle(KEY_NAVIGATE_ARGS)
    function(childId, navArgs)
    arguments.remove(KEY_NAVIGATE_CHILD_ID)
    arguments.remove(KEY_NAVIGATE_ARGS)
    return true
}

fun navigableOptions(childId: Int, navArgs: Bundle?) = Bundle().apply {
    putInt(KEY_NAVIGATE_CHILD_ID, childId)
    putBundle(KEY_NAVIGATE_ARGS, navArgs)
}

fun Fragment.navigateIfNeeded(function: (childId: Int, navArgs: Bundle?) -> Unit): Boolean =
    if (arguments != null) navigateIfNeeded(arguments!!, function) else false
