package android.support.core.base

import android.support.core.functional.OnLoadMoreListener
import androidx.annotation.MainThread
import androidx.recyclerview.widget.RecyclerView


abstract class PageRecyclerAdapter<T>(val view: RecyclerView,
                                      private val pageSize: Int,
                                      private val threshold: Int = 0) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val STATE_INITIAL = -1
        private const val STATE_LOADING = 1
        private const val STATE_UNLOAD = 0
    }

    private var mState = STATE_INITIAL
    private val mItems = PageList<T>(pageSize)
    var onLoadMoreListener: ((Int, Int) -> Unit)? = null

    private var mLoadMoreListener = object : OnLoadMoreListener(threshold) {
        override fun onLoadMore() {
            if (!mItems.shouldLoadMore) return
            if (mState == STATE_LOADING) return
            mState = STATE_LOADING
            val nextPage = mItems.nextPage
            this@PageRecyclerAdapter.onLoadMore(nextPage, pageSize)
            onLoadMoreListener?.invoke(nextPage, pageSize)
        }
    }

    init {
        view.adapter = this
        mItems.onItemsInserted = { from, count ->
            if (count == 1) notifyItemInserted(from)
            else notifyItemRangeInserted(from, count)
        }
    }

    var isLoading: Boolean
        get() = mState == STATE_LOADING
        set(value) {
            mState = if (value) STATE_LOADING else STATE_UNLOAD
        }

    @MainThread
    open fun submit(items: List<T>?) {
        if (items != null) mItems.addAll(items)
    }

    override fun getItemCount() = mItems.size
    fun items() = mItems

    fun getData(): MutableList<T> {
        return mItems.getData()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(mLoadMoreListener)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        recyclerView.removeOnScrollListener(mLoadMoreListener)
        super.onDetachedFromRecyclerView(recyclerView)
    }

    protected open fun onLoadMore(nextPage: Int, pageSize: Int) {

    }

    @Suppress("unchecked_cast")
    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        (p0 as? RecyclerHolder<T>)?.bind(mItems[p1])
    }

    fun clear() {
        mItems.clear()
        notifyDataSetChanged()
    }
}

class PageList<T>(
        private val pageSize: Int
) {
    internal lateinit var onItemsInserted: (from: Int, count: Int) -> Unit
    val nextPage get() = (mItems.size / pageSize) + 1

    var shouldLoadMore: Boolean = true
        private set

    private val mItems = arrayListOf<T>()
    val size get() = mItems.size

    operator fun get(index: Int) = mItems[index]

    fun addAll(items: List<T>) {
        val oldSize = mItems.size
        mItems.addAll(items)
        if (items.size != pageSize) shouldLoadMore = false
        onItemsInserted(oldSize, items.size)
    }

    fun getData(): MutableList<T> {
        return mItems
    }

    fun clear() {
        mItems.clear()
        shouldLoadMore = true
    }

}