package com.co.challengeliv3ly.widgets.datetime

import android.content.Context
import android.widget.TextView
import com.co.challengeliv3ly.extensions.toMillisecond

class DateTimeDialog(context: Context) {
    private val datePickerDialog by lazy { DatePickerDialog(context) }
    private val timePickerDialog by lazy { TimePickerDialog(context) }

    private lateinit var dateTime: String
    private var millisTime = 0L

    fun setupClickWithView(textView: TextView) {
        textView.setOnClickListener {
            datePickerDialog.show { year, month, monthString, day, dayOfWeek ->
                dateTime = "$dayOfWeek, $monthString $day"
                timePickerDialog.show { hours, hoursDisplay, minute, type ->
                    dateTime += " - $hoursDisplay:$minute $type"
                    textView.text = dateTime
                    millisTime = "$year-$month-$day $hours:$minute:00".toMillisecond()
                }
            }
        }
    }

    fun getTextTime(): String = dateTime

    fun getMillisTime(): Long = millisTime


}