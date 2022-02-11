package de.tobiasschuerg.weekview.util

import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

private val localTimeFormat: DateTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

internal fun LocalTime.toLocalString(): String {
    return localTimeFormat.format(this)
}
