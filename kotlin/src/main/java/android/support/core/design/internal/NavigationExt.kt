package android.support.core.design.internal

import androidx.navigation.NavGraph

fun NavGraph.findStartDestination() = findDestination(startDestination)

fun NavGraph.findDestination(id: Int): MenuNavigator.Destination =
    findNode(id) as MenuNavigator.Destination?
        ?: throw RuntimeException("No fragment destination match id $id ")

