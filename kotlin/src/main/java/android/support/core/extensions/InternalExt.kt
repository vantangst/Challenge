package android.support.core.extensions

import android.graphics.RectF
import android.util.SparseArray
import java.util.*

infix fun Int.has(bit: Int): Boolean {
    return this and bit == bit
}

fun <E> MutableList<E>.swap(from: Int, to: Int) {
    Collections.swap(this, from, to)
}

fun <T : Any> T.copy(data: Any): T {
    if (!data.javaClass.isInstance(this))
        throw RuntimeException("${this.javaClass.simpleName} not instance of ${data.javaClass.simpleName}")
    data.javaClass.declaredFields.forEach { field ->
        field.isAccessible = true
        field.set(this, field.get(data))
        field.isAccessible = false
    }
    return this
}

fun <T> T.asList() = arrayListOf(this)

fun Any?.otherwise(function: () -> Unit): Unit? {
    return if (this == null) function() else null
}

fun Boolean.isTrue(function: () -> Unit): Unit? {
    return if (this) function() else null
}

fun <E> MutableList<E>?.clone() = this?.map { it } ?: ArrayList()

fun RectF.clone() = RectF(this.left, this.top, this.right, this.bottom)

fun <E> SparseArray<E>.forEach(function: (E) -> Unit) {
    (0 until size()).forEach {
        function(this[keyAt(it)])
    }
}

fun <E> SparseArray<E>.find(function: (E) -> Boolean): E? {
    var founded: E? = null
    for (i in 0 until size()) {
        val value = this[keyAt(i)]
        if (function(value)) {
            founded = value
            break
        }
    }
    return founded
}

fun <T : Any> T?.ifNull(function: () -> Unit) {
    if (this == null) return function()
}
