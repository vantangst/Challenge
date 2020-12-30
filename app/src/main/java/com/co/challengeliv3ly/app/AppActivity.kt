package com.co.challengeliv3ly.app

import android.app.ActivityManager
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.core.annotations.LayoutId
import android.support.core.base.BaseActivity
import android.support.core.base.BaseViewModel
import android.support.core.extensions.getAnnotation
import android.support.core.extensions.getFirstGenericParameter
import android.support.core.extensions.inject
import android.support.core.extensions.observe
import android.support.core.factory.ViewModelFactory
import android.support.core.utils.DriverUtils
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.ShareCompat
import androidx.lifecycle.ViewModelProvider
import com.co.challengeliv3ly.R
import com.co.challengeliv3ly.datasource.AppCache
import com.co.challengeliv3ly.datasource.AppEvent
import com.co.challengeliv3ly.extensions.*
import com.co.challengeliv3ly.views.activity.MainActivity
import com.co.challengeliv3ly.views.dialogs.AppInfoDialog
import com.co.challengeliv3ly.views.dialogs.CommonDialog
import com.co.challengeliv3ly.widgets.loading.LoadingDialog
import java.net.ConnectException
import java.net.UnknownHostException

abstract class AppActivity<VM : BaseViewModel> : BaseActivity() {
    lateinit var viewModel: VM
        private set
    var isLoading: Boolean = false
        private set
    lateinit var rootView: View

    val appEvent: AppEvent by inject()
    val appCache: AppCache by inject()
    val mHandler by lazy { Handler() }
    val appSettings by lazy { AppSettings(this) }
    val appPermission by lazy { AppPermission(this) }

    private val mLoadingDialog by lazy { LoadingDialog(this) }
    val commonDialog by lazy { CommonDialog(this) }
    private val appSessionExpiredDialog by lazy { AppInfoDialog(this) }
    val appInfoDialog by lazy { AppInfoDialog(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        disableAutoFill()
//        setStatusBarColor(R.color.gray_light)
        getAnnotation<LayoutId>()?.apply { setContentView(value) }
        @Suppress("UNCHECKED_CAST")
        viewModel = ViewModelProvider(this, ViewModelFactory.sInstance)
            .get(getFirstGenericParameter<VM>())
        rootView = findViewById(android.R.id.content)

        viewModel.loading.observe(this, this::showLoading)
        viewModel.error.observe(this, this::handleError)
        appSessionExpiredDialog.onCloseListener = {
            if (!isFinishing) {
                //goto login activity
                appCache.logout()
            }
        }
    }

    fun showLoading(aBoolean: Boolean?) {
        val isShown = mLoadingDialog.isShowing
        if (aBoolean!! && !isShown)
            mLoadingDialog.show()
        else if (isShown) mLoadingDialog.dismiss()
    }

    fun handleError(error: Throwable?) {
        if (error?.cause is ConnectException) appInfoDialog.run {
            title("Connection failed")
            showWithMessage("Please check your connection and try again")
        } else when (error) {
            is NotAuthenticatedException -> {
                appSessionExpiredDialog.setTextClose(R.string.btn_ok)
                appSessionExpiredDialog.showWithMessage("Login session expired. Please log in again.")
            }
            is ResourceException -> toast(error.resource)
            is ViewResourceException -> errorView(error)
            is ApiException -> showDialogError(error.message)
            is NetworkException -> appInfoDialog.showWithMessage(error.message)
            is UnknownHostException -> checkNetWork()
            else -> showDialogError(error?.message ?: "Server error")
        }
    }


    private fun showDialogError(text: String?) {
        if (text != null) {
            if (text.contains("hostname")) {
                appInfoDialog.showWithMessage(R.string.message_not_wifi)
                return
            } else if (text.contains("Token is expired.")) {
                appInfoDialog.onCloseListener = {
                    if (!isFinishing) {
                        //goto login activity
                        appCache.logout()
                    }
                }
            }
        }
        appInfoDialog.showWithMessage(text)
    }

    private fun checkNetWork() {
        if (DriverUtils.isNetworkEnabled(this))
            appInfoDialog.showWithMessage("Internal Service Error")
        else appInfoDialog.showWithMessage("No network connection")
    }

    private fun errorView(error: ViewResourceException) {
        (findViewById<View>(error.viewId) as EditText).apply {
            if (error.stringRes == 0) this.error = " "
            else this.error = getString(error.stringRes)
            requestFocus()
        }
    }

    fun toast(@StringRes res: Int) {
        Toast.makeText(this, res, Toast.LENGTH_SHORT).show()
    }

    fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun disableAutoFill() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            window.decorView.importantForAutofill =
                View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
    }

    override fun onActivityBackPressed() {
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val cn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.appTasks[0].taskInfo.topActivity
        } else {
            am.getRunningTasks(1)[0].topActivity
        }
        if (cn?.className == MainActivity::class.java.name) {
            commonDialog.apply { setMessageDialog(R.string.message_exit_app) }
                .show {
                    mHandler.postDelayed({
                        super.onExitApplication()
                    }, 500)
                }
        } else super.onFinishActivityBackPressed()
    }

    /**
     * Clear focus on touch outside for all EditText inputs.
     */
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    open fun onHomeClicked() {

    }

    open fun onRightClicked() {
        ShareCompat.IntentBuilder.from(this)
            .setType("text/plain")
            .setChooserTitle(getString(R.string.title_invite_your_friend))
            .setText("${getString(R.string.share_app_content)} https://play.google.com/store/apps/details?id=" + this.packageName)
            .startChooser()
    }

}