package de.tobiasschuerg.weekview.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalTime
import java.util.Locale

class LocalTimeExtTest {
    @Test
    fun `toLocalString formats using the requested locale, not the JVM default`() {
        val time = LocalTime.of(9, 5)

        assertEquals("9:05 AM", time.toLocalString(Locale.US))
        assertEquals("09:05", time.toLocalString(Locale.GERMANY))
    }

    @Test
    fun `toLocalString defaults to the JVM default locale when none is given`() {
        val defaultLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale.GERMANY)
            assertEquals(LocalTime.of(14, 30).toLocalString(Locale.GERMANY), LocalTime.of(14, 30).toLocalString())
        } finally {
            Locale.setDefault(defaultLocale)
        }
    }

    @Test
    fun `toAxisLabel splits off the AM PM marker in 12-hour locales`() {
        assertEquals(AxisTimeLabel(time = "1:00", dayPeriod = "PM"), LocalTime.of(13, 0).toAxisLabel(Locale.US))
        assertEquals(AxisTimeLabel(time = "12:05", dayPeriod = "AM"), LocalTime.of(0, 5).toAxisLabel(Locale.US))
    }

    @Test
    fun `toAxisLabel keeps a single line in 24-hour locales`() {
        assertEquals(AxisTimeLabel(time = "13:00", dayPeriod = null), LocalTime.of(13, 0).toAxisLabel(Locale.GERMANY))
        assertEquals(AxisTimeLabel(time = "00:05", dayPeriod = null), LocalTime.of(0, 5).toAxisLabel(Locale.GERMANY))
    }

    @Test
    fun `toAxisLabel keeps the clock time free of the day period in every locale`() {
        val locales = listOf(Locale.US, Locale.UK, Locale.GERMANY, Locale.FRANCE, Locale.JAPAN, Locale.KOREA)

        locales.forEach { locale ->
            val label = LocalTime.of(13, 45).toAxisLabel(locale)

            assertFalse(label.time.contains(label.dayPeriod ?: "@"), "$locale rendered its day period twice")
        }
    }

    @Test
    fun `should strip the day period when it is separated by a narrow no-break space`() {
        assertEquals("h:mm", "h:mm\u202Fa".withoutDayPeriod())
        assertEquals("h:mm", "h:mm\u00A0a".withoutDayPeriod())
        assertEquals("h:mm", "h:mm a".withoutDayPeriod())
        assertEquals("h:mm", "a h:mm".withoutDayPeriod())
    }

    @Test
    fun `should report a time strictly inside the bounds as between them`() {
        assertTrue(LocalTime.of(9, 30).isStrictlyBetween(LocalTime.of(8, 0), LocalTime.of(12, 0)))
    }

    @Test
    fun `should not report a time on or outside the bounds as between them`() {
        val start = LocalTime.of(8, 0)
        val end = LocalTime.of(12, 0)

        assertFalse(start.isStrictlyBetween(start, end))
        assertFalse(end.isStrictlyBetween(start, end))
        assertFalse(LocalTime.of(7, 59).isStrictlyBetween(start, end))
        assertFalse(LocalTime.of(12, 1).isStrictlyBetween(start, end))
    }

    @Test
    fun `should format each locale with its own pattern when locales alternate`() {
        val time = LocalTime.of(13, 5)

        repeat(2) {
            assertEquals(AxisTimeLabel(time = "1:05", dayPeriod = "PM"), time.toAxisLabel(Locale.US))
            assertEquals(AxisTimeLabel(time = "13:05", dayPeriod = null), time.toAxisLabel(Locale.GERMANY))
        }
    }
}
