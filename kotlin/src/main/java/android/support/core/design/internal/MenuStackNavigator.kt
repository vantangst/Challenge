package android.support.core.design.internal

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import java.io.Serializable
import java.util.*

@Navigator.Name("fragment")
open class MenuStackNavigator(private val containerId: Int,
                              fragmentManager: FragmentManager) : MenuNavigator(fragmentManager) {
    companion object {
        private const val KEY_STACK = "android:menu:stack"
    }

    private var mStack = Stack<DestinationWrapper>()

    override fun onSaveState(): Bundle? {
        val state = super.onSaveState()!!
        state.putSerializable(KEY_STACK, mStack)
        return state
    }

    @Suppress("unchecked_cast")
    override fun onRestoreState(savedState: Bundle) {
        val stack = savedState.getSerializable(KEY_STACK)
        mStack = if (stack is Stack<*>) stack as Stack<DestinationWrapper>
        else Stack<DestinationWrapper>().apply {
            addAll(stack as ArrayList<DestinationWrapper>)
        }
        super.onRestoreState(savedState)
    }

    override fun getContainerId() = containerId

    override fun findFragment(destinationId: Int): Fragment? {
        var tag = mStack.findLast { it.destinationId == destinationId }?.fragmentTag
        if (tag == null) tag = fragmentManager.findByDestinationId(destinationId)?.tag
                ?: return null
        return findFragment(tag)
    }

    private fun FragmentManager.findByDestinationId(id: Int): Fragment? {
        return fragments.findLast { it.tag?.id == id }
    }

    private val String.id: Int
        get() = split(":").last().toInt()

    override fun instantiate(transaction: FragmentTransaction, destination: MenuNavigator.Destination, navOptions: NavOptions?): Fragment {
        if (navOptions != null && navOptions.popUpTo != -1) mStack.popUntil(navOptions) {
            if (shouldReuse(this)) return@popUntil
            if (destinationId != destination.id || !navOptions.shouldLaunchSingleTop())
                transaction.remove(it)
        }
        return super.instantiate(transaction, destination, navOptions)
    }

    override fun onInstantiated(destination: Destination, navOptions: NavOptions?, fragment: Fragment, tag: String) {
        mStack.push(createDestinationWrapper(destination.id, tag).setOptions(navOptions).apply { onDestinationPushed(destination, this) })
    }

    override fun createFragmentIfNeeded(navOptions: NavOptions?, destination: Destination): Pair<Fragment, Boolean> {
        val isLaunchSingleTop = navOptions != null && navOptions.shouldLaunchSingleTop()
        if (shouldReuse(destination) || isLaunchSingleTop) {
            val fragment = findFragment(destination.id)
            if (fragment != null) return fragment to false
        }
        return destination.createFragment() to true
    }

    protected open fun createDestinationWrapper(id: Int, tag: String) = DestinationWrapper(id, tag)

    protected open fun onDestinationPushed(destination: Destination, wrapper: DestinationWrapper) {}

    open fun shouldReuse(destination: Destination) = false

    open fun shouldReuse(destination: DestinationWrapper) = false

    override fun generateTag(fragment: Fragment, destination: Destination): String {
        return "android:switcher:${fragment.javaClass.simpleName}:${System.currentTimeMillis()}:${destination.id}"
    }

    private fun Stack<DestinationWrapper>.popUntil(
            navOptions: NavOptions,
            function: DestinationWrapper.(Fragment) -> Unit) {
        val accept = {
            val top = pop()
            function(top, findFragment(top.fragmentTag)!!)
        }
        while (true) {
            if (empty()) return
            val element = lastElement()
            if (element.destinationId == navOptions.popUpTo) {
                if (navOptions.isPopUpToInclusive) accept()
                return
            }
            accept()
        }
    }

    override fun popBackStack(): Boolean {
        if (mStack.isEmpty()) return false
        val current = mStack.pop() ?: return false
        if (mStack.isEmpty()) return false
        val target = mStack.lastElement()

        transaction {
            setPopAnimations(current, target)
            val currentFragment = findFragment(current.fragmentTag)!!
            if (shouldReuse(current)) hide(currentFragment)
            else remove(currentFragment)
            show(findFragment(target.fragmentTag)!!)
        }
        notifyNavigateChanged(target.destinationId)
        return true
    }

    override fun setNavigateAnimations(destination: Destination, enterAnim: Int, exitAnim: Int, popEnterAnim: Int, popExitAnim: Int) {
        if (!mStack.isEmpty()) {
            var lastExit = mStack.lastElement().animExit
            if (lastExit == 0) lastExit = exitAnim
            setCustomAnimations(enterAnim, lastExit)
        } else setCustomAnimations(enterAnim, exitAnim)
    }

    protected open fun setPopAnimations(current: DestinationWrapper, target: DestinationWrapper) {
        val targetPopEnter = if (target.animPopEnter != 0) target.animPopEnter else current.animPopEnter
        setCustomAnimations(targetPopEnter, current.animPopExit)
    }

    open class DestinationWrapper(val destinationId: Int,
                                  val fragmentTag: String) : Serializable {
        var animEnter: Int = 0
            private set
        var animExit: Int = 0
            private set
        var animPopEnter: Int = 0
            private set
        var animPopExit: Int = 0
            private set

        fun setOptions(value: NavOptions?): DestinationWrapper {
            if (value == null) {
                animEnter = 0
                animExit = 0
                animPopEnter = 0
                animPopExit = 0
                return this
            }
            animEnter = if (value.enterAnim == -1) 0 else value.enterAnim
            animExit = if (value.exitAnim == -1) 0 else value.exitAnim
            animPopEnter = if (value.popEnterAnim == -1) 0 else value.popEnterAnim
            animPopExit = if (value.popEnterAnim == -1) 0 else value.popExitAnim
            return this
        }

        fun getNavOptions(): NavOptions {
            return NavOptions.Builder()
                    .setEnterAnim(animEnter)
                    .setExitAnim(animExit)
                    .setPopEnterAnim(animPopEnter)
                    .setPopExitAnim(animPopExit)
                    .build()
        }
    }
}