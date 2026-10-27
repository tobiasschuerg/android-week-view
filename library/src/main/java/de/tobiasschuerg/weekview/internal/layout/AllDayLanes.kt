package de.tobiasschuerg.weekview.internal.layout

import de.tobiasschuerg.weekview.model.Event
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** An all-day or multi-day [event] clipped to the visible days: it covers [spanDays] columns from [startIndex]. */
internal data class AllDayLaneItem(
    val event: Event,
    val startIndex: Int,
    val spanDays: Int,
)

/**
 * Packs all-day and multi-day events into lanes where no two events in the same lane overlap, so a
 * single-day event fills a free column next to a multi-day bar instead of opening a lane of its own.
 * Events are sorted by start date, then by span length (longest first) for stable packing.
 */
internal object AllDayLanes {
    fun pack(
        events: List<Event>,
        firstDay: LocalDate,
        lastDay: LocalDate,
    ): List<List<AllDayLaneItem>> {
        val items =
            events
                .mapNotNull { it.toLaneItem(firstDay, lastDay) }
                .sortedWith(compareBy<AllDayLaneItem> { it.startIndex }.thenByDescending { it.spanDays })

        val lanes = mutableListOf<MutableList<AllDayLaneItem>>()
        for (item in items) {
            val lane = lanes.firstOrNull { lane -> lane.none { it.overlaps(item) } }
            if (lane != null) {
                lane.add(item)
            } else {
                lanes.add(mutableListOf(item))
            }
        }
        return lanes
    }

    private fun Event.toLaneItem(
        firstDay: LocalDate,
        lastDay: LocalDate,
    ): AllDayLaneItem? {
        val end =
            when (this) {
                is Event.AllDay -> date
                is Event.MultiDay -> lastDate
                is Event.Single -> return null
            }
        val clippedStart = maxOf(date, firstDay)
        val clippedEnd = minOf(end, lastDay)
        if (clippedStart > clippedEnd) return null
        return AllDayLaneItem(
            event = this,
            startIndex = ChronoUnit.DAYS.between(firstDay, clippedStart).toInt(),
            spanDays = ChronoUnit.DAYS.between(clippedStart, clippedEnd).toInt() + 1,
        )
    }

    private fun AllDayLaneItem.overlaps(other: AllDayLaneItem): Boolean =
        startIndex < other.startIndex + other.spanDays && other.startIndex < startIndex + spanDays
}
