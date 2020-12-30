package com.co.challengeliv3ly.widgets.datetime

import android.app.TimePickerDialog
import android.content.Context
import android.support.core.base.BaseActivity
import com.co.challengeliv3ly.R
import com.co.challengeliv3ly.extensions.formatTime
import java.util.*

class TimePickerDialog(context: Context) {
    private var mOnTimePickedListener: ((hours: String, hoursDisplay: String, minute: String, type: String) -> Unit)? =
        null

    private var mCalendar = Calendar.getInstance()
    private val mDialog =
        TimePickerDialog(context, R.style.DatePickerDialogTheme, { _, hourOfDay, minute ->
            var hours = hourOfDay
            var type = "AM"

            if (hourOfDay > 12) {
                hours = hourOfDay % 12
                type = "PM"
            }

            mOnTimePickedListener?.invoke(
                hourOfDay.toString(),
                hours.formatTime(),
                minute.formatTime(),
                type
            )
        }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true)

    init {
        (context as BaseActivity).lifeRegister.onDestroy { mDialog.dismiss() }
    }

    fun show(function: (hours: String, hoursDisplay: String, minute: String, type: String) -> Unit) {
        mOnTimePickedListener = function
        mDialog.show()
    }
}
