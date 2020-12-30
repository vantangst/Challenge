package android.support.core.base

import androidx.recyclerview.widget.RecyclerView

@Suppress("UNCHECKED_CAST")
abstract class RecyclerAdapter<T>(val view: RecyclerView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    open var items: MutableList<T>? = null
        set(value) {
            if (field == value) return
            field = value
            notifyDataSetChanged()
        }

    private val mOnEnabledListeners = hashSetOf<(Boolean) -> Unit>()

    open var isEnabled: Boolean = false
        set(value) {
            field = value
            mOnEnabledListeners.forEach { it(value) }
        }

    init {
        view.adapter = this
    }

    fun addOnEnableListener(function: (Boolean) -> Unit) {
        function(isEnabled)
        mOnEnabledListeners.add(function)
    }

    fun removeOnEnableListener(function: (Boolean) -> Unit) {
        mOnEnabledListeners.remove(function)
    }

    open fun submitList(items: MutableList<T>?) {
        this.items = items
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        (p0 as? RecyclerHolder<T>)?.bind(getItem(p1))
    }

    protected open fun getItem(p1: Int): T {
        return items!![p1]
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) super.onBindViewHolder(holder, position, payloads)
        else (holder as? RecyclerHolder<T>)?.bind(getItem(position), payloads)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        (holder as? RecyclerHolder<T>)?.onRecycled()
    }

    override fun getItemCount() = if (items == null) 0 else items!!.size
}