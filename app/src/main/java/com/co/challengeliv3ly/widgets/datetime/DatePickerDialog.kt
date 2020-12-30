package com.co.challengeliv3ly.widgets.datetime

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.support.core.base.BaseActivity
import com.co.challengeliv3ly.R
import com.co.challengeliv3ly.extensions.AppConst.DateTime.FORMAT_DAY_OF_WEEK
import com.co.challengeliv3ly.extensions.AppConst.DateTime.FORMAT_MONTH_STRING
import java.text.SimpleDateFormat
import java.util.*

class DatePickerDialog(context: Context) {
    private var mOnDatePickedListener: ((year: Int, month: Int, monthString: String, day: Int, dayOfWeek: String) -> Unit)? =
        null
    private val mCalendar = Calendar.getInstance()

    @SuppressLint("SimpleDateFormat")
    private val mDialog = DatePickerDialog(
        context, R.style.DatePickerDialogTheme,
        { _, year, month, dayOfMonth ->
            val formatDayOfWeek = SimpleDateFormat(FORMAT_DAY_OF_WEEK)
            val formatMonthString = SimpleDateFormat(FORMAT_MONTH_STRING)
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val date = calendar.time
            val dayOfWeek = formatDayOfWeek.format(date)
            val stringMonth = formatMonthString.format(date)
            mOnDatePickedListener?.invoke(year, month, stringMonth, dayOfMonth, dayOfWeek)
        },
        mCalendar.get(Calendar.YEAR),
        mCalendar.get(Calendar.MONTH),
        mCalendar.get(Calendar.DATE)
    )

    init {
        (context as BaseActivity).lifeRegister.onDestroy { mDialog.dismiss() }
    }

    fun show(function: (year: Int, month: Int, monthString: String, day: Int, dayOfWeek: String) -> Unit) {
        mOnDatePickedListener = function
        mDialog.show()
    }
}
