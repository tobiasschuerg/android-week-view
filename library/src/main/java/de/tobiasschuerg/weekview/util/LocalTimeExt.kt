package de.tobiasschuerg.weekview.util

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val localTimeFormat: DateTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

internal fun LocalTime.toLocalString(): String {
    return localTimeFormat.format(this)
}
