package com.co.challengeliv3ly.app.network

class ApiResponse<T>(status: Boolean, message: String, val data: T) : BaseApiResponse(status, message)