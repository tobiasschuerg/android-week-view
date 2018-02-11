package de.tobiasschuerg.timetableview.util

import android.content.Context
import android.text.format.DateFormat.getTimeFormat
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat

fun LocalTime.toLocalString(context: Context): String {
    val sdf = getTimeFormat(context) as SimpleDateFormat
    val format = DateTimeFormatter.ofPattern(sdf.toPattern())
    return format.format(this)
}