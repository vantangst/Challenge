package com.co.challengeliv3ly.extensions

import android.content.Context
import android.net.Uri
import android.support.core.utils.FileUtils

fun Context.getFilePath(it: Uri) = FileUtils.getPath(this, it)