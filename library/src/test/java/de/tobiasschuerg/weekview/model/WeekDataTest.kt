package de.tobiasschuerg.weekview.model

import androidx.compose.ui.graphics.Color
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

class WeekDataTest {
    private val dateRange = LocalDateRange(LocalDate.of(2024, 9, 1), LocalDate.of(2024, 9, 7))

    private fun weekData(
        vararg events: Event,
        start: LocalTime = LocalTime.of(9, 0),
        end: LocalTime = LocalTime.of(17, 0),
    ) = WeekData(dateRange, start, end, events.toList())

    private fun single(
        id: String = "s",
        date: LocalDate = LocalDate.of(2024, 9, 2),
        start: LocalTime = LocalTime.of(10, 0),
        duration: Duration = Duration.ofHours(1),
    ) = Event.Single(
        id = id,
        date = date,
        title = "Timed",
        shortTitle = "T",
        timeSpan = TimeSpan.of(start, duration),
        textColor = Color.White,
        backgroundColor = Color.Blue,
    )

    private fun allDay(
        id: String = "a",
        date: LocalDate = LocalDate.of(2024, 9, 3),
    ) = Event.AllDay(id = id, date = date, title = "All day", shortTitle = "AD", textColor = Color.White, backgroundColor = Color.Blue)

    private fun multiDay(
        id: String = "m",
        date: LocalDate,
        lastDate: LocalDate,
    ) = Event.MultiDay(
        id = id,
        date = date,
        lastDate = lastDate,
        title = "Multi day",
        shortTitle = "MD",
        textColor = Color.White,
        backgroundColor = Color.Blue,
    )

    @Test
    fun `should keep the configured time span when all timed events fit into it`() {
        val data = weekData(single(start = LocalTime.of(10, 0)))

        assertEquals(TimeSpan(LocalTime.of(9, 0), LocalTime.of(17, 0)), data.timeSpan)
    }

    @Test
    fun `should widen the time span when timed events start earlier or end later`() {
        val data =
            weekData(
                single(id = "early", start = LocalTime.of(7, 30)),
                single(id = "late", start = LocalTime.of(18, 0), duration = Duration.ofMinutes(90)),
            )

        assertEquals(TimeSpan(LocalTime.of(7, 30), LocalTime.of(19, 30)), data.timeSpan)
    }

    @Test
    fun `should have no time span when start equals end and there are no timed events`() {
        val data = weekData(allDay(), start = LocalTime.of(9, 0), end = LocalTime.of(9, 0))

        assertNull(data.timeSpan)
    }

    @Test
    fun `should split the events by type when created from a mixed list`() {
        val timed = single()
        val holiday = allDay()
        val trip = multiDay(date = LocalDate.of(2024, 9, 4), lastDate = LocalDate.of(2024, 9, 6))

        val data = weekData(trip, holiday, timed)

        assertEquals(listOf(timed), data.singleEvents)
        assertEquals(listOf(holiday), data.allDayEvents)
        assertEquals(listOf(trip), data.multiDayEvents)
    }

    @Test
    fun `should be equal when created from the same events`() {
        assertEquals(weekData(single(), allDay()), weekData(single(), allDay()))
    }

    @Test
    fun `should be empty when created without events`() {
        assertTrue(weekData().isEmpty())
        assertFalse(weekData(allDay()).isEmpty())
    }

    @Test
    fun `should reject events when the same id is used across event types`() {
        assertThrows<IllegalArgumentException> { weekData(single(id = "21"), allDay(id = "21")) }
    }

    @Test
    fun `should accept events from different sources when their ids are namespaced`() {
        val data = weekData(single(id = "lesson-12"), allDay(id = "holiday-12"))

        assertEquals(1, data.singleEvents.size)
        assertEquals(1, data.allDayEvents.size)
    }

    @Test
    fun `should reject a timed event when its date is outside the date range`() {
        assertThrows<IllegalArgumentException> { weekData(single(date = LocalDate.of(2024, 9, 8))) }
    }

    @Test
    fun `should reject an all-day event when its date is outside the date range`() {
        assertThrows<IllegalArgumentException> { weekData(allDay(date = LocalDate.of(2024, 8, 31))) }
    }

    @Test
    fun `should accept a multi-day event when it only overlaps the start of the date range`() {
        val data = weekData(multiDay(date = LocalDate.of(2024, 8, 30), lastDate = LocalDate.of(2024, 9, 2)))

        assertEquals(1, data.multiDayEvents.size)
    }

    @Test
    fun `should accept a multi-day event when it only overlaps the end of the date range`() {
        val data = weekData(multiDay(date = LocalDate.of(2024, 9, 6), lastDate = LocalDate.of(2024, 9, 10)))

        assertEquals(1, data.multiDayEvents.size)
    }

    @Test
    fun `should reject a multi-day event when it lies fully outside the date range`() {
        assertThrows<IllegalArgumentException> {
            weekData(multiDay(date = LocalDate.of(2024, 9, 10), lastDate = LocalDate.of(2024, 9, 12)))
        }
    }

    @Test
    fun `should reject a multi-day event when its last date is before its first date`() {
        assertThrows<IllegalArgumentException> { multiDay(date = LocalDate.of(2024, 9, 5), lastDate = LocalDate.of(2024, 9, 4)) }
    }
}
