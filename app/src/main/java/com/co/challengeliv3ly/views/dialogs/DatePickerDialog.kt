package com.co.challengeliv3ly.views.dialogs

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.support.core.lifecycle.LifeRegister
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.co.challengeliv3ly.R
import com.co.challengeliv3ly.app.AppActivity
import com.co.challengeliv3ly.extensions.AppConst.DateTime.FORMAT_DATE_APP
import com.co.challengeliv3ly.extensions.deviceWidth
import com.co.challengeliv3ly.extensions.toDateBirthday
import java.text.SimpleDateFormat
import java.util.*

class DatePickerDialog(activity: AppActivity<*>) :
    DatePickerDialog(activity, null, 0, 0, 0),
    DatePicker.OnDateChangedListener {
    private var mView: TextView? = null
    private var mDisableFutureDate = true
    private var mDisablePastDate = false
    private var mEnableSpinner = false
    private var mNumberLimit: Int = 0
    private val mActivity: AppActivity<*>

    init {
        LifeRegister.of(activity).onDestroy { this.dismiss() }
        setCanceledOnTouchOutside(false)
        setCancelable(false)
        mActivity = activity
    }

    /**
     * Limit year old
     */
    fun setLimit(number: Int) {
        mNumberLimit = number
    }

    /**
     * Disable future date
     * default: true
     */
    fun setDisableFutureDates(disableFutureDates: Boolean) {
        mDisableFutureDate = disableFutureDates
    }

    fun setEnableSpinner(enableSpinner: Boolean) {
        mEnableSpinner = enableSpinner
    }

    fun setupClickWithView(view: TextView) {
        mView = view
        view.setOnClickListener {
            this@DatePickerDialog.handleChooseDate(view.text.toString())
        }
    }

    private fun handleChooseDate(timeNow: String) {
        var timeNowL = timeNow
        val calendar = Calendar.getInstance()

        if (timeNowL.equals(" ", ignoreCase = true) || timeNowL.equals("", ignoreCase = true))
            timeNowL = EMPTY_BIRTHDAY
        val year =
            if (timeNowL == EMPTY_BIRTHDAY) calendar.get(Calendar.YEAR) else timeNow.toDateBirthday(
                YEAR
            )
        val date =
            if (timeNowL == EMPTY_BIRTHDAY) calendar.get(Calendar.DATE) else timeNow.toDateBirthday(
                DATE
            )
        val month =
            if (timeNowL == EMPTY_BIRTHDAY) calendar.get(Calendar.MONTH) else timeNow.toDateBirthday(
                MONTH
            ) - 1

        val datePickerDialog: DatePickerDialog
        if (mEnableSpinner)
            datePickerDialog = DatePickerDialog(
                context,
                R.style.AppDatePickerSpinnerDialog,
                OnDateSetListener { _, year1, monthOfYear, dayOfMonth ->
                    calendar.set(year1, monthOfYear, dayOfMonth)
                    val simpleDateFormat = SimpleDateFormat(FORMAT_DATE_APP, Locale.getDefault())
                    display(simpleDateFormat.format(calendar.time))
                },
                year,
                month,
                date
            )
        else datePickerDialog = DatePickerDialog(
            context,
            R.style.AppDatePickerCalendarDialog,
            OnDateSetListener { _, year1, monthOfYear, dayOfMonth ->
                calendar.set(year1, monthOfYear, dayOfMonth)
                val simpleDateFormat = SimpleDateFormat(FORMAT_DATE_APP, Locale.getDefault())
                display(simpleDateFormat.format(calendar.time))
            },
            year,
            month,
            date
        )

        applyConfig(datePickerDialog)
        datePickerDialog.show()
        datePickerDialog.window?.setBackgroundDrawableResource(android.R.color.white)
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(context, R.color.gray))
        datePickerDialog.window?.setLayout(
            mActivity.deviceWidth() - 100,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun display(date: String) {
        mView?.text=date
    }

    private fun applyConfig(datePickerDialog: DatePickerDialog) {
        if (mDisableFutureDate)
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        if (mDisablePastDate)
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        val calendar = Calendar.getInstance()
        val mYear = calendar.get(Calendar.YEAR)
        val mDay = calendar.get(Calendar.DATE)
        val mMonth = calendar.get(Calendar.MONTH)

        val maxDate = Calendar.getInstance()
        maxDate.set(Calendar.DAY_OF_MONTH, mDay)
        maxDate.set(Calendar.MONTH, mMonth)
        maxDate.set(Calendar.YEAR, mYear - mNumberLimit)

        datePicker.maxDate = maxDate.timeInMillis
    }

    companion object {
        private const val MONTH = "MM"
        private const val DATE = "dd"
        private const val YEAR = "yyyy"
        private const val EMPTY_BIRTHDAY = ""
    }
}
