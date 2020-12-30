package com.co.challengeliv3ly.views.popups

import android.content.Context
import android.support.core.base.RecyclerAdapter
import android.support.core.base.RecyclerHolder
import android.support.core.lifecycle.LifeRegister
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.co.challengeliv3ly.R

class MenuPopup<T>(owner: LifecycleOwner, context: Context) : PopupWindow(context) {
    private var mMenuAdapter: Adapter
    private val mMaxWidth = context.resources.getDimensionPixelSize(R.dimen.size_200)
    private val mMaxHeight = context.resources.getDimensionPixelSize(R.dimen.size_400)
    private var mOnItemClickListener: ((T) -> Unit)? = null
    private var mOnCallBack: ((T) -> Unit)? = null
    var mItemSelected: T? = null
    var callBackFirst = false
    var items: MutableList<T>?
        set(value) {
            callBackFirst = true
            mMenuAdapter.items = value
            measure()
        }
        get() = mMenuAdapter.items

    init {
        contentView = RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            mMenuAdapter = Adapter(this)
        }

        LifeRegister.of(owner).onDestroy(this::dismiss)
        setBackgroundDrawable(ContextCompat.getDrawable(context, android.R.drawable.picture_frame))
        isOutsideTouchable = true
    }

    private fun measure() {
        val measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        contentView.measure(measureSpec, measureSpec)
//        width = max(contentView.measuredWidth, mMaxWidth)
//        height = min(contentView.measuredHeight, mMaxHeight)
    }

    fun showAtRight(view: View) {
        val translate = -(contentView.measuredWidth - view.measuredWidth)
        showAsDropDown(view, translate, 0)
    }

    fun showAtLeft(view: View) {
        val translate = -(contentView.measuredWidth)
        showAsDropDown(view, translate, 0)
    }

    fun showAtRight(view: View, callback: (T) -> Unit) {
        mOnItemClickListener = callback
        showAtRight(view)
    }

    fun setListener(callback: (T) -> Unit) {
        mOnCallBack = callback
    }

    fun show(view: View) {
//        showAsDropDown(view, 0, 0)
        showAtLeft(view)
    }

    fun showAtCenter(view: View) {
        val translate = -(contentView.measuredWidth - view.measuredWidth) / 2
        showAsDropDown(view, translate, 0)
    }

    fun setupWithViews(viewClicked: View, viewToRender: TextView, gravity: Int = Gravity.END, rotate: Boolean = false) {
        viewClicked.setOnClickListener {
            when (gravity) {
                Gravity.END -> showAtRight(it)
                Gravity.CENTER -> showAtCenter(it)
                Gravity.START -> showAtLeft(it)
                else -> show(it)
            }
            if (rotate) {
                setOnDismissListener { it.animate().rotation(0f).start() }
                if (it.rotation == 0f) it.animate().rotation(90f).start()
                else dismiss()
            } else setOnDismissListener(null)
        }
        mOnItemClickListener = {
            viewToRender.text = it.toString()
            mOnCallBack?.invoke(it!!)
        }
    }

    fun setupWithView(viewClicked: View) {
        viewClicked.setOnClickListener {
            show(it)
        }
        mOnItemClickListener = {
            mOnCallBack?.invoke(it!!)
        }
    }

    fun setSelection(pos: Int) {
        mMenuAdapter.select(pos)
    }

    inner class Adapter(view: RecyclerView) : RecyclerAdapter<T>(view) {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int) =
            object : RecyclerHolder<T>(p0, R.layout.item_view_text_popup) {
                init {
                    itemView.setOnClickListener {
                        mOnItemClickListener?.invoke(item!!)
                        mItemSelected = item
                        dismiss()
                    }
                }

                override fun bind(item: T) {
                    super.bind(item)
                    if (adapterPosition == 0 && callBackFirst) {
                        mOnItemClickListener?.invoke(item!!)
                        mItemSelected = item
                        callBackFirst = false
                    }
                    itemView as TextView
                    itemView.text = item.toString()
                }
            }

        fun select(pos: Int) {
            if (pos < items!!.size) {
                val item = items!![pos]
                if (item == mItemSelected) return
                mOnItemClickListener?.invoke(item)
                mItemSelected = item
            }
        }
    }
}