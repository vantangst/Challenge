package com.co.challengeliv3ly.extensions

import android.content.Context

object AppConst {
    fun getAppFolder(context: Context): String { return "${context.externalCacheDir?.path}/.woodpeckers"}
    fun getAppPhotoFolder(context: Context): String { return "${context.externalCacheDir?.path}/.woodpeckers/photos"}
    fun getAppVideoThumbnailFolder(context: Context): String { return "${context.externalCacheDir?.path}/.woodpeckers/videos/thumbnails"}
    const val MAX_WIDTH_SIZE = 1600
    const val MAX_HEIGHT_SIZE = 1200

    object Api {
        const val PAGE = 1
        const val NUMBER_ITEM_PAGE = 5
        const val API_VERSION = "/api/"
    }

    object DateTime {
        const val FORMAT_DATE_APP = "MM/dd/yyyy"
        const val FORMAT_DATE_SERVER = "yyyy-MM-dd"

        val FORMAT_DATE_TIME_SERVER = "yyyy-MM-dd HH:mm:ss"
        val FORMAT_DATE_TIME_APP = "HH:mm MM/dd/yyyy"

        val FORMAT_TIME_1 = "HH:mm"
        val FORMAT_TIME_2 = "HH:mm:ss"

        const val FORMAT_DAY_OF_WEEK = "EEE"
        const val FORMAT_MONTH_STRING = "MMM"
    }

    object MAP {
        const val DEFAULT_ZOOM_CAMERA = 14f
    }

    object SHAPE {
        val RANGE_SIZE_PERCENT = 0.1 .. 0.45
    }
}