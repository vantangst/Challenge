package com.co.challengeliv3ly.views.dialogs

import android.content.Context
import android.net.Uri
import android.os.Build
import android.support.core.extensions.activity
import androidx.fragment.app.Fragment
import com.co.challengeliv3ly.R
import com.co.challengeliv3ly.app.AppActivity
import com.co.challengeliv3ly.app.AppDialog
import kotlinx.android.synthetic.main.dialog_pick_photo.*


class PhotoPickerDialog : AppDialog {
    constructor(context: Context) : super(context)
    constructor(fragment: Fragment) : super(fragment)

    private val mActivity get() = context.activity as AppActivity<*>
    var onPhotoPickedListener: ((Boolean, Uri) -> Unit)? = null
        set(value) {
            field = value
            btnCamera.setOnClickListener {
                mActivity.appPermission.accessCamera {
                    mActivity.appSettings.openCameraForImage { uri ->
                        onPhotoPickedListener?.invoke(true, uri)
                    }
                }
                dismiss()
            }
            btnPhoto.setOnClickListener {
                mActivity.appPermission.accessStorage {
                    mActivity.appSettings.openGalleryForImage { uri ->
                        onPhotoPickedListener?.invoke(false, uri)
                    }
                }
                dismiss()
            }
        }

    var onPhotosPickedListener: ((MutableList<Uri>) -> Unit)? = null
        set(value) {
            field = value
            btnPhoto.setOnClickListener {
                mActivity.appPermission.accessStorage {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        mActivity.appSettings.openGalleryForImages { uris ->
                            onPhotosPickedListener?.invoke(uris)
                        }
                    }
                }
                dismiss()
            }
            btnCamera.setOnClickListener {
                mActivity.appPermission.accessCamera {
                    mActivity.appSettings.openCameraForImage { uri ->
                        val uris: MutableList<Uri> = ArrayList()
                        uris.add(uri)
                        onPhotosPickedListener?.invoke(uris)
                    }
                }
                dismiss()
            }
        }

    init {
        setContentView(R.layout.dialog_pick_photo)
        isSlideShow = true
        btnCancel.setOnClickListener { dismiss() }
    }

    fun shows(function: (MutableList<Uri>) -> Unit) {
        onPhotosPickedListener = function
        super.show()
    }

    fun show(function: (Boolean, Uri) -> Unit) {
        onPhotoPickedListener = function
        super.show()
    }
}
