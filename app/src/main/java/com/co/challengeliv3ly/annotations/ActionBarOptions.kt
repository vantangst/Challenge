package com.co.challengeliv3ly.annotations

import androidx.annotation.StringRes

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ActionBarOptions(
        @StringRes val title: Int = 0,
        val left: Boolean = true,
        val isHome: Boolean = false,
        val visible: Boolean = true,
        val right: Boolean = false
)