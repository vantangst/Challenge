package android.support.core.design.widget

import android.os.Bundle
import androidx.annotation.NavigationRes
import androidx.annotation.NonNull
import android.support.core.extensions.dispatchHidden
import android.support.core.extensions.isVisibleOnScreen
import android.support.core.functional.Backable
import android.support.core.functional.navigateIfNeeded
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment

internal class SupportNavHostFragment : NavHostFragment(), Backable {

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) navigateIfNeeded()
        dispatchHidden(hidden)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isAdded) onHiddenChanged(!isVisibleToUser)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navigateIfNeeded()
    }

    override fun onStart() {
        super.onStart()
        if (isVisibleOnScreen()) navigateIfNeeded()
    }

    fun navigateIfNeeded() =
        navigateIfNeeded { childId, navArgs ->
            navController.navigate(childId, navArgs, NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(navController.graph.startDestination, false)
                .build())
        }

    companion object {
        private const val KEY_GRAPH_ID = "android-support-nav:fragment:graphId"

        @NonNull
        fun create(@NavigationRes graphResId: Int): NavHostFragment {
            var b: Bundle? = null
            if (graphResId != 0) {
                b = Bundle()
                b.putInt(KEY_GRAPH_ID, graphResId)
            }

            val result = SupportNavHostFragment()
            if (b != null) {
                result.arguments = b
            }
            return result
        }
    }
}