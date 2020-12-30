package android.support.core.functional

import android.support.core.design.internal.MenuStackNavigator
import android.support.core.design.widget.MenuHostFragment
import android.support.core.extensions.findChildVisible
import androidx.fragment.app.Fragment

interface Backable {
    fun onBackPressed(): Boolean {
        if (this !is Fragment) return false
        val child = findChildVisible()
        if (child is Backable && child.onBackPressed()) return true
        if (this is MenuHostFragment && navController!!.navigator is MenuStackNavigator)
            return if (navController!!.startDestination != navController!!.navigator.currentDestinationId)//if is start destination then not navigateUp
                navController!!.navigateUp()
            else false
        return false
    }
}
