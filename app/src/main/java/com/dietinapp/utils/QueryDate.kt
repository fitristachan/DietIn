package com.dietinapp.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun queryMonth(): String {
    val calendar = Calendar.getInstance()
    val formatter = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    return formatter.format(calendar.time)
}

fun queryYear(): String {
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    return currentYear.toString()
}

fun queryToday(): String {
    val calendar = Calendar.getInstance()
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(calendar.time)
}


