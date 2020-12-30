package android.support.core.functional

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.support.core.base.BaseActivity
import android.support.core.base.BaseFragment
import android.support.core.extensions.put
import android.support.core.lifecycle.ResultLifecycle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlin.reflect.KClass

interface Dispatcher {
    fun getResultLifecycle(): ResultLifecycle {
        return when (this) {
            is BaseActivity -> resultLife
            is BaseFragment -> resultLife
            else -> throw UnsupportedOperationException("Not support for ${this.javaClass.name}")
        }
    }
}

const val REQUEST_FOR_RESULT_INSTANTLY = 1000

fun Dispatcher.open(
    clazz: KClass<out AppCompatActivity>,
    vararg args: Pair<String, Any>
): Dispatcher {
    when (this) {
        is Fragment -> startActivity(Intent(activity, clazz.java).put(args))
        is Activity -> startActivity(Intent(this, clazz.java).put(args))
        else -> throw IllegalArgumentException("This is not instance of activity or fragment")
    }
    return this
}

fun Dispatcher.openClearTop(
    clazz: KClass<out AppCompatActivity>,
    vararg args: Pair<String, Any>
): Dispatcher {
    when (this) {
        is Fragment -> startActivity(
            Intent(activity, clazz.java)
                .put(args)
        )
        is Activity -> startActivity(
            Intent(this, clazz.java)
                .put(args)
        )
        else -> throw IllegalArgumentException("This is not instance of activity or fragment")
    }
    return this
}

fun Dispatcher.open(clazz: KClass<out Service>): ResultLifecycle {
    when (this) {
        is Fragment -> activity!!.startService(Intent(activity, clazz.java))
        is Activity -> startService(Intent(this, clazz.java))
        else -> throw IllegalArgumentException("This is not instance of activity or fragment")
    }
    return getResultLifecycle()
}

fun Dispatcher.openForResult(
    clazz: KClass<out AppCompatActivity>,
    vararg args: Pair<String, Any>
): ResultLifecycle {
    when (this) {
        is BaseFragment -> startActivityForResult(
            Intent(activity, clazz.java).put(args),
            REQUEST_FOR_RESULT_INSTANTLY
        )
        is BaseActivity -> startActivityForResult(
            Intent(this, clazz.java).put(args),
            REQUEST_FOR_RESULT_INSTANTLY
        )
        else -> throw IllegalArgumentException("This is not instance of BaseActivity or BaseFragment")
    }
    return getResultLifecycle()
}

fun Dispatcher.close(clazz: KClass<out Service>) {
    when (this) {
        is Fragment -> activity!!.stopService(Intent(activity, clazz.java))
        is Activity -> stopService(Intent(this, clazz.java))
    }
}

inline fun <reified T : AppCompatActivity> Dispatcher.open(vararg args: Pair<String, Any>): Dispatcher {
    start(T::class) { put(*args) }
    return this
}

inline fun <reified T : AppCompatActivity> Dispatcher.openClearTop(vararg args: Pair<String, Any>): Dispatcher {
    start(T::class) {
        flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        put(*args)
    }
    return this
}

inline fun <reified T : AppCompatActivity> Dispatcher.openRequest(
    requestId: Int,
    vararg args: Pair<String, Any>
): Dispatcher {
    startForResult(requestId, T::class) { put(*args) }
    return this
}

inline fun <reified T : AppCompatActivity> Dispatcher.openWithIntent(function: Intent.() -> Unit): Dispatcher {
    start(T::class, function)
    return this
}

inline fun <reified T : Activity> Dispatcher.openForResult(vararg args: Pair<String, Any>): ResultLifecycle {
    return openClassForResult(T::class, *args)
}

fun Dispatcher.openClassForResult(
    clazz: KClass<out Activity>,
    vararg args: Pair<String, Any>
): ResultLifecycle {
    startForResult(REQUEST_FOR_RESULT_INSTANTLY, clazz) { put(*args) }
    return getResultLifecycle()
}

fun Dispatcher.openAppInGooglePlay(packageName: String) {
    try {
        start(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
    } catch (abe: android.content.ActivityNotFoundException) {
        start(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            )
        )
    }
}

private fun Dispatcher.start(intent: Intent) {
    when (this) {
        is BaseFragment -> startActivity(intent)
        is BaseActivity -> startActivity(intent)
        else -> throw IllegalArgumentException("This is not instance of Activity or Fragment")
    }
}

inline fun Dispatcher.start(clazz: KClass<out Activity>, function: Intent.() -> Unit) {
    when (this) {
        is Fragment -> startActivity(Intent(activity!!, clazz.java).apply(function))
        is Activity -> startActivity(Intent(this, clazz.java).apply(function))
        else -> throw IllegalArgumentException("This is not instance of Activity or Fragment")
    }
}

fun Dispatcher.startForResult(
    requestId: Int,
    clazz: KClass<out Activity>,
    function: Intent.() -> Unit
) {
    when (this) {
        is Fragment -> startActivityForResult(
            Intent(activity!!, clazz.java).apply(function),
            requestId
        )
        is Activity -> startActivityForResult(Intent(this, clazz.java).apply(function), requestId)
        else -> throw IllegalArgumentException("This is not instance of Activity or Fragment")
    }
}

fun Dispatcher.close(result: Int, vararg args: Pair<String, Any>): Dispatcher {
    val intent = Intent().also { it.put(*args) }
    when (this) {
        is Fragment -> activity!!.close(result, intent)
        is Activity -> close(result, intent)
    }
    return this
}

fun Dispatcher.closeSuccess(vararg args: Pair<String, Any>): Dispatcher {
    close(Activity.RESULT_OK, *args)
    return this
}

fun Dispatcher.closeCancel(vararg args: Pair<String, Any>): Dispatcher {
    close(Activity.RESULT_CANCELED, *args)
    return this
}

fun Activity.close(result: Int, intent: Intent) {
    setResult(result, intent)
    finish()
}

fun Dispatcher.close(): Dispatcher {
    when (this) {
        is Fragment -> activity!!.finish()
        is Activity -> finish()
    }
    return this
}