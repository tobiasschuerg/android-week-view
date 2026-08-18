package de.tobiasschuerg.weekview.util

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

private val localTimeFormat: DateTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

internal fun LocalTime.toLocalString(locale: Locale = Locale.getDefault()): String {
    return localTimeFormat.withLocale(locale).format(this)
}
