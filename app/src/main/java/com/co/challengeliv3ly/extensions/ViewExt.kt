package com.co.challengeliv3ly.extensions

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

fun ViewGroup.setContentView(@LayoutRes id: Int) {
    LayoutInflater.from(context).inflate(id, this, true)
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

var TextView.textRes: Int
    get() = tag as? Int ?: 0
    set(value) {
        tag = value
        setText(value)
    }

operator fun View.plus(view: View) = arrayOf(this, view)

fun AttributeSet?.load(
    view: View, attr: IntArray,
    function: TypedArray.() -> Unit
) {
    val context = view.context
    val ta = context.obtainStyledAttributes(this, attr)
    function(ta)
    ta.recycle()
}

private operator fun Int.iterator(): Iterator<Int> {
    return iterator()
}

infix fun Boolean.lock(views: Array<View>) {
    views.forEach { it.isEnabled = !this }
}

infix fun Boolean.show(views: Array<View>) {
    views.forEach { it.visibility = if (this) View.VISIBLE else View.GONE }
}

infix fun Boolean.show(view: View) {
    view.visibility = if (this) View.VISIBLE else View.GONE
}

infix fun Boolean.show(view: ViewGroup) {
    view.visibility = if (this) View.VISIBLE else View.GONE
}

infix fun Boolean.lock(view: View) {
    view.isEnabled = !this
}

infix fun Array<View>.setText(text: String) {
    forEach { (it as? EditText)?.setText(text) }
}

infix fun Boolean.visible(view: View) {
    view.visibility = if (this) View.VISIBLE else View.INVISIBLE
}

infix fun Boolean.visible(views: Array<View>) {
    views.forEach { it.visibility = if (this) View.VISIBLE else View.INVISIBLE }
}


fun TextView.show(show: Boolean, @StringRes res: Int) {
    visibility = if (show) {
        setText(res)
        View.VISIBLE
    } else View.GONE
}

val View.viewWidth: Int
    get() {
        var width = this.width
        if (width > 0) return width
        width = layoutParams?.width ?: 0
        if (width > 0) return width
        width = measuredWidth
        if (width > 0) return width
        measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), 0)
        width = measuredWidth
        return width
    }

val View.viewHeight: Int
    get() {
        var height = this.height
        if (height > 0) return height
        height = layoutParams?.height ?: 0
        if (height > 0) return height
        height = measuredHeight
        if (height > 0) return height
        measure(0, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        height = measuredHeight
        return height
    }

fun Context.getAppResourceId(attr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.resourceId
}

fun Context.loadAttrs(attrs: AttributeSet?, attrType: IntArray, function: (TypedArray) -> Unit) {
    if (attrs == null) return
    val a = obtainStyledAttributes(attrs, attrType)
    function(a)
    a.recycle()
}

infix fun Int.color(views: Array<View>) {
    views.forEach { if (it is TextView) it.setTextColor(ContextCompat.getColor(it.context, this)) }
}

fun textResource(context: Context, idString: Int): String {
    return context.getString(idString)
}

fun textColorResource(context: Context, idColor: Int): Int {
    return ContextCompat.getColor(context, idColor)
}

fun backgroundDrawable(context: Context, idDrawable: Int): Drawable {
    return ContextCompat.getDrawable(context, idDrawable)!!
}

fun TextView.drawableStart(idDrawable: Int) {
    this.setCompoundDrawablesWithIntrinsicBounds(idDrawable, 0, 0, 0)
}


