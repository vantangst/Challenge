package com.co.challengeliv3ly.extensions


fun <T> T.duplicate(i: Int) = (0 until i).map { this } as MutableList<T>
fun <T> MutableList<T>.findIndex(function: (T) -> Boolean) = indexOf(find(function))