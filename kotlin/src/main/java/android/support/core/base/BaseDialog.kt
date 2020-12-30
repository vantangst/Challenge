package android.support.core.base

import android.app.Dialog
import android.content.Context
import android.graphics.PointF
import android.os.Build
import android.view.*
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.Window.ID_ANDROID_CONTENT
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlin.math.pow
import kotlin.math.sqrt

abstract class BaseDialog(context: Context, lifecycle: Lifecycle?, themeRes: Int)
    : Dialog(context, themeRes) {
    private var mCancelable: Boolean
    private lateinit var mContentView: View
    private val mTouchDown = PointF()

    constructor(context: Context) : this(context,
            if (context is LifecycleOwner) context.lifecycle else null, 0)

    constructor(context: Context, themeRes: Int) : this(context,
            if (context is LifecycleOwner) context.lifecycle else null, themeRes)

    constructor(fragment: Fragment) : this(fragment.requireContext(), fragment.lifecycle, 0)
    constructor(fragment: Fragment, themeRes: Int) : this(fragment.requireContext(), fragment.lifecycle, themeRes)

    init {
        lifecycle?.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                dismiss()
            }
        })
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        mCancelable = true
    }


    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        mContentView = (findViewById<View>(ID_ANDROID_CONTENT) as ViewGroup).getChildAt(0)
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) {
        super.setContentView(view, params)
        mContentView = view
    }

    fun getContentView() = mContentView

    fun requestWidthMatchParent() {
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun requestFullScreen() {
        window?.apply {
            setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                decorView.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
        }
    }

    override fun setCancelable(flag: Boolean) {
        super.setCancelable(flag)
        mCancelable = flag
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mCancelable) {
            if (event.action == MotionEvent.ACTION_DOWN) {
                mTouchDown.set(event.x, event.y)
            } else if (event.action == MotionEvent.ACTION_UP) {
                if (mTouchDown.isClick(event) && !mContentView.contains(mTouchDown))
                    cancel()
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}

private fun View.contains(point: PointF): Boolean {
    return left <= point.x && point.x <= right && top <= point.y && point.y <= bottom
}

private fun PointF.isClick(event: MotionEvent): Boolean {
    return sqrt((x - event.x).toDouble().pow(2.0)
            + (y - event.y).toDouble().pow(2.0)) <= 10
}
