@file:Suppress("UNUSED")

package android.support.core.helpers

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.provider.Settings
import android.support.R
import android.support.core.base.BaseActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


open class PermissionChecker(private val activity: BaseActivity) {
    companion object {
        const val REQUEST_PERMISSION_CHECKER = 10000
    }

    private var mOpenSettingDialog: AlertDialog? = null

    protected open var titleDenied = "Permission denied"
    protected open var messageDenied = "You need to allow permission to use this feature"

    protected fun access(vararg permissions: String, onAccess: () -> Unit) {
        check(*permissions) { if (it) onAccess() }
    }

    protected fun check(vararg permissions: String, onPermission: (Boolean) -> Unit) {
        if (permissions.isEmpty()) throw RuntimeException("No permission to check")
        if (isAllowed(*permissions)) onPermission(true) else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[0]))
                showSuggestOpenSetting(permissions, onPermission)
            else request(permissions, onPermission)
        }
    }

    private fun request(permissions: Array<out String>, onPermission: (Boolean) -> Unit) {
        ActivityCompat.requestPermissions(activity, permissions, REQUEST_PERMISSION_CHECKER)
        activity.resultLife.onPermissionsResult { requestCodeReceived, _, grantResults ->
            if (REQUEST_PERMISSION_CHECKER != requestCodeReceived) return@onPermissionsResult
            if (grantResults.isEmpty()) {
                onPermission(false)
                return@onPermissionsResult
            }
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    onPermission(false)
                    return@onPermissionsResult
                }
            }
            onPermission(true)
        }
    }

    private fun isAllowed(vararg permissions: String): Boolean {
        return permissions.fold(true) { acc, permission ->
            acc && ContextCompat.checkSelfPermission(
                activity,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun showSuggestOpenSetting(
        permissions: Array<out String>,
        onPermission: (Boolean) -> Unit
    ) {
        if (mOpenSettingDialog == null) {
            mOpenSettingDialog = AlertDialog.Builder(activity, R.style.MyDialog)
                .setTitle(titleDenied)
                .setMessage(messageDenied)
                .setPositiveButton("Ok") { _: DialogInterface, _: Int ->
                    openSetting(permissions, onPermission)
                }
                .create()
        }
        mOpenSettingDialog?.setOnShowListener {
            mOpenSettingDialog?.getButton(AlertDialog.BUTTON_POSITIVE)
                ?.setTextColor(Color.parseColor("#4987f7"))
        }
        mOpenSettingDialog!!.show()
    }

    private fun openSetting(permissions: Array<out String>, onPermission: (Boolean) -> Unit) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:" + activity.packageName)
        )
        activity.startActivityForResult(intent, REQUEST_PERMISSION_CHECKER)
        activity.resultLife.onActivityResult(REQUEST_PERMISSION_CHECKER) { _, _ ->
            onPermission(isAllowed(*permissions))
        }
    }
}
