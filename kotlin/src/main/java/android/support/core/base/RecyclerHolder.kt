package android.support.core.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

@Suppress("UNCHECKED_CAST")
open class RecyclerHolder<T> : RecyclerView.ViewHolder, LayoutContainer {

    private var mParent: ViewGroup

    constructor(parent: ViewGroup, @LayoutRes id: Int) : super((inflate(parent, id))) {
        this.mParent = parent
    }

    constructor(itemView: View) : super(itemView) {
        this.mParent = itemView.parent as ViewGroup
    }

    override val containerView = itemView

    var item: T? = null
        private set

    private val mAdapter get() = ((mParent as RecyclerView).adapter as RecyclerView.Adapter<*>)

    open fun bind(item: T) {
        this.item = item
    }

    open fun bind(item: T, payload: Any?) {
        this.item = item
    }

    open fun onRecycled() {
    }

    fun fitSpanCount(count: Int) {
        itemView.layoutParams.apply {
            width = mParent.measuredWidth / count
            height = width
        }
    }

    val isAtFirst get() = adapterPosition == 0

    open val isAtLast: Boolean
        get() {
            return adapterPosition == mAdapter.itemCount - 1
        }

    companion object {
        fun inflate(parent: ViewGroup, id: Int): View =
                LayoutInflater.from(parent.context)
                        .inflate(id, parent, false)
    }

}