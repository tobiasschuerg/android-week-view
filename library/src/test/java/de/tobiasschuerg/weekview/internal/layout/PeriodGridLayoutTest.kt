package de.tobiasschuerg.weekview.internal.layout

import androidx.compose.ui.unit.dp
import de.tobiasschuerg.weekview.model.Period
import de.tobiasschuerg.weekview.model.TimeSpan
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalTime

class PeriodGridLayoutTest {
    // 1: 08:00-08:45, 2: 08:50-09:35, break 09:35-09:55, 3: 09:55-10:40
    private val periods =
        listOf(
            period("1", 8, 0, 8, 45),
            period("2", 8, 50, 9, 35),
            period("Break", 9, 35, 9, 55, isBreak = true),
            period("3", 9, 55, 10, 40),
        )
    private val layout = PeriodGridLayout.of(periods, periodHeight = 60.dp, breakHeight = 20.dp)

    @Test
    fun `should stack lessons and breaks without gaps when built`() {
        assertEquals(listOf(60.dp, 60.dp, 20.dp, 60.dp), layout.heights)
        assertEquals(listOf(0.dp, 60.dp, 120.dp, 140.dp), layout.tops)
        assertEquals(200.dp, layout.height)
    }

    @Test
    fun `should grow only the lessons when the grid must fill a taller minimum height`() {
        val filled = PeriodGridLayout.of(periods, periodHeight = 60.dp, breakHeight = 20.dp, minHeight = 320.dp)

        assertEquals(listOf(100.dp, 100.dp, 20.dp, 100.dp), filled.heights)
        assertEquals(320.dp, filled.height)
    }

    @Test
    fun `should keep the period height when it already fills the minimum height`() {
        val filled = PeriodGridLayout.of(periods, periodHeight = 60.dp, breakHeight = 20.dp, minHeight = 100.dp)

        assertEquals(layout, filled)
    }

    @Test
    fun `should fill one row when an event matches a period`() {
        assertEquals(0..0, layout.rowsOf(span(8, 0, 8, 45)))
    }

    @Test
    fun `should fill its row when an event starts late and ends early in a period`() {
        assertEquals(1..1, layout.rowsOf(span(9, 0, 9, 20)))
    }

    @Test
    fun `should fill both rows when an event is a double lesson`() {
        assertEquals(0..1, layout.rowsOf(span(8, 0, 9, 35)))
    }

    @Test
    fun `should include the break when an event runs through it`() {
        assertEquals(1..3, layout.rowsOf(span(8, 50, 10, 40)))
    }

    @Test
    fun `should fill the break row when an event lies within the break`() {
        assertEquals(2..2, layout.rowsOf(span(9, 40, 9, 50)))
    }

    @Test
    fun `should not count a touching period when an event ends where the next one starts`() {
        assertEquals(1..1, layout.rowsOf(span(8, 50, 9, 35)))
    }

    @Test
    fun `should snap to the nearest row when an event lies in a gap between periods`() {
        assertEquals(0..0, layout.rowsOf(span(8, 45, 8, 47)))
        assertEquals(1..1, layout.rowsOf(span(8, 48, 8, 50)))
    }

    @Test
    fun `should snap to the first or last row when an event lies outside all periods`() {
        assertEquals(0..0, layout.rowsOf(span(7, 0, 7, 30)))
        assertEquals(3..3, layout.rowsOf(span(14, 0, 15, 0)))
    }

    @Test
    fun `should place a time proportionally within its row when inside a period`() {
        // 08:50 + 22.5 min is halfway through period 2.
        assertEquals(90.dp, layout.yOf(8 * 60f + 50f + 22.5f))
    }

    @Test
    fun `should place a time on the line below a row when it falls in a gap`() {
        assertEquals(60.dp, layout.yOf(8 * 60f + 47f))
    }

    @Test
    fun `should clamp a time to the grid when it is outside all periods`() {
        assertEquals(0.dp, layout.yOf(6 * 60f))
        assertEquals(200.dp, layout.yOf(12 * 60f))
    }

    @Test
    fun `should return the original time when mapping a time inside a row there and back`() {
        val minute = 10 * 60f + 3f
        assertEquals(minute, layout.minuteAt(layout.yOf(minute)), 0.001f)
    }

    @Test
    fun `should clamp an offset to the first start and last end when it is outside the grid`() {
        assertEquals(8 * 60f, layout.minuteAt((-10).dp))
        assertEquals(10 * 60f + 40f, layout.minuteAt(500.dp))
    }

    @Test
    fun `should convert between px and dp with the density when used as geometry`() {
        val geometry = PeriodGridGeometry(layout, density = 2f)

        assertEquals(180f, geometry.yOf(8 * 60f + 50f + 22.5f))
        assertEquals(8 * 60f + 50f + 22.5f, geometry.minuteAt(180f), 0.001f)
    }

    private fun span(
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int,
    ) = TimeSpan(LocalTime.of(startHour, startMinute), LocalTime.of(endHour, endMinute))

    private fun period(
        label: String,
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int,
        isBreak: Boolean = false,
    ) = Period(label, span(startHour, startMinute, endHour, endMinute), isBreak)
}
