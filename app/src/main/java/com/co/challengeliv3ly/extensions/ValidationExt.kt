package com.co.challengeliv3ly.extensions

import java.util.regex.Pattern


fun String.isEmail(): Boolean {
    val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
    val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(this)
    return matcher.matches()
}

fun String.isName(): Boolean {
    val expression = "^[A-Za-z0-9]+(?:[ _-][A-Za-z0-9]+)*\$"
    val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(this)
    return matcher.matches()
}