package com.co.challengeliv3ly.app

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings
import android.support.core.utils.DriverUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.co.challengeliv3ly.R


private const val OPEN_GALLERY = 1
private const val SELECT_MULTIPLE_IMAGE = 2
private const val CAMERA_REQUEST = 3
private const val LOCATION_REQUEST = 4

class AppSettings(val context: AppActivity<*>) {
    companion object {
        private const val REQUEST_CHECK_SETTINGS = 214
        private const val REQUEST_ENABLE_GPS = 516
        private const val RC_OPEN_PLACE_AUTO_COMPLETE = 4
    }

    fun openGalleryForImage(function: (Uri) -> Unit) {
        openGallery("image/*", "Select Picture", function)
    }

    fun openGalleryForVideo(function: (Uri) -> Unit) {
        openGallery("video/*", "Select Videos", function)
    }

    private fun openGallery(format: String, title: String, function: (Uri) -> Unit) {
        val galleryIntent = Intent()
        galleryIntent.type = format
        galleryIntent.action = Intent.ACTION_GET_CONTENT

        context.resultLife.onActivityResult(OPEN_GALLERY) { resultCode, intent ->
            if (resultCode != Activity.RESULT_OK || intent == null) return@onActivityResult
            val data = intent.data ?: return@onActivityResult
            function.invoke(data)
        }

        context.startActivityForResult(Intent.createChooser(galleryIntent, title), OPEN_GALLERY)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun openGalleryForImages(function: (MutableList<Uri>) -> Unit) {
        val galleryIntent = Intent()
        galleryIntent.type = "image/*"
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

        context.resultLife.onActivityResult(SELECT_MULTIPLE_IMAGE) { resultCode, intent ->
            if (resultCode != Activity.RESULT_OK || intent == null) return@onActivityResult
            if (intent.clipData != null) {
                val count = intent.clipData!!.itemCount
                val uris = (0 until count).asSequence()
                    .map { intent.clipData!!.getItemAt(it).uri }
                    .toList()
                function(uris as MutableList<Uri>)
            } else if (intent.data != null) {
                function(arrayListOf(intent.data!!))
            }
        }
        context.startActivityForResult(
            Intent.createChooser(galleryIntent, "Select Picture"),
            SELECT_MULTIPLE_IMAGE
        )
    }

    fun openCameraForBitmap(function: (Bitmap) -> Unit) {
        val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        context.resultLife.onActivityResult(CAMERA_REQUEST) { resultCode, intent ->
            if (resultCode != Activity.RESULT_OK || intent == null) return@onActivityResult
            val bundle = intent.extras
            bundle?.apply {
                val bitmap = get("data") as Bitmap
                function(bitmap)
            }
        }
        context.startActivityForResult(cameraIntent, CAMERA_REQUEST)
    }

    fun openCameraForImage(function: (Uri) -> Unit) {
        val imageURI = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            ContentValues().apply {
                put(MediaStore.Images.Media.TITLE, "New Picture")
                put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
            })
        context.resultLife.onActivityResult(CAMERA_REQUEST) { resultCode, _ ->
            if (resultCode != Activity.RESULT_OK) return@onActivityResult
            try {
//                val thumbnail = MediaStore.Images.Media.getBitmap(context.contentResolver, imageURI)
//                val url = getRealPathFromURI(imageURI!!)
                function(imageURI!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        context.startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, imageURI)
        }, CAMERA_REQUEST)
    }

    fun openLocation(function: (Boolean) -> Unit) {
        if (DriverUtils.isGPSEnabled(context)) {
            function(true)
            return
        }
        LocationServices.getSettingsClient(context).checkLocationSettings(
            LocationSettingsRequest.Builder()
                .setAlwaysShow(true)
                .addLocationRequest(
                    LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(10000)
                        .setFastestInterval((10000 / 2).toLong())
                )
                .build()
        )
            .addOnSuccessListener {
                context.runOnUiThread {
                    function(
                        it.locationSettingsStates.isGpsUsable
                                || it.locationSettingsStates.isLocationUsable
                                || it.locationSettingsStates.isNetworkLocationUsable
                    )
                }
            }.addOnFailureListener {
                when ((it as ApiException).statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> openGpsEnableDialog(
                        it,
                        function
                    )
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> openGpsEnableSetting()
                    else -> context.runOnUiThread { function(false) }
                }
            }.addOnCanceledListener { context.runOnUiThread { function(false) } }

        context.resultLife.onActivityResult(LOCATION_REQUEST) { resultCode, _ ->
            if (resultCode == REQUEST_CHECK_SETTINGS) {
                function(resultCode == Activity.RESULT_OK)
            } else if (resultCode == REQUEST_ENABLE_GPS) {
                function(DriverUtils.isGPSEnabled(context))
            }
        }
    }

    private fun openGpsEnableDialog(it: ApiException, function: (Boolean) -> Unit) {
        if (context.isLoading) context.showLoading(false)
        try {
            it as ResolvableApiException
            it.startResolutionForResult(context, REQUEST_CHECK_SETTINGS)
        } catch (sie: IntentSender.SendIntentException) {
            context.runOnUiThread { function(false) }
        }
    }

    private fun openGpsEnableSetting() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivityForResult(intent, REQUEST_ENABLE_GPS)
    }

    fun openPlaceAutoComplete(placeOriginal: String, function: (Place) -> Unit) {
        var statusCode = -1
        try {
            if (!Places.isInitialized()) {
                Places.initialize(context, context.getString(R.string.api_key))
            }
            val fields = listOf<Place.Field>(
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )
            context.resultLife.onActivityResult { resultCode, intent ->
                if (resultCode == AutocompleteActivity.RESULT_ERROR)
                    Toast.makeText(
                        context, Autocomplete.getStatusFromIntent(intent!!).statusMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                if (resultCode != AutocompleteActivity.RESULT_OK) return@onActivityResult
                function(Autocomplete.getPlaceFromIntent(intent!!))
            }
            val builder = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .setInitialQuery(placeOriginal)
            context.startActivityForResult(
                builder.build(context), RC_OPEN_PLACE_AUTO_COMPLETE
            )

        } catch (exception: GooglePlayServicesRepairableException) {
            statusCode = exception.connectionStatusCode
        } catch (exception: GooglePlayServicesNotAvailableException) {
            statusCode = exception.errorCode
        }
        if (statusCode != -1) {
            GoogleApiAvailability.getInstance().showErrorDialogFragment(context, statusCode, 30422)
        }
    }

    fun getNumberContact(context: Context): MutableList<String> {
        val contactsNumberMap: MutableList<String> = ArrayList()
        val phoneCursor: Cursor? = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        if (phoneCursor != null && phoneCursor.count > 0) {
            val contactIdIndex =
                phoneCursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val numberIndex =
                phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (phoneCursor.moveToNext()) {
                val contactId = phoneCursor.getString(contactIdIndex)
                val number: String = phoneCursor.getString(numberIndex)
                //check if the map contains key or not, if not then create a new array list with number
                contactsNumberMap.add(number)
            }
            //contact contains all the number of a particular contact
            phoneCursor.close()
        }
        return contactsNumberMap
    }

//
//    private fun openGpsEnableDialog(it: ApiException, function: (Boolean) -> Unit) {
//        try {
//            it as ResolvableApiException
//            it.startResolutionForResult(context, REQUEST_CHECK_SETTINGS)
//        } catch (sie: IntentSender.SendIntentException) {
//            context.runOnUiThread { function(false) }
//        }
//    }
//
//    private fun openGpsEnableSetting() {
//        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//        context.startActivityForResult(intent, REQUEST_ENABLE_GPS)
//    }
}

