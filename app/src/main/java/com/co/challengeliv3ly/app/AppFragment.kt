package com.co.challengeliv3ly.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.core.annotations.LayoutId
import android.support.core.annotations.ShareViewModel
import android.support.core.annotations.SharedOf
import android.support.core.base.BaseFragment
import android.support.core.base.BaseViewModel
import android.support.core.extensions.getAnnotation
import android.support.core.extensions.getFirstGenericParameter
import android.support.core.extensions.observe
import android.support.core.factory.ViewModelFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.co.challengeliv3ly.R
import com.co.challengeliv3ly.annotations.ActionBarOptions
import com.co.challengeliv3ly.views.dialogs.CommonDialog
import com.co.challengeliv3ly.widgets.AppActionBar

abstract class AppFragment<VM : BaseViewModel> : BaseFragment() {
    lateinit var viewModel: VM
    private var mLoadingView: View? = null
    private var mLoadingRefreshView: SwipeRefreshLayout? = null

    val appActivity get() = activity as AppActivity<*>
    val appSettings get() = appActivity.appSettings
    val appPermission by lazy { AppPermission(appActivity) }
    val appEvent get() = appActivity.appEvent
    val appCache get() = appActivity.appCache
    val commonDialog by lazy { CommonDialog(requireContext()) }

    //    val appInfoDialog by lazy { AppInfoDialog(context!!) }
    val mHandler by lazy { Handler() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = when (getAnnotation<ShareViewModel>()?.value) {
            SharedOf.ACTIVITY -> ViewModelProvider(requireActivity(), ViewModelFactory.sInstance)
            SharedOf.PARENT -> ViewModelProvider(requireParentFragment(), ViewModelFactory.sInstance)
            else -> ViewModelProvider(this, ViewModelFactory.sInstance)
        }.get(getFirstGenericParameter()) as VM
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mLoadingView = view.findViewById(R.id.viewLoading)
        mLoadingRefreshView = view.findViewById(R.id.viewRefresh)
        mLoadingRefreshView?.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        if (viewModel != (activity as? AppActivity<*>)?.viewModel) {
            viewModel.loading.observe(this, this::showLoading)
            viewModel.refreshLoading.observe(this, this::showLoadingRefresh)
            viewModel.error.observe(this, appActivity::handleError)
        }
    }

    @SuppressLint("WrongViewCast")
    override fun onFragmentStarted() {
        super.onFragmentStarted()
        getAnnotation<ActionBarOptions>()?.apply {
            val actionBar = appActivity.findViewById<AppActionBar>(R.id.appActionBar)
                ?: return@apply
            actionBar.setupWithOptions(this)
            actionBar.setOnLeftClickListener {
                if (actionBar.isHome()) {
                    appActivity.onHomeClicked()
                } else {
                    appActivity.onBackPressed()
                }
            }
            actionBar.setOnRightClickListener {
                appActivity.onRightClicked()
            }
        }
    }

    private fun showLoadingRefresh(it: Boolean?) {
        mLoadingRefreshView?.isRefreshing = it!!
    }

    private fun showLoading(it: Boolean?) {
        mLoadingView?.visibility = if (it!!) View.VISIBLE else View.GONE
        appActivity.showLoading(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getAnnotation<LayoutId>()!!.value, container, false)
    }

    fun toast(@StringRes res: Int) = appActivity.toast(res)

    fun toast(text: String) = appActivity.toast(text)
}