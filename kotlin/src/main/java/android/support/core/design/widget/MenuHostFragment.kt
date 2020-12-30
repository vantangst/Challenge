package android.support.core.design.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.R
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import android.support.core.extensions.addArgs
import android.support.core.extensions.dispatchHidden
import android.support.core.extensions.isVisibleOnScreen
import android.support.core.functional.Backable
import android.support.core.functional.MenuOwner
import android.support.core.functional.navigateIfNeeded
import android.support.core.design.internal.*
import androidx.fragment.app.Fragment
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.navigation.NavOptions

class MenuHostFragment : Fragment(), Backable {
    var navController: MenuNavController? = null
        private set
    private var mOnActivityCreatedListener: (() -> Unit)? = null
    private var mNavOptions: NavOptions? = null

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = MenuNavController(requireContext())
        val navigator = when (arguments?.getInt(KEY_NAVIGATOR_TYPE)) {
            MenuNavigator.Destination.NAV_TYPE_STACK -> MenuStackNavigator(id, childFragmentManager)
            MenuNavigator.Destination.NAV_TYPE_STACK_ORDER -> MenuStackOrderNavigator(id, childFragmentManager)
            else -> MenuOrderNavigator(id, childFragmentManager)
        }

        navController!!.navigatorProvider.addNavigator(navigator)

        var navState: Bundle? = null
        if (savedInstanceState != null) navState = savedInstanceState.getBundle(KEY_NAV_CONTROLLER_STATE)
        if (navState != null) {
            navController!!.restoreState(navState)
        } else {
            val graphId = arguments?.getInt(KEY_GRAPH_ID) ?: 0
            if (graphId != 0) navController!!.setGraph(graphId)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FrameLayout(inflater.context).also { it.id = id }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        check(view is ViewGroup) { "created host view $view is not a ViewGroup" }
        val rootView = if (view.getParent() != null) view.getParent() as View else view
        rootView.setTag(R.string.nav_menu_controller_view_tag, navController!!)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (arguments == null) {
            if (savedInstanceState == null) navController!!.navigateToStart()
            return
        }

        val menuId = requireArguments().getInt(KEY_MENU_ID, 0)
        if (menuId != 0) setMenu(menuId)
        if (savedInstanceState != null) return
        if (navigateIfNeeded()) return
        if (menuId != 0) mOnActivityCreatedListener?.invoke() else navController!!.navigateToStart()
    }

    fun navigateIfNeeded() =
        navigateIfNeeded { childId, navArgs -> navController!!.navigate(childId, navArgs) }

    private fun setMenu(menuId: Int) {
        setupWithView(requireView().rootView.findViewById(menuId))
    }

    override fun onInflate(context: Context, attrs: AttributeSet, savedInstanceState: Bundle?) {
        super.onInflate(context, attrs, savedInstanceState)
        val a = context.obtainStyledAttributes(attrs, R.styleable.NavHost)
        val graphId = a.getResourceId(R.styleable.NavHost_navGraph, 0)
        a.recycle()

        val ta = context.obtainStyledAttributes(attrs, R.styleable.MenuHostFragment)
        val menuId = ta.getResourceId(R.styleable.MenuHostFragment_navMenu, 0)
        val navType = ta.getInt(R.styleable.MenuHostFragment_navType, MenuNavigator.Destination.NAV_TYPE_ORDER)
        ta.recycle()

        val action = context.resources.obtainAttributes(attrs, R.styleable.NavAction)
        mNavOptions = NavOptions.Builder()
            .setEnterAnim(a.getResourceId(R.styleable.NavAction_enterAnim, R.anim.default_fade_in))
            .setExitAnim(a.getResourceId(R.styleable.NavAction_exitAnim, R.anim.default_fade_out))
            .setPopEnterAnim(a.getResourceId(R.styleable.NavAction_popEnterAnim, R.anim.default_fade_in))
            .setPopExitAnim(a.getResourceId(R.styleable.NavAction_popExitAnim, R.anim.default_fade_out))
            .build()
        action.recycle()

        if (graphId != 0) setGraph(graphId, menuId, navType)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBundle(KEY_NAV_CONTROLLER_STATE, navController!!.saveState())
    }

    fun setGraph(@NavigationRes graphResId: Int, @IdRes menuId: Int, navType: Int) {
        if (navController == null) addArgs(
            KEY_GRAPH_ID to graphResId,
            KEY_MENU_ID to menuId,
            KEY_NAVIGATOR_TYPE to navType)
        else navController!!.setGraph(graphResId)
    }

    private fun setupWithView(view: View) {
        if (mNavOptions == null) mNavOptions = MenuNavController.animOptions()
        mNavOptions = wrapNavOptionsIfNeeded(mNavOptions!!)
        if (view is MenuOwner) {
            mOnActivityCreatedListener = { navController!!.navigate(view.getCurrentId()) }
            view.setOnIdSelectedListener { navController!!.navigate(it, navOptions = mNavOptions) }
            navController!!.setOnNavigateChangeListener { view.selectId(it) }
        } else {
            if (navController!!.getDestinationCount() < 2) throw RuntimeException("Navigation graph need 2 fragment to setup")
            mOnActivityCreatedListener = { navController!!.navigateToStart() }
            val toggle = {
                val destination = navController!!.getDestinationActivated(view.isActivated)
                navController!!.navigate(destination, navOptions = mNavOptions)
            }
            view.setOnClickListener {
                view.isActivated = !view.isActivated
                toggle()
            }
            navController!!.setOnNavigateChangeListener {
                view.isActivated = navController!!.getActivated(it)
            }
        }
    }

    private fun wrapNavOptionsIfNeeded(navOptions: NavOptions): NavOptions {
        if (navController!!.navigator !is MenuStackOrderNavigator) return navOptions
        return NavOptions.Builder()
            .setEnterAnim(navOptions.enterAnim)
            .setExitAnim(navOptions.exitAnim)
            .setPopEnterAnim(navOptions.popEnterAnim)
            .setPopExitAnim(navOptions.popExitAnim)
            .setLaunchSingleTop(true)
            .setPopUpTo(navController!!.startDestination, false)
            .build()
    }

    override fun onStart() {
        super.onStart()
        if (isVisibleOnScreen()) navigateIfNeeded()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) navigateIfNeeded()
        dispatchHidden(hidden)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isAdded) onHiddenChanged(!isVisibleToUser)
    }

    companion object {
        fun create(graphResId: Int, menuId: Int, navType: Int): Fragment {
            val fragment = MenuHostFragment()
            fragment.setGraph(graphResId, menuId, navType)
            return fragment
        }

        fun findNavController(fragment: Fragment): MenuNavController? {
            if (fragment is MenuHostFragment) return fragment.navController
            val parent = fragment.parentFragment
            if (parent != null) return findNavController(parent)
            return null
        }

        private const val KEY_GRAPH_ID = "android-support-nav:fragment:graphId"
        private const val KEY_NAVIGATOR_TYPE = "android-support-nav:fragment:navigator:type"
        private const val KEY_MENU_ID = "android-support-nav:fragment:menuId"
        private const val KEY_NAV_CONTROLLER_STATE = "android-support-nav:fragment:navControllerState"
    }
}