package com.co.challengeliv3ly.app


import android.Manifest
import android.support.core.base.BaseActivity
import android.support.core.helpers.PermissionChecker

class AppPermission(activity: BaseActivity) : PermissionChecker(activity) {

    fun checkLocation(function: (Boolean) -> Unit) {
        check(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION, onPermission = function
        )
    }

    fun accessLocation(function: () -> Unit) {
        access(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, onAccess = function
        )
    }

    fun accessStorage(function: () -> Unit) {
        access(Manifest.permission.READ_EXTERNAL_STORAGE, onAccess = function)
    }

    fun accessWriteStorage(function: () -> Unit) {
        access(Manifest.permission.WRITE_EXTERNAL_STORAGE, onAccess = function)
    }

    fun accessCamera(function: () -> Unit) {
        access(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            onAccess = function
        )
    }

    fun accessContact(function: () -> Unit) {
        access(Manifest.permission.READ_CONTACTS, onAccess = function)
    }
}