package de.tobiasschuerg.weekview.internal.layout

import androidx.compose.ui.graphics.Color
import de.tobiasschuerg.weekview.model.Event
import de.tobiasschuerg.weekview.model.TimeSpan
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

class AllDayLanesTest {
    private val monday = LocalDate.of(2026, 10, 26)
    private val sunday = monday.plusDays(6)

    @Test
    fun `should put single-day events into the lane of a multi-day event when they do not overlap`() {
        val autumnBreak = multiDay("break", monday, monday.plusDays(4))
        val reformationDay = allDay("reformation", monday.plusDays(5))
        val allSaints = allDay("saints", sunday)

        val lanes = AllDayLanes.pack(listOf(reformationDay, allSaints, autumnBreak), monday, sunday)

        assertEquals(1, lanes.size)
        assertEquals(
            listOf(AllDayLaneItem(autumnBreak, 0, 5), AllDayLaneItem(reformationDay, 5, 1), AllDayLaneItem(allSaints, 6, 1)),
            lanes.single(),
        )
    }

    @Test
    fun `should open a new lane when a single-day event overlaps a multi-day event`() {
        val autumnBreak = multiDay("break", monday, monday.plusDays(4))
        val holiday = allDay("holiday", monday.plusDays(2))

        val lanes = AllDayLanes.pack(listOf(holiday, autumnBreak), monday, sunday)

        assertEquals(listOf(listOf(autumnBreak), listOf(holiday)), lanes.map { lane -> lane.map { it.event } })
    }

    @Test
    fun `should stack single-day events on the same date into separate lanes`() {
        val first = allDay("a", monday)
        val second = allDay("b", monday)

        val lanes = AllDayLanes.pack(listOf(first, second), monday, sunday)

        assertEquals(listOf(listOf(first), listOf(second)), lanes.map { lane -> lane.map { it.event } })
    }

    @Test
    fun `should place the longest event first when events start on the same day`() {
        val short = multiDay("short", monday, monday.plusDays(1))
        val long = multiDay("long", monday, monday.plusDays(3))
        val single = allDay("single", monday.plusDays(2))

        val lanes = AllDayLanes.pack(listOf(short, single, long), monday, sunday)

        assertEquals(listOf(listOf(long), listOf(short, single)), lanes.map { lane -> lane.map { it.event } })
    }

    @Test
    fun `should clip multi-day events to the visible days when they extend beyond the week`() {
        val event = multiDay("long", monday.minusDays(3), sunday.plusDays(2))

        val lanes = AllDayLanes.pack(listOf(event), monday, sunday)

        assertEquals(AllDayLaneItem(event, 0, 7), lanes.single().single())
    }

    @Test
    fun `should skip events outside the visible days and timed events`() {
        val outside = allDay("outside", sunday.plusDays(1))
        val timed =
            Event.Single(
                id = "timed",
                date = monday,
                title = "Timed",
                shortTitle = "T",
                timeSpan = TimeSpan.of(LocalTime.NOON, Duration.ofHours(1)),
                textColor = Color.White,
                backgroundColor = Color.Black,
            )

        assertEquals(emptyList<List<AllDayLaneItem>>(), AllDayLanes.pack(listOf(outside, timed), monday, sunday))
    }

    private fun allDay(
        id: String,
        date: LocalDate,
    ) = Event.AllDay(id, date, id, id, Color.White, Color.Black)

    private fun multiDay(
        id: String,
        date: LocalDate,
        lastDate: LocalDate,
    ) = Event.MultiDay(id, date, id, id, lastDate, Color.White, Color.Black)
}
