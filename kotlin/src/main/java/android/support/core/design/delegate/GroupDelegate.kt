package android.support.core.design.delegate

import android.support.core.base.RecyclerAdapter
import android.util.SparseArray
import android.util.SparseIntArray

class GroupDelegate(private val adapter: RecyclerAdapter<out Group>) {
    companion object {
        const val TYPE_ITEM = 0
        const val TYPE_GROUP = 1
    }

    private var mItemViewType = SparseIntArray()
    private var mItems = SparseArray<Any>()
    private var mGroups = SparseArray<Group>()
    private var mAdapterPositions = HashMap<Any, Int>()

    var itemCount: Int = 0
        private set
    val groupCount get() = mGroups.size()

    fun getItemViewType(position: Int) = mItemViewType[position]

    fun notifyDataSetChanged() {
        updateCache()
        adapter.notifyDataSetChanged()
    }

    fun notifyGroupInserted(group: Group) {
        val groupAdapterPosition = itemCount
        mItemViewType.put(groupAdapterPosition, TYPE_GROUP)
        mItems.put(groupAdapterPosition, group)
        mAdapterPositions[group] = groupAdapterPosition
        itemCount += 1
        adapter.notifyItemInserted(groupAdapterPosition)
    }

    fun toggle(group: Group, adapterPosition: Int) {
        group.toggle()
        updateCache()
        if (group.expanded) expandGroup(group, adapterPosition)
        else collapseGroup(group, adapterPosition)
    }

    fun toggle(group: Group) {
        toggle(group, mAdapterPositions[group]!!)
    }

    private fun expandGroup(group: Group, adapterPosition: Int) {
        if (group.size > 0) adapter.notifyItemRangeInserted(adapterPosition + 1, group.size)
    }

    private fun collapseGroup(group: Group, adapterPosition: Int) {
        if (group.size > 0) adapter.notifyItemRangeRemoved(adapterPosition + 1, group.size)
    }

    private fun updateCache() {
        mItemViewType.clear()
        mItems.clear()
        mGroups.clear()
        mAdapterPositions.clear()
        updateItemCount()
        updateItems()
    }

    private fun updateItems() {
        if (adapter.items == null) return
        var adapterPosition = 0
        for (item in adapter.items!!) {
            onTraversalGroup(adapterPosition, item)
            adapterPosition++
            if (item.expanded) {
                for (i in (0 until item.size)) {
                    onTraversalItem(adapterPosition, item[i], item)
                    adapterPosition++
                }
            }
        }
    }

    private fun onTraversalItem(adapterPosition: Int, item: Any, group: Group) {
        mItemViewType.put(adapterPosition, TYPE_ITEM)
        mItems.put(adapterPosition, item)
        mGroups.put(adapterPosition, group)
        mAdapterPositions[item] = adapterPosition
    }

    private fun onTraversalGroup(adapterPosition: Int, item: Group) {
        mItemViewType.put(adapterPosition, TYPE_GROUP)
        mItems.put(adapterPosition, item)
        mAdapterPositions[item] = adapterPosition
    }

    private fun updateItemCount() {
        itemCount = adapter.items?.fold(0) { acc, group ->
            val size = if (group.expanded) group.size + 1 else 1
            acc + size
        } ?: 0
    }

    fun getItem(adapterPosition: Int) = mItems.get(adapterPosition)!!

    fun getGroup(adapterPosition: Int) = mGroups[adapterPosition]

    fun getGroup(item: Any) = mGroups[mAdapterPositions[item]!!]

    fun notifyItemChanged(item: Any) {
        adapter.notifyItemChanged(mAdapterPositions[item]!!)
    }

    fun notifyItemChanged(item: Any, payload: Any) {
        adapter.notifyItemChanged(mAdapterPositions[item]!!, payload)
    }

    fun removeItem(it: Any) {
        val pos = mAdapterPositions[it]!!
        mGroups[pos].remove(it)
        updateCache()
        adapter.notifyItemRemoved(pos)
    }

    fun contains(it: Any) = mAdapterPositions[it] != null

    abstract class Group {
        @Transient
        var expanded = true
            private set

        protected abstract val items: MutableList<Any>?

        val size get() = if (items == null) 0 else items!!.size

        fun toggle() {
            expanded = !expanded
        }

        operator fun get(i: Int): Any {
            return items!![i]
        }

        fun remove(item: Any) {
            items!!.remove(item)
        }

        fun indexOf(item: Any): Int {
            return items!!.indexOf(item)
        }
    }
}