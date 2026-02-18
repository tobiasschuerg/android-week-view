package de.tobiasschuerg.weekview.util

import java.time.LocalDate
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.util.Locale

/**
 * Formats this date as a short locale-aware string without the year component.
 * Uses the locale's SHORT date pattern with the year portion stripped.
 *
 * Example outputs: "2/18" (US), "18.02." (Germany), "18/02" (UK)
 */
internal fun LocalDate.toShortDateStringWithoutYear(): String {
    val locale = Locale.getDefault()
    val localizedPattern =
        DateTimeFormatterBuilder.getLocalizedDateTimePattern(
            FormatStyle.SHORT,
            null,
            IsoChronology.INSTANCE,
            locale,
        )
    val noYearPattern = localizedPattern.replace(Regex("\\W*y+\\W*"), "")
    return format(DateTimeFormatter.ofPattern(noYearPattern, locale))
}
