package android.support.core.extensions

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityManager
import android.app.KeyguardManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.util.TypedValue
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * Get status bar height
 *
 * @return height of status bar
 */
val Context.statusBarHeight
    get(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }


/**
 * Get Bottom Action bar height
 *
 * @return height of bottom Action bar
 */
val Context.navigationBarHeight
    get(): Int {
        val hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey()
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0 && !hasMenuKey) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }


/**
 * Get size of device screen
 *
 * @param context
 * @return width and height of screen
 */
val Context.windowSize
    get(): Point {
        val display = (this as Activity).windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size
    }


/**
 * Set status bar and bottom action bar transparent
 */
fun Activity.setBarsTransparent() {
    window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
}

/**
 * Hide keyboard from EditText
 */
fun View.showKeyboard(value: Boolean) {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    if (value) {
        requestFocus()
        (this as? EditText)?.setSelection(text.length)
        imm.showSoftInput(this, 0)
    } else imm.hideSoftInputFromWindow(windowToken, 0)
}

val Context.activity: Activity?
    get() = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.activity
        else -> null
    }


/**
 * Hide keyboard from context
 */
fun Context.showKeyboard(value: Boolean) {
    this.activity?.showKeyboard(value)
}

/**
 * Hide Keyboard from view on activity
 */
fun Activity.showKeyboard(value: Boolean) {
    var view = currentFocus as? EditText
    if (view == null) view = EditText(this)
    view.showKeyboard(value)
}

/**
 * Hide Keyboard from view on fragment
 */
fun androidx.fragment.app.Fragment.showKeyboard(value: Boolean) = view!!.showKeyboard(value)

/**
 * Hide System UI of [Activity], make Activity screen as Immersive
 * It include Status bar, Action Bar, Bottom Action bar
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
fun Activity.showSystemUI(isShow: Boolean) {
    if (isShow) window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    else window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN)
}

@SuppressLint("PrivateApi")
private fun Activity.setMIUIStatusBarDarkIcon(darkIcon: Boolean) {
    val clazz = window.javaClass
    try {
        val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
        val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
        val darkModeFlag = field.getInt(layoutParams)
        val extraFlagField = clazz.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
        extraFlagField.invoke(window, if (darkIcon) darkModeFlag else 0, darkModeFlag)
    } catch (e: Exception) {
    }

}

private fun Activity.setMeizuStatusBarDarkIcon(darkIcon: Boolean) {
    try {
        val lp = window.attributes
        val darkFlag = WindowManager.LayoutParams::class.java.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
        val meizuFlags = WindowManager.LayoutParams::class.java.getDeclaredField("meizuFlags")
        darkFlag.isAccessible = true
        meizuFlags.isAccessible = true
        val bit = darkFlag.getInt(null)
        var value = meizuFlags.getInt(lp)
        value = if (darkIcon) {
            value or bit
        } else {
            value and bit.inv()
        }
        meizuFlags.setInt(lp, value)
        window.attributes = lp
    } catch (e: Exception) {
    }

}

@TargetApi(Build.VERSION_CODES.M)
fun Activity.setStatusBarLightMode() {
    setMIUIStatusBarDarkIcon(true)
    setMeizuStatusBarDarkIcon(true)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}

@TargetApi(Build.VERSION_CODES.M)
fun Activity.setStatusBarDarkMode() {
    setMIUIStatusBarDarkIcon(false)
    setMeizuStatusBarDarkIcon(false)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun Activity.setStatusBarColor(@ColorRes colorRes: Int) {
    window.apply {
        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        statusBarColor = ContextCompat.getColor(this@setStatusBarColor, colorRes)
    }

}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun Activity.setStatusBarTextColor(@ColorRes colorRes: Int) {
    window.apply {
        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        statusBarColor = ContextCompat.getColor(this@setStatusBarTextColor, colorRes)
    }
}

private fun Activity.setWindowFlag(bits: Int, on: Boolean) {
    val winParams = window.attributes
    if (on) {
        winParams.flags = winParams.flags or bits
    } else {
        winParams.flags = winParams.flags and bits.inv()
    }
    window.attributes = winParams
}

fun Activity.setTransparentStatusBar() {
    //make translucent statusBar on kitkat devices
    if (Build.VERSION.SDK_INT in 19..20) {
        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
    }
    if (Build.VERSION.SDK_INT >= 19) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
    //make fully Android Transparent Status bar
    if (Build.VERSION.SDK_INT >= 21) {
        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        window.statusBarColor = Color.TRANSPARENT
    }
}

/**
 * Coppy text into clipboard
 *
 * @param text
 */
fun Context.copyToClipboard(text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("message", text)
    clipboard.setPrimaryClip(clip)
}

/**
 * Check App is on foreground
 *
 * @return App in foreground
 */
@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
fun Context.isAppOnForeground(): Boolean {
    val appProcessInfo = ActivityManager.RunningAppProcessInfo()
    ActivityManager.getMyMemoryState(appProcessInfo)
    if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND || appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
        return true
    }
    val km = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    return km.inKeyguardRestrictedInputMode()
}

/**
 * Check App is in background
 *
 * @return App in background
 */
@Suppress("DEPRECATION")
fun Context.isAppIsInBackground(): Boolean {
    var isInBackground = true
    val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
        val runningProcesses = am.runningAppProcesses
        for (processInfo in runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (activeProcess in processInfo.pkgList) {
                    if (activeProcess == packageName) {
                        isInBackground = false
                    }
                }
            }
        }
    } else {
        val taskInfo = am.getRunningTasks(1)
        val componentInfo = taskInfo[0].topActivity
        if (componentInfo?.packageName == packageName) {
            isInBackground = false
        }
    }
    return isInBackground
}

fun Context.isPackageExisted(targetPackage: String): Boolean {
    val pm = packageManager
    try {
        pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA)
    } catch (e: PackageManager.NameNotFoundException) {
        return false
    }
    return true
}

/**
 * Convert dp to pixel
 *
 * @param dp
 * @return Value pixel
 */
fun Context.dpToPx(dp: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)


/**
 * Convert dp to sp
 *
 * @param dp
 * @return value of sp
 */
fun Context.dpToSp(dp: Float) = dpToPx(dp) / resources.displayMetrics.scaledDensity


/**
 * Get Number of column in screen
 *
 * @return number of column
 */
fun Context.getNumOfColumn(): Int {
    val displayMetrics = resources.displayMetrics
    val dpWidth = displayMetrics.widthPixels / displayMetrics.density
    return (dpWidth / 180).toInt()
}



