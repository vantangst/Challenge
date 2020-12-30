package android.support.core.design.internal

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.MenuRes
import androidx.appcompat.view.menu.MenuBuilder
import android.view.Menu
import android.view.MenuInflater

@SuppressLint("RestrictedApi")
fun Context.getMenu(@MenuRes id: Int): Menu {
    val menu = MenuBuilder(this)
    MenuInflater(this).inflate(id, menu)
    return menu
}
