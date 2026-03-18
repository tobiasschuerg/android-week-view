package de.tobiasschuerg.weekview.util

import org.junit.Assert.assertEquals
import org.junit.Test

class LocalDateExtTest {
    @Test
    fun `removeYearFromPattern removes year from German short pattern`() {
        assertEquals("dd.MM", removeYearFromPattern("dd.MM.yy"))
    }

    @Test
    fun `removeYearFromPattern removes year from US short pattern`() {
        assertEquals("M/d", removeYearFromPattern("M/d/yy"))
    }

    @Test
    fun `removeYearFromPattern removes year from year-first pattern`() {
        assertEquals("MM/dd", removeYearFromPattern("yy/MM/dd"))
    }

    @Test
    fun `removeYearFromPattern removes year and adjacent quoted label from Russian short pattern`() {
        // Russian SHORT date pattern includes 'г' (abbreviation for год = year)
        // e.g. d.MM.yy 'г'  →  d.MM
        assertEquals("d.MM", removeYearFromPattern("d.MM.yy 'г'"))
    }

    @Test
    fun `removeYearFromPattern removes year and quoted dot-suffixed label`() {
        // e.g. d.MM.yy 'г.'  →  d.MM
        assertEquals("d.MM", removeYearFromPattern("d.MM.yy 'г.'"))
    }

    @Test
    fun `removeYearFromPattern handles four-digit year`() {
        assertEquals("dd.MM", removeYearFromPattern("dd.MM.yyyy"))
    }

    @Test
    fun `removeYearFromPattern handles pattern without year unchanged`() {
        assertEquals("dd.MM", removeYearFromPattern("dd.MM"))
    }
}
