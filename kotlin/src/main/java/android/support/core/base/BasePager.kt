package android.support.core.base

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.viewpager.widget.PagerAdapter
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer

@Suppress("UNCHECKED_CAST")
abstract class BasePager : androidx.viewpager.widget.PagerAdapter() {
    private val mCache = SparseArray<PagerHolder<*>>()

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return o === view
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val viewHolder = onCreateViewHolder(container, getViewType(position))
        val view = viewHolder.itemView
        mCache.put(position, viewHolder)
        container.addView(view)
        viewHolder.position = position
        notifyItemChanged(position)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val pagerHolder = mCache.get(position)
        mCache.remove(position)
        pagerHolder.onRecycled()
        container.removeView(`object` as View)
    }

    @JvmOverloads
    fun notifyItemChanged(position: Int, payload: Any? = null) {
        val item = getItem(position)
        val pagerHolder = mCache.get(position) as? PagerHolder<Any>
        if (item != null && pagerHolder != null) {
            if (payload != null) {
                pagerHolder.bind(item, payload)
            } else {
                pagerHolder.bind(item)
            }
        }
    }

    protected open fun getViewType(position: Int): Int {
        return position
    }

    protected abstract fun onCreateViewHolder(container: ViewGroup, viewType: Int): PagerHolder<*>

    protected abstract fun getItem(position: Int): Any?

    fun <T : Any> getViewHolder(index: Int): PagerHolder<T> {
        return mCache.get(index) as PagerHolder<T>
    }
}

open class PagerHolder<T : Any> : LayoutContainer {
    override val containerView get() = itemView

    val itemView: View
    var position: Int = 0
    protected lateinit var item: T

    constructor(itemView: View) {
        this.itemView = itemView
    }

    constructor(parent: ViewGroup, @LayoutRes layoutId: Int) {
        itemView = LayoutInflater.from(parent.context)
            .inflate(layoutId, parent, false)
    }

    open fun bind(item: T) {
        this.item = item
    }

    open fun bind(item: T, payload: Any) {
        this.item = item
    }

    fun <V : View> findViewById(@IdRes id: Int): V {
        return itemView.findViewById(id)
    }

    open fun onRecycled() {

    }
}

