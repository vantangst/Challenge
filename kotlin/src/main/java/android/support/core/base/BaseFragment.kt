package android.support.core.base

import android.content.Intent
import android.os.Bundle
import android.support.core.extensions.dispatchHidden
import android.support.core.extensions.isVisibleOnScreen
import android.support.core.functional.Backable
import android.support.core.functional.Dispatcher
import android.support.core.functional.Navigable
import android.support.core.lifecycle.LifeRegister
import android.support.core.lifecycle.ResultLifecycle
import android.support.core.lifecycle.ResultRegistry
import android.support.core.lifecycle.ViewLifecycleOwner
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.View

/**
 * Custom for lifecycle
 */
abstract class BaseFragment : Fragment(), Backable, Dispatcher, Navigable {
    private val TAG: String = this.javaClass.simpleName

    companion object {
        private const val STATE_INVISIBLE = 1
        private const val STATE_VISIBLE = 0
        private const val STATE_NONE = -1
    }

    val resultLife: ResultLifecycle = ResultRegistry()
    val viewLife = ViewLifecycleOwner()
    val lifeRegister by lazy { LifeRegister.of(viewLife) }

    private val mLifeRegistry get() = viewLife.lifecycle
    private var mVisibleState = STATE_NONE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mLifeRegistry.create()
        Log.i(TAG, "Created")
        initView()
        loadData()
        observeData()
        setListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mLifeRegistry.destroy()
        Log.i(TAG, "Destroy")
    }

    final override fun onStart() {
        super.onStart()
        if (isVisibleOnScreen()) {
            performStartFragment()
            mLifeRegistry.start()
            Log.i(TAG, "Start")
        }
    }

    final override fun onStop() {
        super.onStop()
        if (isVisibleOnScreen()) {
            performStopFragment()
            mLifeRegistry.stop()
            Log.i(TAG, "Stop")
        }
    }

    final override fun onResume() {
        super.onResume()
        if (isVisibleOnScreen()) {
            onFragmentResumed()
            mLifeRegistry.resume()
            Log.i(TAG, "Resume")
        }
    }

    final override fun onPause() {
        super.onPause()
        if (isVisibleOnScreen()) {
            onFragmentPaused()
            mLifeRegistry.pause()
            Log.i(TAG, "Pause")
        }
    }

    final override fun onHiddenChanged(hidden: Boolean) {
        if (mVisibleState == (if (hidden) STATE_INVISIBLE else STATE_VISIBLE)) return

        if (hidden) {
            onFragmentPaused()
            mLifeRegistry.pause()
            performStopFragment()
            mLifeRegistry.stop()
            Log.i(TAG, "Hide")
        } else {
            performStartFragment()
            mLifeRegistry.start()
            onFragmentResumed()
            mLifeRegistry.resume()
            Log.i(TAG, "Show")
        }
        dispatchHidden(hidden)
    }

    private fun performStopFragment() {
        onFragmentStopped()
        mVisibleState = STATE_INVISIBLE
    }

    private fun performStartFragment() {
        onFragmentStarted()
        mVisibleState = STATE_VISIBLE
        arguments?.apply { handleNavigateArguments(this) }
    }

    protected open fun onFragmentStarted() {
    }

    protected open fun onFragmentStopped() {
    }

    protected open fun onFragmentResumed() {
    }

    protected open fun onFragmentPaused() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        (resultLife as ResultRegistry).handleActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        (resultLife as ResultRegistry).handlePermissionsResult(requestCode, permissions, grantResults)
    }

    open fun initView() {}
    open fun loadData() {}
    open fun setListener() {}
    open fun observeData() {}
}