package android.support.core.extensions

import android.support.core.utils.ClassUtils
import kotlin.reflect.KClass

/**
 * Check annotation exist in object
 */
fun <A : Annotation> Any.hasAnnotation(annotationClass: Class<A>): Boolean {
    return ClassUtils.getAnnotation(this, annotationClass) != null
}

/**
 * Check annotation exist in object
 */
fun <A : Annotation> Any.hasAnnotation(annotationClass: KClass<A>): Boolean {
    return ClassUtils.getAnnotation(this, annotationClass.java) != null
}

/**
 * Check annotation exist in object
 */
inline fun <reified A : Annotation> Any.hasAnnotation(): Boolean {
    return ClassUtils.getAnnotation(this, A::class.java) != null
}

/**
 * Get annotation in object
 */
fun <A : Annotation> Any.getAnnotation(annotationClass: Class<A>): A? {
    return ClassUtils.getAnnotation(this, annotationClass)
}

/**
 * Get annotation in object
 */
fun <A : Annotation> Any.getAnnotation(annotationClass: KClass<A>): A? {
    return ClassUtils.getAnnotation(this, annotationClass.java)
}

/**
 * Get annotation in object
 */
inline fun <reified A : Annotation> Any.getAnnotation(): A? {
    return ClassUtils.getAnnotation(this, A::class.java)
}

/**
 * Get first generic parameter
 */
fun <A> Any.getFirstGenericParameter(): Class<A> {
    return ClassUtils.getFirstGenericParameter<A>(this)
}

/**
 * Get first generic parameter
 */
fun <A> Any.getGenericParameter(index: Int): Class<A> {
    return ClassUtils.getGenericParameter<A>(this, index)
}