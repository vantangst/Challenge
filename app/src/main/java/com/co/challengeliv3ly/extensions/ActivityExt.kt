package com.co.challengeliv3ly.extensions

import android.app.Activity
import android.util.DisplayMetrics
import android.util.Range
import kotlin.random.Random

fun Activity.deviceWidth(): Int {
    val displayMetrics = DisplayMetrics()
    this.windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

/**
 * input range of percent of width you want to get (0 until 1)
 * return : width in percent
 */
fun Activity.getDeviceWidthIn(percentRange: ClosedFloatingPointRange<Double>): Int {
    val percent = Random.nextDouble(percentRange.start, percentRange.endInclusive)
    return (percent * this.deviceWidth()).toInt()
}