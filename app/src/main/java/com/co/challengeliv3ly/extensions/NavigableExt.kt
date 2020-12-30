package com.co.challengeliv3ly.extensions

import android.os.Bundle
import androidx.annotation.IdRes
import android.support.core.extensions.findMenuNavController
import android.support.core.functional.Navigable
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions

private fun Navigable.requireFragment() {
    if (this !is Fragment) throw RuntimeException("${this.javaClass.simpleName} should be fragment")
}

fun Navigable.navigate(@IdRes id: Int, args: Bundle? = null, navOptions: NavOptions? = null) {
    requireFragment()
    (this as Fragment).findMenuNavController().navigate(id, args, navOptions)
}

fun Navigable.animNavigate(@IdRes id: Int, args: Bundle? = null, options: NavOptions? = null) {
    requireFragment()
    val navOptionsBuilder = NavOptions.Builder()
        .setEnterAnim(android.support.R.anim.default_fade_in)
        .setExitAnim(android.support.R.anim.default_fade_out)
        .setPopEnterAnim(android.support.R.anim.default_fade_in)
        .setPopExitAnim(android.support.R.anim.default_fade_out)
    if (options != null) {
        navOptionsBuilder.setPopUpTo(options.popUpTo, options.isPopUpToInclusive)
            .setLaunchSingleTop(options.shouldLaunchSingleTop())
    }
    (this as Fragment).findMenuNavController().navigate(id, args, navOptionsBuilder.build())
}

fun Navigable.navigateToStart(): Boolean {
    requireFragment()
    return (this as Fragment).findMenuNavController().navigateToStart()
}

fun Navigable.navigateUp(): Boolean {
    requireFragment()
    return (this as Fragment).findMenuNavController().navigateUp()
}