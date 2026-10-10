package de.tobiasschuerg.weekview.internal.time

import java.time.LocalTime
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

private val localTimeFormat: DateTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

/**
 * The AM/PM field of a localized pattern together with the space next to it, e.g. the ` a` of `h:mm a`.
 *
 * Newer CLDR data (Android 14+, JDK 20+) separates the field with a narrow no-break space (U+202F),
 * which `\s` does not match, so the no-break spaces are listed explicitly.
 */
private val dayPeriodFieldRegex = Regex("""[\s\u00A0\u202F]?a+[\s\u00A0\u202F]?""")

/** Quoted literals of a pattern, which may contain pattern letters that are not fields. */
private val quotedLiteralRegex = Regex("'[^']*'")

internal fun LocalTime.toLocalString(locale: Locale = Locale.getDefault()): String {
    return localTimeFormat.withLocale(locale).format(this)
}

/**
 * Splits the localized time into the clock time and, in 12-hour locales, the AM/PM marker, so the
 * axis can stack them instead of needing a gutter wide enough for `10:58 PM` on one line.
 */
internal fun LocalTime.toAxisLabel(locale: Locale = Locale.getDefault()): AxisTimeLabel =
    axisLabelFormats.getOrPut(locale) { axisLabelFormat(locale) }.format(this)

/** One [AxisLabelFormat] per locale; the axis formats the same hours again on every zoom step. */
private val axisLabelFormats = ConcurrentHashMap<Locale, AxisLabelFormat>()

private fun axisLabelFormat(locale: Locale): AxisLabelFormat {
    val pattern = shortTimePattern(locale)
    if (!pattern.usesDayPeriod()) return AxisLabelFormat(time = localTimeFormat.withLocale(locale), dayPeriod = null)
    return AxisLabelFormat(
        time = DateTimeFormatter.ofPattern(pattern.withoutDayPeriod(), locale),
        dayPeriod = DateTimeFormatter.ofPattern("a", locale),
    )
}

/** Removes the AM/PM field and the space next to it from a localized time pattern. */
internal fun String.withoutDayPeriod(): String = replace(dayPeriodFieldRegex, "")

private fun shortTimePattern(locale: Locale): String =
    DateTimeFormatterBuilder.getLocalizedDateTimePattern(null, FormatStyle.SHORT, IsoChronology.INSTANCE, locale)

/** Whether the pattern renders an AM/PM marker outside of a quoted literal. */
private fun String.usesDayPeriod(): Boolean = replace(quotedLiteralRegex, "").contains('a')

/** Whether this time lies strictly inside [start]..[end], i.e. where the current-time indicator is shown. */
internal fun LocalTime.isStrictlyBetween(
    start: LocalTime,
    end: LocalTime,
): Boolean = isAfter(start) && isBefore(end)
