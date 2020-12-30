package com.co.challengeliv3ly.app.network

class LoadMoreResponse<T> {
    var data: MutableList<T>? = null
    val total: Int = 0
    var current_page: Int = 1
    var per_page: Int = 0
}