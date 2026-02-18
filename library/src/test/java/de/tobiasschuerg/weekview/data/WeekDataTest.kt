package de.tobiasschuerg.weekview.data

import de.tobiasschuerg.weekview.util.TimeSpan
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

class WeekDataTest {
    private lateinit var weekData: WeekData
    private val dateRange = LocalDateRange(LocalDate.of(2024, 9, 1), LocalDate.of(2024, 9, 7))

    @Before
    fun setUp() {
        weekData = WeekData(dateRange, start = LocalTime.of(9, 0), end = LocalTime.of(9, 0))
    }

    @Test
    fun `add single event updates time span`() {
        val event =
            Event.Single(
                id = 1L,
                date = LocalDate.of(2024, 9, 2),
                title = "Test",
                shortTitle = "T",
                timeSpan = TimeSpan.of(LocalTime.of(8, 0), Duration.ofHours(2)),
                backgroundColor = 0,
                textColor = 0,
            )
        weekData.add(event)
        val timeSpan = weekData.getTimeSpan()
        assertNotNull(timeSpan)
        assertEquals(LocalTime.of(8, 0), timeSpan?.start)
        assertEquals(LocalTime.of(10, 0), timeSpan?.endExclusive)
    }

    @Test
    fun `add all day event is stored correctly`() {
        val event =
            Event.AllDay(
                id = 2L,
                date = LocalDate.of(2024, 9, 3),
                title = "AllDay",
                shortTitle = "AD",
                textColor = 0,
                backgroundColor = 0,
            )
        weekData.add(event)
        assertEquals(1, weekData.getAllDayEvents().size)
    }

    @Test
    fun `clear removes all events`() {
        val event =
            Event.Single(
                id = 3L,
                date = LocalDate.of(2024, 9, 4),
                title = "ClearTest",
                shortTitle = "CT",
                timeSpan = TimeSpan.of(LocalTime.of(9, 0), Duration.ofHours(1)),
                backgroundColor = 0,
                textColor = 0,
            )
        weekData.add(event)
        weekData.clear()
        assertTrue(weekData.isEmpty())
    }

    @Test
    fun `add event outside date range throws exception`() {
        val event =
            Event.Single(
                id = 4L,
                // outside range
                date = LocalDate.of(2024, 8, 31),
                title = "Outside",
                shortTitle = "O",
                timeSpan = TimeSpan.of(LocalTime.of(12, 0), Duration.ofHours(1)),
                backgroundColor = 0,
                textColor = 0,
            )
        try {
            weekData.add(event)
            // If no exception is thrown, fail the test
            assertTrue("Expected IllegalArgumentException was not thrown", false)
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("outside the allowed range"))
        }
    }
}
