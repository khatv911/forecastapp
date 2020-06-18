package com.kay.forecast.utils

import java.text.SimpleDateFormat
import java.util.*

fun Long.millisToDate(formatStr: String = "EEE, dd MMM yyyy"): String {
    val sdf = SimpleDateFormat(formatStr, Locale.getDefault())
    return sdf.format(Date(this))
}