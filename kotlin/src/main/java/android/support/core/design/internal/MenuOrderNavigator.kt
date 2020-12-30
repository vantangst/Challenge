package android.support.core.design.internal

import android.content.Context
import android.content.res.TypedArray
import android.support.R
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import android.util.AttributeSet
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator

@Navigator.Name("fragment")
class MenuOrderNavigator(private val containerId: Int, fragmentManager: FragmentManager)
    : MenuNavigator(fragmentManager) {
    override fun getContainerId() = containerId

    override fun generateTag(fragment: Fragment, destination: MenuNavigator.Destination) = generateTag(destination.id)

    override fun createFragmentIfNeeded(navOptions: NavOptions?, destination: MenuNavigator.Destination): Pair<Fragment, Boolean> {
        val fragment = findFragment(destination.id)
        if (fragment != null) return fragment to false
        return destination.createFragment() to true
    }

    override fun findFragment(destinationId: Int): Fragment? {
        return findFragment(generateTag(destinationId))
    }

    override fun createDestination() = Destination(this)

    override fun setNavigateAnimations(destination: MenuNavigator.Destination, enterAnim: Int, exitAnim: Int, popEnterAnim: Int, popExitAnim: Int) {
        destination as Destination
        if (destination.before(currentDestination)) setCustomAnimations(popEnterAnim, popExitAnim)
        else setCustomAnimations(enterAnim, exitAnim)
    }

    @NavDestination.ClassType(Fragment::class)
    open class Destination(@NonNull fragmentNavigator: Navigator<out MenuNavigator.Destination>) : MenuNavigator.Destination(fragmentNavigator) {
        companion object {
            const val ORDER_NOT_SET = -2
        }

        var order = ORDER_NOT_SET
            private set

        override fun onInflate(context: Context, attrs: AttributeSet) {
            super.onInflate(context, attrs)

            val order = context.obtainStyledAttributes(attrs, R.styleable.Destination)
            this.order = order.getInteger(R.styleable.Destination_navOrder, ORDER_NOT_SET)
            onInflate(order)
            order.recycle()
        }

        protected open fun onInflate(typedArray: TypedArray) {
        }

        fun before(destination: MenuNavigator.Destination?) =
                order - ((destination as? Destination)?.order ?: -1) < 0
    }
}