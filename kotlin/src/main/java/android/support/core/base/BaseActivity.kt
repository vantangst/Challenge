package android.support.core.base

import android.content.Intent
import android.support.core.extensions.findChildVisible
import android.support.core.extensions.isVisibleOnScreen
import android.support.core.functional.Backable
import android.support.core.functional.Dispatcher
import android.support.core.lifecycle.LifeRegister
import android.support.core.lifecycle.ResultLifecycle
import android.support.core.lifecycle.ResultRegistry
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment


abstract class BaseActivity : AppCompatActivity(), Dispatcher {

    val resultLife: ResultLifecycle = ResultRegistry()
    val lifeRegister by lazy { LifeRegister.of(this) }

    override fun onBackPressed() {
        var backed = false
        for (fragment in supportFragmentManager.fragments) {
            if (fragment is Backable && fragment.isVisibleOnScreen()) backed = backed || fragment.onBackPressed()
            else if (fragment is NavHostFragment)
                backed = backed || (fragment.findChildVisible() as? Backable)?.onBackPressed() ?: false
        }
        if (!backed) onActivityBackPressed()
    }

    protected open fun onActivityBackPressed() {}

    protected open fun onFinishActivityBackPressed() {
        super.onBackPressed()
    }

    protected open fun onExitApplication() {
        android.os.Process.killProcess(android.os.Process.myPid())
        super.onBackPressed()
    }

    fun findFragmentById(id: Int) = supportFragmentManager.findFragmentById(id)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        (resultLife as ResultRegistry).handleActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        (resultLife as ResultRegistry).handlePermissionsResult(requestCode, permissions, grantResults)
    }
}