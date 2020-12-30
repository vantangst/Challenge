package com.co.challengeliv3ly.extensions

import com.google.gson.Gson

inline fun <reified T> String.toObject() = Gson().fromJson(this, T::class.java)!!

fun <E> MutableList<E>.toJson() = Gson().toJson(this)!!