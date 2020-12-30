package com.co.challengeliv3ly.extensions

import com.co.challengeliv3ly.BuildConfig

fun String.formatJoined(): String {
    return "Joined in $this"
}


fun Int.formatTime(): String {
    return if (this < 10) "0$this"
    else "$this"
}

fun String?.formatImage(): String {
    return if (this.isNullOrEmpty()) "" else "${BuildConfig.ROOT_PHOTO}$this"
}

fun String.formatCost(): String {
    return "Your cost is $$this"
}

fun String.removeSpace(): String {
    return this.replace("-", "").replace("(", "").replace(")", "").replace(" ", "")
}

fun String.formatContacts(): String {
    return this.substring(1, this.length - 1).removeSpace()
}