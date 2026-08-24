package de.tobiasschuerg.weekview.data

import de.tobiasschuerg.weekview.util.TimeSpan
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

class WeekDataTest {
    private lateinit var weekData: WeekData
    private val dateRange = LocalDateRange(LocalDate.of(2024, 9, 1), LocalDate.of(2024, 9, 7))

    @BeforeEach
    fun setUp() {
        weekData = WeekData(dateRange, start = LocalTime.of(9, 0), end = LocalTime.of(9, 0))
    }

    @Test
    fun `add single event updates time span`() {
        val event =
            Event.Single(
                id = "1",
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
                id = "2",
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
    fun `adding and clearing events increments the change version`() {
        val initialVersion = weekData.changeVersion
        weekData.add(
            Event.AllDay(
                id = "20",
                date = LocalDate.of(2024, 9, 3),
                title = "Changed",
                shortTitle = "C",
                textColor = 0,
                backgroundColor = 0,
            ),
        )

        assertEquals(initialVersion + 1, weekData.changeVersion)

        weekData.clear()

        assertEquals(initialVersion + 2, weekData.changeVersion)
    }

    @Test
    fun `duplicate event ids are rejected across event types`() {
        assertThrows<IllegalArgumentException> {
            weekData.add(
                Event.Single(
                    id = "21",
                    date = LocalDate.of(2024, 9, 2),
                    title = "Timed",
                    shortTitle = "T",
                    timeSpan = TimeSpan.of(LocalTime.of(9, 0), Duration.ofHours(1)),
                    backgroundColor = 0,
                    textColor = 0,
                ),
            )

            weekData.add(
                Event.AllDay(
                    id = "21",
                    date = LocalDate.of(2024, 9, 3),
                    title = "All day",
                    shortTitle = "AD",
                    textColor = 0,
                    backgroundColor = 0,
                ),
            )
        }
    }

    @Test
    fun `clear removes all events`() {
        val event =
            Event.Single(
                id = "3",
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
    fun `add multi-day event is stored correctly`() {
        val event =
            Event.MultiDay(
                id = "10",
                date = LocalDate.of(2024, 9, 2),
                title = "Conference",
                shortTitle = "Conf",
                lastDate = LocalDate.of(2024, 9, 4),
                textColor = 0,
                backgroundColor = 0,
            )
        weekData.add(event)
        assertEquals(1, weekData.getMultiDayEvents().size)
        assertEquals("Conference", weekData.getMultiDayEvents()[0].title)
    }

    @Test
    fun `multi-day event partially overlapping start is accepted`() {
        val event =
            Event.MultiDay(
                id = "11",
                date = LocalDate.of(2024, 8, 30),
                title = "Overlap Start",
                shortTitle = "OS",
                lastDate = LocalDate.of(2024, 9, 2),
                textColor = 0,
                backgroundColor = 0,
            )
        weekData.add(event)
        assertEquals(1, weekData.getMultiDayEvents().size)
    }

    @Test
    fun `multi-day event partially overlapping end is accepted`() {
        val event =
            Event.MultiDay(
                id = "12",
                date = LocalDate.of(2024, 9, 5),
                title = "Overlap End",
                shortTitle = "OE",
                lastDate = LocalDate.of(2024, 9, 10),
                textColor = 0,
                backgroundColor = 0,
            )
        weekData.add(event)
        assertEquals(1, weekData.getMultiDayEvents().size)
    }

    @Test
    fun `multi-day event fully outside date range throws exception`() {
        assertThrows<IllegalArgumentException> {
            weekData.add(
                Event.MultiDay(
                    id = "13",
                    date = LocalDate.of(2024, 8, 1),
                    title = "Outside",
                    shortTitle = "Out",
                    lastDate = LocalDate.of(2024, 8, 3),
                    textColor = 0,
                    backgroundColor = 0,
                ),
            )
        }
    }

    @Test
    fun `multi-day event with lastDate before date throws exception`() {
        assertThrows<IllegalArgumentException> {
            weekData.add(
                Event.MultiDay(
                    id = "14",
                    date = LocalDate.of(2024, 9, 4),
                    title = "Invalid",
                    shortTitle = "Inv",
                    lastDate = LocalDate.of(2024, 9, 2),
                    textColor = 0,
                    backgroundColor = 0,
                ),
            )
        }
    }

    @Test
    fun `isEmpty returns false when only multi-day events exist`() {
        weekData.add(
            Event.MultiDay(
                id = "15",
                date = LocalDate.of(2024, 9, 1),
                title = "Test",
                shortTitle = "T",
                lastDate = LocalDate.of(2024, 9, 3),
                textColor = 0,
                backgroundColor = 0,
            ),
        )
        assertFalse(weekData.isEmpty())
    }

    @Test
    fun `clear removes multi-day events`() {
        weekData.add(
            Event.MultiDay(
                id = "16",
                date = LocalDate.of(2024, 9, 1),
                title = "Test",
                shortTitle = "T",
                lastDate = LocalDate.of(2024, 9, 3),
                textColor = 0,
                backgroundColor = 0,
            ),
        )
        weekData.clear()
        assertTrue(weekData.isEmpty())
        assertTrue(weekData.getMultiDayEvents().isEmpty())
    }

    @Test
    fun `add event outside date range throws exception`() {
        val event =
            Event.Single(
                id = "4",
                // outside range
                date = LocalDate.of(2024, 8, 31),
                title = "Outside",
                shortTitle = "O",
                timeSpan = TimeSpan.of(LocalTime.of(12, 0), Duration.ofHours(1)),
                backgroundColor = 0,
                textColor = 0,
            )
        val exception =
            assertThrows<IllegalArgumentException> {
                weekData.add(event)
            }
        assertTrue(exception.message!!.contains("outside the allowed range"))
    }
}
