package com.dam.entregapp.logic.utils

import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

object Calculations {
    private fun timeStampToString(timeStamp: Long): String {

        val stamp = Timestamp(timeStamp)
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val date = sdf.format(Date(stamp.time))

        return date.toString()
    }

    fun cleanDate(_day: Int, _month: Int, _year: Int): String {
        var day = _day.toString()
        var month = _month.toString()

        if (_day < 10) {
            day = "0$_day"
        }

        if (_month < 9) { //Because the month instance we retrieve starts at 0 and it's stupid!
            month = "0${_month + 1}"
        } else if (_month >= 9 && _month <= 11) {
            month = (_month + 1).toString()
        }

        return "$day/$month/$_year"
    }

    fun cleanTime(_hour: Int, _minute: Int): String {
        var hour = _hour.toString()
        var minute = _minute.toString()

        if (_hour < 10) {
            hour = "0$_hour"
        }
        if (_minute < 10) {
            minute = "0$_minute"
        }
        return "$hour:$minute"
    }
}
