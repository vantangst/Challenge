package com.co.challengeliv3ly.extensions

import androidx.annotation.IdRes
import androidx.annotation.StringRes


class AlertException(val resource: Int) : Throwable()

class ResourceException(val resource: Int) : Throwable()

class ViewResourceException(val stringRes: Int, val viewId: Int) : Throwable()

class SnackException(val resource: Int) : Throwable()

class TokenException : Throwable()

class UpdateException(throwable: Throwable, private val payload: Any) : Throwable(throwable) {
    fun <T : Any> get() = payload
}

class LocalIOException(throwable: Throwable) : Throwable(throwable)

class NotAuthenticatedException(message: String) : Throwable(message)

class ApiException(message: String) : Throwable(message)

class NotFoundException(message: String) : Throwable(message)

class PaymentRequiredException(message: String) : Throwable(message)

class RequestTimeOutException(message: String) : Throwable(message)

class ManyRequestException(message: String) : Throwable(message)

open class NetworkException(message: String) : Throwable(message)

class InternalServerException : NetworkException("Error Internal server")

fun fail(@StringRes res: Int): Nothing = throw ResourceException(res)

fun fail(@StringRes res: Int, @IdRes viewId: Int): Nothing = throw ViewResourceException(res, viewId)

fun fail(text: String): Nothing = throw Throwable(text)

fun alert(@StringRes res: Int): Nothing = throw AlertException(res)

fun snack(@StringRes res: Int): Nothing = throw SnackException(res)