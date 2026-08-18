package de.tobiasschuerg.weekview.util

import org.junit.Assert.assertEquals
import org.junit.Test
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
}
