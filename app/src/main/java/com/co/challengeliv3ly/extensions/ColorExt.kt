package com.co.challengeliv3ly.extensions

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.co.challengeliv3ly.R
import okhttp3.internal.toHexString
import kotlin.random.Random

fun Boolean.lastMessageColor(context: Context): Int {
    return if (this) ContextCompat.getColor(context, R.color.gray)
    else ContextCompat.getColor(context, R.color.black)
}

fun Boolean.getColorSelected(context: Context): Int {
    return if (this) ContextCompat.getColor(context, R.color.colorPrimary)
    else ContextCompat.getColor(context, R.color.gray)
}

fun Context.getColorRandom() = "#${Color.argb(255,
    Random.nextInt(0, 256),
    Random.nextInt(0, 256),
    Random.nextInt(0, 256)
).toHexString()}"