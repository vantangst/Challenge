package com.co.challengeliv3ly.extensions

import android.content.Context

fun stringResource(context: Context, idString: Int): String {
    return context.getString(idString)
}