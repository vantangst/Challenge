package android.support.core.design.widget

import android.annotation.SuppressLint
import android.content.Context
import android.support.core.base.BaseFragment
import android.support.core.functional.MenuOwner
import android.support.core.design.internal.MenuNavController
import android.support.core.design.internal.MenuOrderNavigator
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.NavigationRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.R
import java.util.concurrent.atomic.AtomicBoolean

@Deprecated("Unused")
class MenuHostView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private var mFragmentManager: FragmentManager? = null
    val navController = MenuNavController(context)
    private var mGraphId: Int = 0

    init {
        loadAttrs(attrs)
        setTag(android.support.R.string.nav_menu_controller_view_tag, navController)
        super.setTag(android.support.R.string.nav_menu_controller_view_tag)
    }

    override fun setTag(tag: Any?) {
        throw RuntimeException("Not support set tag for MenuHostView")
    }

    @SuppressLint("CustomViewStyleable")
    private fun loadAttrs(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.NavHost)
        mGraphId = a.getResourceId(R.styleable.NavHost_navGraph, 0)
        a.recycle()
    }

    private fun setGraph(@NavigationRes graphResId: Int) {
        navController.setGraph(graphResId)
    }

    fun setFragmentManager(fragmentManager: FragmentManager) {
        mFragmentManager = fragmentManager
        navController.navigatorProvider.addNavigator(MenuOrderNavigator(id, fragmentManager))
        setGraph(mGraphId)
    }

    fun setupWithView(view: View, fragmentManager: FragmentManager) {
        val options = MenuNavController.animOptions()
        setFragmentManager(fragmentManager)
        if (view is MenuOwner) {
            val shouldAnimation = AtomicBoolean(false)
            view.setOnIdSelectedListener {
                navController.navigate(it, navOptions = if (shouldAnimation.get()) options else null)
            }
            navController.setOnNavigateChangeListener { view.selectId(it) }
            navController.navigate(view.getCurrentId())
        } else {
            if (navController.getDestinationCount() < 2) throw RuntimeException("Navigation graph need 2 fragment to setup")
            val navigate: (Boolean) -> Unit = {
                val destination = navController.getDestinationActivated(view.isActivated)
                if (it) navController.navigate(destination, navOptions = options)
                else navController.navigate(destination)
            }
            navigate(false)
            view.setOnClickListener {
                view.isActivated = !view.isActivated
                navigate(true)
            }
        }
    }

    fun preformResume() {
        (mFragmentManager!!.primaryNavigationFragment as? BaseFragment)?.apply {
            viewLife.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
    }

    fun preformPause() {
        (mFragmentManager!!.primaryNavigationFragment as? BaseFragment)?.apply {
            viewLife.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        }
    }

    companion object {
        fun findNavController(fragment: Fragment): MenuNavController? {
            val view = fragment.view
            if (view != null) {
                val hostView = view.findViewWithTag<MenuHostView>(android.support.R.string.nav_menu_controller_view_tag)
                if (hostView != null) return hostView.navController
            }
            if (fragment.parentFragment != null) return findNavController(fragment.requireParentFragment())
            return null
        }
    }
}