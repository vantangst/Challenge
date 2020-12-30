package com.co.challengeliv3ly.extensions

import com.co.challengeliv3ly.extensions.AppConst.DateTime.FORMAT_DATE_SERVER
import com.co.challengeliv3ly.extensions.AppConst.DateTime.FORMAT_DATE_TIME_SERVER
import java.text.SimpleDateFormat
import java.util.*

fun String.toDateBirthday(format: String): Int {
    val inputFormat = SimpleDateFormat(FORMAT_DATE_SERVER, Locale.getDefault())
    val outputFormat = SimpleDateFormat(format, Locale.getDefault())
    return try {
        val date = inputFormat.parse(this)
        Integer.parseInt(outputFormat.format(date ?: 0))
    } catch (e: Exception) {
        0
    }
}

fun String.toMillisecond(): Long {
    val inputFormat = SimpleDateFormat(FORMAT_DATE_TIME_SERVER, Locale.getDefault())
    val date = inputFormat.parse(this)
    return date.time
}