@file:Suppress("UNCHECKED_CAST")

package android.support.core.extensions

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.io.Serializable
import kotlin.reflect.KClass

/**
 * Start an Activity for given class T and allow to work on intent with "run" lambda function
 */
fun Fragment.withArguments(vararg arguments: Pair<String, Serializable>): Fragment {
    val bundle = Bundle()
    arguments.forEach { bundle.putSerializable(it.first, it.second) }
    this.arguments = bundle
    return this
}

fun Serializable.asArgument() = javaClass.name to this
fun Parcelable.asArgument() = javaClass.name to this
fun Serializable.asArgument(clazz: KClass<out Serializable>) = clazz.java.name to this
fun Serializable.asArgument(key: String) = key to this
fun Parcelable.asArgument(clazz: KClass<out Parcelable>) = clazz.java.name to this

fun <T : Serializable> MutableList<T>.asArgument(key: String) = key to this

/**
 * Retrieve property from intent
 */
fun <T : Any> FragmentActivity.argument(key: String) = lazy { intent?.extras!![key] as T }

/**
 * Retrieve property with default value from intent
 */
fun <T : Any> FragmentActivity.argument(key: String, defaultValue: T? = null) = lazy {
    intent.extras?.get(key) as? T ?: defaultValue
}

inline fun <reified T : Any> FragmentActivity.argument() =
    lazy { intent?.extras!![T::class.java.name] as T }

inline fun <reified T : Any> FragmentActivity.nullableArguments() =
    lazy { intent?.extras!![T::class.java.name] as? T }

/**
 * Retrieve property from intent
 */
fun <T : Any> Fragment.argument(key: String) = lazy { arguments?.get(key) as T }


/**
 * Retrieve property from intent
 */
inline fun <reified T : Any> Fragment.argument() = lazy { arguments?.get(T::class.java.name) as T }

inline fun <reified T : Any> Fragment.arguments() = arguments?.get(T::class.java.name) as T


/**
 * Retrieve property from intent
 */
inline fun <reified T : Any> Fragment.argument(defaultValue: T) =
    lazy { arguments?.get(T::class.java.name) as? T ?: defaultValue }

/**
 * Create new bundle with key = class name
 */
fun <T : Serializable> T.toBundle(): Bundle {
    val bundle = Bundle()
    bundle.putSerializable(this.javaClass.name, this)
    return bundle
}

/**
 * Create new bundle with key = class name
 */
fun Map<String, Serializable>.toBundle(): Bundle {
    val bundle = Bundle()
    forEach { entry ->
        bundle.putSerializable(entry.key, entry.value)
    }
    return bundle
}

/**
 * Create new bundle with key = class name
 */
fun <T> Pair<String, T>.toBundle(): Bundle {
    val bundle = Bundle()
    when (second) {
        is Serializable -> bundle.putSerializable(first, second as Serializable)
        is Parcelable -> bundle.putParcelable(first, second as Parcelable)
        else -> throw java.lang.RuntimeException("Not support this type ${second.toString()}")
    }
    return bundle
}

/**
 * Retrieve property with default value from intent
 */
fun <T : Any> Fragment.argument(key: String, defaultValue: T? = null) = lazy {
    arguments?.get(key)  as? T ?: defaultValue
}


fun <T : Serializable> Intent.put(value: T): Intent {
    putExtra(value.javaClass.name, value)
    return this
}

fun <T : Any> Intent.put(vararg args: Pair<String, T>): Intent {
    args.forEach {
        when (it.second) {
            is Serializable -> putExtra(it.first, it.second as Serializable)
            is Parcelable -> putExtra(it.first, it.second as Parcelable)
            else -> throw RuntimeException("${it.second.javaClass.name} is not Serializable or Parcelable")
        }
    }
    return this
}

operator fun Pair<String, Serializable>.plus(pair: Pair<String, Serializable>): Map<String, Serializable> {
    return hashMapOf(this, pair)
}

fun <T : Parcelable> Intent.put(value: T): Intent {
    putExtra(value.javaClass.name, value)
    return this
}

fun <T : Serializable> Intent.put(key: String, value: T): Intent {
    putExtra(key, value)
    return this
}

fun <T : Serializable> Intent.put(key: String, value: MutableList<T>): Intent {
    putExtra(key, value as Serializable)
    return this
}


fun <T : Serializable> Intent.put(key: String, value: ArrayList<T>): Intent {
    putExtra(key, value as Serializable)
    return this
}

fun <T : Serializable> Intent.put(keyClass: KClass<*>, value: T): Intent {
    putExtra(keyClass.java.name, value)
    return this
}

fun <T : Any> Intent.get(key: String): T? {
    return extras?.get(key) as? T
}

inline fun <reified T : Any> Intent.get(): T? {
    return extras?.get(T::class.java.name) as? T
}
