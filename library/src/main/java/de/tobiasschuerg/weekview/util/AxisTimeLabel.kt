package de.tobiasschuerg.weekview.util

/**
 * A time split into the parts the time axis stacks on top of each other: `7:00` with `AM` below it.
 *
 * [dayPeriod] is null in 24-hour locales, where the whole label is a single line.
 */
internal data class AxisTimeLabel(
    val time: String,
    val dayPeriod: String?,
)
