package com.google.android.material.tabs

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.support.R
import android.support.core.design.internal.getMenu
import android.support.core.extensions.windowSize
import android.support.core.functional.MenuOwner
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.DrawableCompat

class BottomMenuView : TabLayout, MenuOwner {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private var mMenu: Menu? = null
    private var mTabTextAppearance = 0

    private fun init(attrs: AttributeSet?) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BottomMenuView)
        val menuId = typedArray.getResourceId(R.styleable.BottomMenuView_tabMenu, 0)
        val isCustom = typedArray.getBoolean(R.styleable.BottomMenuView_tabCustom, false)
        mTabTextAppearance = typedArray.getResourceId(R.styleable.BottomMenuView_tabTextAppearance, 0)
        typedArray.recycle()
        mMenu = context.getMenu(menuId)
        if (!isCustom || isInEditMode) {
            setMenu(menuId)
        }
    }

    fun setAdapter(adapter: Adapter) {
        if (mMenu == null) return
        for (i in 0 until mMenu!!.size()) {
            val item = mMenu!!.getItem(i)
            val tab = newTab().setTag(item.itemId)
            val viewHolder = adapter.onCreateViewHolder(item, i)
            tab.setCustomView(viewHolder.layoutId)
            viewHolder.itemView = tab.customView!!
            viewHolder.menuView = this
            viewHolder.position = i
            viewHolder.bind(item)
            if (item.isChecked) tab.select()
            addTab(tab, i)
        }
    }

    override fun getCurrentId() = getTabAt(selectedTabPosition)!!.tag as Int

    override fun setOnIdSelectedListener(onIdSelectedListener: (id: Int) -> Unit) {
        addOnTabSelectedListener(OnMenuChangedListener(onIdSelectedListener))
    }

    override fun selectId(id: Int) {
        if (getCurrentId() != id)
            findTabByTag(id)?.select()
    }

    internal override fun populateFromPagerAdapter() {
        if (viewPager?.adapter !is IconSettable) {
            super.populateFromPagerAdapter()
            return
        }
        removeAllTabs()
        val pagerAdapter = viewPager?.adapter
        if (pagerAdapter != null) {
            val adapterCount = pagerAdapter.count
            for (i in 0 until adapterCount) {
                val tab = newTab()
                        .setIcon((pagerAdapter as IconSettable).getPageIcon(i))
                        .setText(pagerAdapter.getPageTitle(i))
                addTab(tab, false)
            }

            if (viewPager != null && adapterCount > 0) {
                val curItem = viewPager?.currentItem ?: 0
                if (curItem != selectedTabPosition && curItem < tabCount) {
                    selectTab(getTabAt(curItem))
                }
            }
        }
    }

    private fun findTabByTag(id: Int): Tab? {
        val pos = (0 until tabCount).find { getTabAt(it)!!.tag as Int == id }
        if (pos != null)
            return getTabAt(pos)!!
        return null
    }

    fun setupWithAdapter(container: ViewGroup, adapter: androidx.fragment.app.FragmentPagerAdapter) {
        container.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(view: View) {
                adapter.startUpdate(container)
                adapter.instantiateItem(container, getTabAt(selectedTabPosition)?.tag as Int)
                adapter.finishUpdate(container)
            }

            override fun onViewDetachedFromWindow(view: View) {
                // Skip
            }
        })
        addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                adapter.startUpdate(container)
                adapter.instantiateItem(container, tab.tag as Int)
                adapter.finishUpdate(container)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                adapter.startUpdate(container)
                adapter.destroyItem(container, tab.tag as Int, adapter.getItem(tab.tag as Int))
                adapter.finishUpdate(container)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // Skip
            }
        })
    }

    /**
     * Set Menu with default layout R.layout.view_tab_menu
     */
    private fun setMenu(@MenuRes menuId: Int) {
        val textSize = getTextSize()
        val menu = context.getMenu(menuId)
        for (i in 0 until menu.size()) {
            addMenu(menu.getItem(i), i, textSize)
        }
    }

    fun getTabTextSize() = tabTextSize

    private fun setIconTintMode(drawable: Drawable) {
        if (tabIconTintMode != null) DrawableCompat.setTintMode(drawable, tabIconTintMode)
    }

    private fun setIconTintList(drawable: Drawable) {
        DrawableCompat.setTintList(drawable, tabIconTint)
    }

    @SuppressLint("NewApi")
    private fun addMenu(item: MenuItem, position: Int, textSize: Float) {
        val tab = newTab().setTag(item.itemId)
        tab.setCustomView(R.layout.view_tab_menu)
        val view = tab.customView!! as ViewGroup
        val textView = view.getChildAt(1) as TextView
        val iconView = view.getChildAt(0) as ImageView
        textView.setTextAppearance(mTabTextAppearance)
        if (item.title != null && item.title.isNotEmpty()) {
            textView.visibility = View.VISIBLE
            textView.text = item.title
            if (tabTextSize != 0f) textView.setTextSize(0, textSize)
            if (tabTextColors != null) textView.setTextColor(tabTextColors)
        } else {
            textView.visibility = View.GONE
        }
        item.icon?.apply {
            setIconTintList(this)
            setIconTintMode(this)
            iconView.setImageDrawable(this)
        }
        tab.view.setPadding(0,0,0,0)
        tab.view.isClickable = !(item.icon == null && item.title.isNullOrEmpty())
        if (item.isChecked) tab.select()
        addTab(tab, position)
    }

    private fun getTextSize(): Float {
        val windowSizeX = context.windowSize.x
        val windowSizeXY = context.windowSize.y
        return when (windowSizeX) {
            1440 -> if (windowSizeXY == 2560) tabTextSize - 20f else tabTextSize - 9f
            1080 -> tabTextSize - 9f
            720 -> tabTextSize - 9f
            else -> tabTextSize
        }
    }

    interface IconSettable {
        fun getPageIcon(i: Int): Int
    }

    override fun onSaveInstanceState(): Parcelable? {
        val parentState = super.onSaveInstanceState()
        val state = SaveState(parentState)
        state.tabSelected = selectedTabPosition
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is SaveState) {
            super.onRestoreInstanceState(state)
            return
        }
        onRestoreInstanceState(state.superState)
        getTabAt(state.tabSelected)?.select()
    }

    class SaveState : BaseSavedState {
        var tabSelected: Int = 0

        constructor(parcel: Parcel) : super(parcel) {
            tabSelected = parcel.readInt()
        }

        constructor(parcelable: Parcelable?) : super(parcelable)

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.writeInt(tabSelected)
        }

        companion object CREATOR : Parcelable.Creator<SaveState> {
            override fun createFromParcel(parcel: Parcel): SaveState {
                return SaveState(parcel)
            }

            override fun newArray(size: Int): Array<SaveState?> {
                return arrayOfNulls(size)
            }
        }
    }

    class OnMenuChangedListener(private val function: (Int) -> Unit) : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(p0: TabLayout.Tab?) {}

        override fun onTabUnselected(p0: TabLayout.Tab?) {}

        override fun onTabSelected(p0: TabLayout.Tab?) {
            function(p0!!.tag as Int)
        }
    }

    abstract class Adapter {
        abstract fun onCreateViewHolder(item: MenuItem, position: Int): ViewHolder
    }

    abstract class ViewHolder(@LayoutRes val layoutId: Int) {
        lateinit var menuView: BottomMenuView
        lateinit var itemView: View
        var position: Int = -1
        abstract fun bind(item: MenuItem)
    }
}

