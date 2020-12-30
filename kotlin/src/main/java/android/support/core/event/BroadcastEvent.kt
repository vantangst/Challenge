package android.support.core.event

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Parcelable
import java.io.Serializable
import java.util.*

@Suppress("UNCHECKED_CAST")
open class BroadcastEvent<T : Any>(
    private val context: Context,
    private val onEvent: ((T?) -> Unit)? = null
) : ForwardEvent<T, BroadcastReceiver>() {
    var filter: String = "${BroadcastReceiver::class.java.name}.${UUID.randomUUID()}"
    var key = BroadcastReceiver::class.java.name
    var convertToResult: ((Intent) -> T?)? = null

    var value: T? = null
        set (value) {
            field = value
            context.sendBroadcast(Intent(filter).apply {
                when (value) {
                    is Serializable -> putExtra(key, value)
                    is Int -> putExtra(key, value)
                    is String -> putExtra(key, value)
                    is Float -> putExtra(key, value)
                    is Boolean -> putExtra(key, value)
                    is Parcelable -> putExtra(key, value)
                }
            })
        }

    fun call() {
        value = null
    }

    override fun registry(notify: ForwardEvent.Notify<T?>) = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val value = convertToResult?.invoke(intent) ?: intent.let { it.extras?.get(key) as? T }
            onEvent?.invoke(value)
            notify.call(value)
        }
    }.apply { context.registerReceiver(this, IntentFilter(filter)) }

    override fun unRegistry(event: BroadcastReceiver) = context.unregisterReceiver(event)

}