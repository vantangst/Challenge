package com.co.challengeliv3ly.extensions

import android.support.core.helpers.RequestBodyBuilder
import android.support.core.utils.FileUtils
import com.co.challengeliv3ly.app.network.ApiResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException


private val <T> Response<T>.errorBodyMessage: String?
    get() = errorBody()?.string()?.toObject<ApiResponse<Any>>()?.message

fun <T> Call<ApiResponse<T>>.call(): T {
    val response = try {
        execute()
    } catch (e: SocketTimeoutException) {
        throw TimeoutException()
    } catch (e: Throwable) {
        throw LocalIOException(e)
    }

    if (!response.isSuccessful) {
        handleResponseError(response)
    }
    val result = response.body() ?: throw NetworkException("Body null")
    return if (result.result) {
        result.data
    } else {
        throw ApiException(result.message)
    }
}

fun <T> Call<T>.callRaw(): T {
    val response = try {
        execute()
    } catch (e: SocketTimeoutException) {
        throw TimeoutException()
    } catch (e: Throwable) {
        throw LocalIOException(e)
    }

    if (!response.isSuccessful) {
        handleResponseErrorRaw(response)
    }
    return  response.body() ?: throw NetworkException("Body null")
}

fun <T> handleResponseError(response: Response<ApiResponse<T>>) {
    when (response.code()) {
        400 -> throw ApiException(
            response.errorBodyMessage ?: "The request header does not contain the required authentication code and the client is denied access."
        )
        401 -> throw NotAuthenticatedException(
            response.errorBodyMessage
                ?: "The request header does not contain the required authentication code and the client is denied access."
        )
        402 -> throw PaymentRequiredException(
            response.errorBodyMessage ?: "Payment is required. This code is not yet operational."
        )
        403 -> throw ApiException(response.errorBodyMessage ?: "Server has denied access")
        404 -> throw NotFoundException(
            response.errorBodyMessage
                ?: "This file has been deleted, or has never existed before. Please check the URL."
        )
        408 -> throw RequestTimeOutException(
            response.errorBodyMessage ?: "Too many request"
        )
        429 -> throw ManyRequestException(
            response.errorBodyMessage ?: "The server took too long to process the request"
        )
        442 -> throw ApiException(response.errorBodyMessage ?: "Internal Server Error")
        500 -> throw InternalServerException()
        else -> throw NetworkException(
            (response.errorBodyMessage
                ?: "API response format is incorrect")
        )
    }
}

fun <T> handleResponseErrorRaw(response: Response<T>) {
    when (response.code()) {
        400 -> throw ApiException(
            response.errorBodyMessage ?: "The request header does not contain the required authentication code and the client is denied access."
        )
        401 -> throw NotAuthenticatedException(
            response.errorBodyMessage
                ?: "The request header does not contain the required authentication code and the client is denied access."
        )
        402 -> throw PaymentRequiredException(
            response.errorBodyMessage ?: "Payment is required. This code is not yet operational."
        )
        403 -> throw ApiException(response.errorBodyMessage ?: "Server has denied access")
        404 -> throw NotFoundException(
            response.errorBodyMessage
                ?: "This file has been deleted, or has never existed before. Please check the URL."
        )
        408 -> throw RequestTimeOutException(
            response.errorBodyMessage ?: "Too many request"
        )
        429 -> throw ManyRequestException(
            response.errorBodyMessage ?: "The server took too long to process the request"
        )
        442 -> throw ApiException(response.errorBodyMessage ?: "Internal Server Error")
        500 -> throw InternalServerException()
        else -> throw NetworkException(
            (response.errorBodyMessage
                ?: "API response format is incorrect")
        )
    }
}

fun <T> Call<ApiResponse<T>>.call(function: T.() -> Unit): T {
    return call().apply(function)
}

fun <T> Call<T>.callRaw(function: T.() -> Unit): T {
    return callRaw().apply(function)
}

fun <T> Call<ApiResponse<T>>.tryCall(shouldBeSuccess: Throwable.() -> Boolean): T? {
    return try {
        call()
    } catch (e: Throwable) {
        if (!shouldBeSuccess(e)) throw e else null
    }
}

private fun createValuePart(value: String): RequestBody {
    return value.toRequestBody(MultipartBody.FORM)
}

private fun createValuePart(key: String, value: String) =
    MultipartBody.Part.createFormData(key, value)

fun RequestBodyBuilder.buildMultipart(): Map<String, RequestBody> {
    val multipart = HashMap<String, RequestBody>()
    build().forEach { multipart[it.key] = createValuePart(it.value) }
    return multipart
}

fun createImagePart(field: String, url: String?): MultipartBody.Part? {
    if (url == null) return null
    val file = File(url)
    if (!file.exists()) return null
    val type = FileUtils.getMimeType(file.path) ?: return null
    return MultipartBody.Part.createFormData(
        field, file.name,
        file.asRequestBody(type.toMediaTypeOrNull())
    )
}

fun String.toPart() = createValuePart(this)
fun String.toImagePart(key: String) = createImagePart(key, this)
fun String.toPart(key: String) = createValuePart(key, this)
