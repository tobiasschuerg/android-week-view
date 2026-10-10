package de.tobiasschuerg.weekview.internal.time

import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * The formatters that split one locale's short time into an [AxisTimeLabel].
 *
 * Building them means looking up and rewriting the locale's pattern, which costs far more than
 * formatting a time, so they are built once per locale, see [toAxisLabel].
 */
internal class AxisLabelFormat(
    private val time: DateTimeFormatter,
    /** Null in 24-hour locales, which have no AM/PM marker. */
    private val dayPeriod: DateTimeFormatter?,
) {
    fun format(value: LocalTime): AxisTimeLabel = AxisTimeLabel(time = time.format(value), dayPeriod = dayPeriod?.format(value))
}
