package android.support.core.design.internal

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.NonNull
import android.support.core.functional.navigableOptions
import androidx.navigation.*

@SuppressLint("RestrictedApi")
class MenuNavController(context: Context) {
    private lateinit var mNavGraph: NavGraph
    private var mNavGraphId: Int = 0
    private var mOnNavigatorChangedListener: ((Int) -> Unit)? = null
    private lateinit var mNavigator: MenuNavigator
    val navigator get() = mNavigator

    val navigatorProvider = object : NavigatorProvider() {
        override fun addNavigator(name: String, navigator: Navigator<out NavDestination>): Navigator<out NavDestination>? {
            if (navigator is MenuNavigator) {
                mNavigator = navigator
                navigator.setOnNavigateChangedListener { mOnNavigatorChangedListener?.invoke(it) }
            }
            return super.addNavigator(name, navigator)
        }
    }
    private val mInflater: NavInflater = NavInflater(context, navigatorProvider)
    val startDestination get() = mNavGraph.startDestination

    init {
        navigatorProvider.addNavigator(NavGraphNavigator(navigatorProvider))
    }

    fun navigate(@IdRes hostId: Int, @IdRes childId: Int, args: Bundle? = null, navOptions: NavOptions? = null) {
        mNavigator.navigate(mNavGraph.findDestination(hostId), navigableOptions(childId, args), navOptions, null)
    }

    fun navigate(@NonNull directions: NavDirections) {
        navigate(directions.actionId, directions.arguments)
    }

    fun navigate(@IdRes id: Int, args: Bundle? = null, navOptions: NavOptions? = null) {
        if (mNavGraphId == 0) throw RuntimeException("Not set fragment manager yet!")
        var desId = id
        var navOpts = navOptions
        val arguments = mNavGraph.findNode(mNavigator.currentDestinationId)?.getAction(id)?.let {
            desId = it.destinationId
            if (navOpts == null) navOpts = it.navOptions
            if (it.defaultArguments != null) Bundle().also { combine ->
                combine.putAll(it.defaultArguments)
                if (args != null) combine.putAll(args)
            } else args
        } ?: args
        navigate(mNavGraph.findDestination(desId), arguments, navOpts)
    }

    fun navigate(destination: MenuNavigator.Destination, args: Bundle? = null, navOptions: NavOptions? = null) {
        mNavigator.navigate(destination, args, navOptions, null)
    }

    fun navigateToStart(): Boolean {
        if (startDestination == 0) return false
        if (mNavigator.currentDestinationId == startDestination) return false
        mNavigator.navigate(mNavGraph.findDestination(startDestination), null, null, null)
        return true
    }

    fun navigateUp() = mNavigator.popBackStack()

    fun setOnNavigateChangeListener(function: (Int) -> Unit) {
        mOnNavigatorChangedListener = function
    }

    fun getDestinationActivated(activated: Boolean): MenuNavigator.Destination {
        return if (!activated) mNavGraph.findDestination(startDestination)
        else mNavGraph.find { it.id != startDestination } as MenuNavigator.Destination
    }

    fun getActivated(@IdRes desId: Int) = startDestination != desId

    fun getDestinationCount(): Int {
        return mNavGraph.count()
    }

    fun setGraph(graphResId: Int) {
        mNavGraphId = graphResId
        mNavGraph = mInflater.inflate(graphResId)
        mNavigator.navGraph = mNavGraph
    }

    fun restoreState(state: Bundle?) {
        if (state == null) return
        mNavGraphId = state.getInt(KEY_GRAPH_ID, 0)
        if (mNavGraphId != 0) setGraph(mNavGraphId)
        mNavigator.onRestoreState(state)
    }

    fun saveState(): Bundle? {
        val state = Bundle()
        if (mNavGraphId != 0) state.putInt(KEY_GRAPH_ID, mNavGraphId)
        state.putAll(mNavigator.onSaveState())
        return state
    }

    companion object {
        fun animOptions() = NavOptions.Builder()
            .setEnterAnim(android.support.R.anim.default_fade_in)
            .setExitAnim(android.support.R.anim.default_fade_out)
            .setPopEnterAnim(android.support.R.anim.default_fade_in)
            .setPopExitAnim(android.support.R.anim.default_fade_out)
            .build()

        private const val KEY_GRAPH_ID = "android-support-nav:controller:graphId"
    }

}