package com.co.challengeliv3ly.utils

import com.co.challengeliv3ly.extensions.AppConst.DateTime.FORMAT_DATE_APP
import com.co.challengeliv3ly.extensions.AppConst.DateTime.FORMAT_DATE_SERVER
import com.co.challengeliv3ly.extensions.AppConst.DateTime.FORMAT_DATE_TIME_SERVER
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object DateTimeUtil {

    fun getDate(dateString: String, currentFormat: String): Date? {
        val sdf = SimpleDateFormat(currentFormat)
        var d: Date? = null
        try {
            d = sdf.parse(dateString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return d
    }

    fun getDateByFormat(format: String, date: Date): String {
        val sdf = SimpleDateFormat(format)
        return sdf.format(date)
    }

    //format date server theo dinh dang moi
    fun formatDateServer(dateFormate: String, newFormat: String): String {
        val sdf = SimpleDateFormat(FORMAT_DATE_SERVER)
        val d: Date
        try {
            d = sdf.parse(dateFormate)
            sdf.applyPattern(newFormat)
            return sdf.format(d)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return ""
    }

    fun formatDateApp(dateFormate: String): String {
        val inputFormat = SimpleDateFormat(FORMAT_DATE_APP, Locale.getDefault())
        val outputFormat = SimpleDateFormat(FORMAT_DATE_SERVER, Locale.getDefault())
        var date: Date? = null
        try {
            date = inputFormat.parse(dateFormate)
        } catch (e: Exception) {
        }
        return outputFormat.format(date!!)
    }

    //format theo ngay gio cua server theo dinh dang moi
    fun formatDateTimeServer(dateFormate: String, newFormat: String): String {
        val sdf = SimpleDateFormat(FORMAT_DATE_TIME_SERVER)
        val d: Date
        try {
            d = sdf.parse(dateFormate)
            sdf.applyPattern(newFormat)
            return sdf.format(d)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return ""
    }

    /**
     * Date sever @date : 2019-08-06 00:00:00
     * Time @time : 18:30
     * Convert to: 2019-08-06 18:30:00
     */
    fun convertDateServer(date: String, time: String): String {
        return formatDateServer(date, FORMAT_DATE_SERVER) + " " + time + ":00"
    }

    /**
     * Kiem tra ngay co phai la qua khu so voi ngay hien tai tren phone
     */
    fun isPastTime(yyyymmddhhmmss: String): Boolean {
        val sdf = SimpleDateFormat(FORMAT_DATE_TIME_SERVER)
        try {
            return sdf.parse(yyyymmddhhmmss).time < System.currentTimeMillis()
        } catch (e: ParseException) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * Kiem tra ngay co phai la ngay hien tai tren phone
     */
    fun isPresentTime(yyyymmddhhmmss: String): Boolean {
        val sdf = SimpleDateFormat(FORMAT_DATE_TIME_SERVER)
        try {
            return sdf.parse(yyyymmddhhmmss).time == System.currentTimeMillis()
        } catch (e: ParseException) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * Kiem tra ngay co phai la tuong lai so voi ngay hien tai tren phone
     */
    fun isFutureTime(yyyymmddhhmmss: String): Boolean {
        val sdf = SimpleDateFormat(FORMAT_DATE_TIME_SERVER)
        try {
            return sdf.parse(yyyymmddhhmmss).time > System.currentTimeMillis()
        } catch (e: ParseException) {
            e.printStackTrace()
            return false
        }
    }

    //dua vao ngay gio server va timezone cua dien thoai de format theo dinh dang moi
    fun formatDateTimeFlowTimezone(dateFormate: String, newFormat: String): String {
        val sdfServer = SimpleDateFormat(FORMAT_DATE_TIME_SERVER)
        sdfServer.timeZone =
            TimeZone.getTimeZone("UTC") //server hien tai dang o 'timezone' => 'Asia/Ho_Chi_Minh',
        val dateServer: Date

        val sdfLocal = SimpleDateFormat(newFormat)
        sdfLocal.timeZone = TimeZone.getDefault()
        val dateLocal: String

        try {
            dateServer = sdfServer.parse(dateFormate)
            dateLocal = sdfLocal.format(dateServer)
            return dateLocal
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return ""
    }

    fun getDateInputForServer(dayOfMonth: Int, monthOfYear: Int, year: Int): String {
        return year.toString() + "-" + String.format("%02d", monthOfYear + 1) + "-" + String.format(
            "%02d",
            dayOfMonth
        )
    }

    fun getDateForView(dayOfMonth: Int, monthOfYear: Int, year: Int): String {
        val tempDate = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString() + ""
        val tempMonth =
            if (monthOfYear < 2) "0" + (monthOfYear + 1) else (monthOfYear + 1).toString() + ""
        return "$tempDate-$tempMonth-$year"
    }

    /**
     * input @hhmmss: HH:mm:ss (23:35:59)
     */
    fun getLongTimeMilisecond(hhmmss: String): Long {
        val split = hhmmss.split(":")
        if (split.size == 3) {
            val hour = split[0].toLong()
            val minute = split[1].toLong()
            val second = split[2].toLong()

            val mili = (hour * 60 * 60 + minute * 60 + second) * 1000
            return mili
        }
        return -1
    }
}
