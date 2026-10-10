package de.tobiasschuerg.weekview.internal.layout

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GridGeometryTest {
    // Grid starting at 08:00 with 100 px per hour.
    private val geometry = GridGeometry(startMinute = 8 * 60f, rowHeightPx = 100f)

    @Test
    fun `should map a content offset to the time of day when inside the grid`() {
        assertEquals(9 * 60f + 30f, geometry.minuteAt(150f))
    }

    @Test
    fun `should map a time of day to its content offset when inside the grid`() {
        assertEquals(150f, geometry.yOf(9 * 60f + 30f))
    }

    @Test
    fun `should return the original time when mapping there and back`() {
        assertEquals(13 * 60f + 45f, geometry.minuteAt(geometry.yOf(13 * 60f + 45f)), 0.001f)
    }
}
