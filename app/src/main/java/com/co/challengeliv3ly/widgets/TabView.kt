package com.co.challengeliv3ly.widgets

import android.content.Context
import android.support.core.design.internal.getMenu
import android.support.core.extensions.find
import android.support.core.functional.MenuOwner
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.MenuRes
import androidx.core.content.ContextCompat
import com.co.challengeliv3ly.R
import com.co.challengeliv3ly.extensions.load

class TabView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener, MenuOwner {

    private var mCurrentTabIndex = 0
    private val mOnTabChangeListeners = HashSet<(View, Int) -> Unit>()
    val currentTab get() = mCurrentTabIndex

    private var mTabMenuId: Int = 0
    private var mBackground: Int = 0
    private var mColor: Int = 0
    private var mInflater = LayoutInflater.from(context)
    private val mViewCache = SparseArray<View>()

    init {
        orientation = HORIZONTAL
        attrs?.load(this, R.styleable.TabView) {
            mTabMenuId = getResourceId(R.styleable.TabView_tabMenu, 0)
            mBackground =
                getResourceId(R.styleable.TabView_android_background, R.drawable.tab_default)
            mColor = getResourceId(R.styleable.TabView_android_color, R.color.selector_default)
        }
        setMenu(mTabMenuId)
    }

    fun setMenu(@MenuRes menu: Int, color: Int? = R.color.selector_default) {
        if (menu == 0) return
        mColor = color!!
        setMenu(context.getMenu(menu))
        select(mCurrentTabIndex, true)
    }

    private fun setMenu(menu: Menu) {
        mViewCache.clear()
        removeAllViews()
        val menuSize = menu.size()
        for (i in 0 until menuSize) {
            addMenu(menu.getItem(i), i, menuSize)
        }
    }

    private fun addMenu(item: MenuItem, i: Int, menuSize: Int) {
        (mInflater.inflate(R.layout.item_view_tab, this, false) as TextView).apply {
            id = item.itemId
            text = item.title
            tag = i
            this.background = ContextCompat.getDrawable(context, mBackground)
            this.setTextColor(ContextCompat.getColorStateList(context, mColor))
            if (item.isChecked) mCurrentTabIndex = i
            setOnClickListener(this@TabView)
            addView(this)
            mViewCache.put(i, this)
        }
    }

    fun addOnTabSelectedListener(tabChangeListener: (view: View, position: Int) -> Unit) {
        if (mOnTabChangeListeners.add(tabChangeListener)) {
            tabChangeListener.invoke(mViewCache[mCurrentTabIndex], mCurrentTabIndex)
        }
    }

    override fun onClick(v: View) {
        select(v.tag as Int)
    }

    fun select(i: Int) {
        select(i, false)
    }

    private fun select(i: Int, reselect: Boolean) {
        if (!reselect && mCurrentTabIndex == i) return
        mViewCache.get(mCurrentTabIndex).isSelected = false
        val currentView = mViewCache.get(i)
        currentView.isSelected = true
        mCurrentTabIndex = i
        mOnTabChangeListeners.forEach { it(currentView, i) }
    }

    override fun getCurrentId() = mViewCache[mCurrentTabIndex].id

    override fun setOnIdSelectedListener(onIdSelectedListener: (Int) -> Unit) {
        addOnTabSelectedListener { view, _ -> onIdSelectedListener(view.id) }
    }

    override fun selectId(id: Int) {
        mViewCache.find { it.id == id }?.apply { select(this.tag as Int) }
    }
}
