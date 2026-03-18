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
    val noYearPattern = removeYearFromPattern(localizedPattern)
    return format(DateTimeFormatter.ofPattern(noYearPattern, locale))
}

/**
 * Removes the year field and its adjacent separators/quoted labels from a [DateTimeFormatter] pattern.
 *
 * Uses pattern-syntax-aware matching: separators are bare non-letter chars ([^A-Za-z']),
 * and quoted literals ('...') are consumed whole — never split. This prevents
 * locale-specific year labels like Russian 'г' (год = year) from leaving an unclosed
 * string literal in the result.
 */
internal fun removeYearFromPattern(pattern: String): String =
    pattern.replace(Regex("""(?:[^A-Za-z']|'[^']*')*y+(?:[^A-Za-z']|'[^']*')*"""), "")
