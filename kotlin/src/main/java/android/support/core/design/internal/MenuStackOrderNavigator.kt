package android.support.core.design.internal

import android.content.res.TypedArray
import android.support.R
import androidx.annotation.NonNull
import android.support.core.design.internal.MenuOrderNavigator.Destination.Companion.ORDER_NOT_SET
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.Navigator

@Navigator.Name("fragment")
class MenuStackOrderNavigator(containerId: Int,
                              fragmentManager: FragmentManager) : MenuStackNavigator(containerId, fragmentManager) {
    override fun onDestinationPushed(destination: MenuNavigator.Destination, wrapper: MenuStackNavigator.DestinationWrapper) {
        wrapper as DestinationWrapper
        destination as Destination
        wrapper.keepInstance = destination.keepInstance
        wrapper.order = destination.order
    }

    override fun createDestination() = Destination(this)

    override fun createDestinationWrapper(id: Int, tag: String) = DestinationWrapper(id, tag)

    override fun setNavigateAnimations(destination: MenuNavigator.Destination, enterAnim: Int, exitAnim: Int, popEnterAnim: Int, popExitAnim: Int) {
        destination as Destination
        if (destination.order == ORDER_NOT_SET || currentDestination == null) {
            super.setNavigateAnimations(destination, enterAnim, exitAnim, popEnterAnim, popExitAnim)
            return
        }
        if (destination.before(currentDestination)) setCustomAnimations(popEnterAnim, popExitAnim)
        else setCustomAnimations(enterAnim, exitAnim)
    }

    override fun findFragment(destinationId: Int): Fragment? {
        val fragment = findFragment(generateTag(destinationId))
        if (fragment != null) return fragment
        return super.findFragment(destinationId)
    }

    override fun generateTag(fragment: Fragment, destination: MenuNavigator.Destination): String {
        destination as Destination
        if (destination.keepInstance) return generateTag(destination.id)
        return super.generateTag(fragment, destination)
    }

    override fun setPopAnimations(current: MenuStackNavigator.DestinationWrapper, target: MenuStackNavigator.DestinationWrapper) {
        current as DestinationWrapper
        target as DestinationWrapper
        if (current.order == ORDER_NOT_SET) {
            super.setPopAnimations(current, target)
            return
        }
        val targetEnter = if (target.animEnter != 0) target.animEnter else current.animEnter
        val targetPopEnter = if (target.animPopEnter != 0) target.animPopEnter else current.animPopEnter
        if (current.before(target)) setCustomAnimations(targetEnter, current.animExit)
        else setCustomAnimations(targetPopEnter, current.animPopExit)
    }

    override fun shouldReuse(destination: MenuNavigator.Destination): Boolean {
        destination as Destination
        return destination.keepInstance
    }

    override fun shouldReuse(destination: MenuStackNavigator.DestinationWrapper): Boolean {
        destination as DestinationWrapper
        return destination.keepInstance
    }

    class DestinationWrapper(destinationId: Int,
                             fragmentTag: String) : MenuStackNavigator.DestinationWrapper(destinationId, fragmentTag) {
        var keepInstance: Boolean = false
        var order = 0

        fun before(destination: DestinationWrapper) =
                order - destination.order < 0
    }

    @NavDestination.ClassType(Fragment::class)
    class Destination(@NonNull fragmentNavigator: Navigator<out MenuNavigator.Destination>) : MenuOrderNavigator.Destination(fragmentNavigator) {

        var keepInstance = false
            private set

        override fun onInflate(typedArray: TypedArray) {
            this.keepInstance = typedArray.getBoolean(R.styleable.Destination_navKeepInstance, false)
        }
    }
}
