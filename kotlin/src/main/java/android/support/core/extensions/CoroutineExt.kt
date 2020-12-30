package android.support.core.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T> withIO(block: suspend CoroutineScope.() -> T) = withContext(Dispatchers.IO, block)

suspend fun <T> withMain(block: suspend CoroutineScope.() -> T) = withContext(Dispatchers.Main, block)

suspend fun <T> withDefault(block: suspend CoroutineScope.() -> T) = withContext(Dispatchers.Default, block)