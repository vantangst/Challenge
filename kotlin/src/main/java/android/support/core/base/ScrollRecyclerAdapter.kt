package android.support.core.base


import androidx.recyclerview.widget.RecyclerView
import android.util.SparseIntArray
import android.view.ViewGroup

@Suppress("LeakingThis")
abstract class ScrollRecyclerAdapter(view: RecyclerView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val SIZE_OF_SINGLE = -1
    }

    private val mOrders by lazy { getOrders() }
    private val mOrderCache = SparseIntArray()

    abstract fun getOrders(): IntRange

    init {
        view.adapter = this
        registerAdapterDataObserver(DataObserver())
    }

    private fun onDataChanged() {
        mOrderCache.clear()
    }

    final override fun getItemViewType(position: Int): Int {
        val order = findOrderIfNeeded(position)
        if (isSingle(order)) return order
        return getListViewType(position - getOffset(order), order)
    }

    private fun isSingle(order: Int) = getOrderSize(order) == SIZE_OF_SINGLE

    private fun findOrderIfNeeded(position: Int): Int {
        var orderValue = mOrderCache.get(position, -1)
        if (orderValue != -1) return orderValue
        orderValue = findOrder(position)
        mOrderCache.put(position, orderValue)
        return orderValue
    }

    private fun findOrder(position: Int): Int {
        var lastOffset = -1
        var lastOrder = -1
        for (order in mOrders) {
            val offset = getOffset(order)
            if (offset == position && getOrderRealSize(order) != 0) return order
            if (position in (lastOffset + 1) until offset) return lastOrder
            lastOffset = offset
            lastOrder = order
        }
        return -1
    }

    fun getOffset(order: Int): Int {
        if (order < 1) throw RuntimeException("Order (type of view) should be >= 1")
        if (order == 1) return 0
        return getOffset(order - 1) + getOrderRealSize(order - 1)
    }

    protected open fun getOrderRealSize(order: Int): Int {
        val orderSize = getOrderSize(order)
        if (orderSize == SIZE_OF_SINGLE) return 1
        return orderSize
    }

    protected open fun getOrderSize(order: Int) = SIZE_OF_SINGLE

    open fun getListViewType(positionInList: Int, orderOfList: Int) = orderOfList

    final override fun getItemCount() =
        mOrders.fold(0) { acc, order -> acc + getOrderRealSize(order) }

    final override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        val order = findOrderIfNeeded(p1)
        if (isSingle(order)) onBindSingleViewHolder(p0, order)
        else onBindListViewHolder(p0, p1 - getOffset(order))
    }

    final override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.size == 0) super.onBindViewHolder(holder, position, payloads)
        else {
            val order = findOrderIfNeeded(position)
            if (isSingle(order)) onBindSingleViewHolder(holder, order, payloads)
            else onBindListViewHolder(holder, position - getOffset(order), payloads)
        }
    }

    abstract override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    abstract fun onBindListViewHolder(p0: RecyclerView.ViewHolder, positionInList: Int)

    abstract fun onBindSingleViewHolder(p0: RecyclerView.ViewHolder, order: Int)

    open fun onBindListViewHolder(p0: RecyclerView.ViewHolder, positionInList: Int, payloads: MutableList<Any>) {
        onBindListViewHolder(p0, positionInList)
    }

    open fun onBindSingleViewHolder(p0: RecyclerView.ViewHolder, order: Int, payloads: MutableList<Any>) {
        onBindSingleViewHolder(p0, order)
    }

    private inner class DataObserver : RecyclerView.AdapterDataObserver() {
        override fun onChanged() = onDataChanged()
        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) =
            onDataChanged()

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) =
            onDataChanged()

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) =
            onDataChanged()

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) =
            onDataChanged()
    }
}