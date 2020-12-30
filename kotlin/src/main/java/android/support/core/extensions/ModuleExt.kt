package android.support.core.extensions

import android.app.Application
import android.support.core.di.DependenceContext
import kotlin.reflect.KClass

/**
 * Declare app modules
 */
fun Application.appModules(vararg classes: KClass<*>) {
    DependenceContext.init(this, *classes.map { it.java }.toTypedArray())
}

/**
 * Lookup an instance of type <T>
 * @param <T>   generic return type
 * @return lazy initialize for T
</T> */
inline fun <reified T : Any> inject() =
    lazy { DependenceContext.sInstance.lookup(T::class.java)!!.instance as T }

/**
 * Lookup an instance of type <T>
 * @param <T>   generic return type
 * @return instance of T by get
</T> */
inline fun <reified T : Any> lookup() =
    DependenceContext.sInstance.lookup(T::class.java)!!.instance as T
