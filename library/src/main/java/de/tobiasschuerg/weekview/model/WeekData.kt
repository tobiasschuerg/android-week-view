package de.tobiasschuerg.weekview.model

import androidx.compose.runtime.Immutable
import java.time.LocalTime

/**
 * The events of a week (or any date range) to show, plus the time range that is visible at least.
 *
 * Immutable: to change what the week view shows, pass a new instance. Timed events outside
 * [start]..[end] widen the visible [timeSpan]. Every event must fall within [dateRange] (multi-day
 * events must overlap it), and event IDs must be unique across all event types.
 */
@Immutable
public data class WeekData(
    val dateRange: LocalDateRange,
    val start: LocalTime,
    val end: LocalTime,
    val events: List<Event> = emptyList(),
) {
    val singleEvents: List<Event.Single> = events.filterIsInstance<Event.Single>()
    val allDayEvents: List<Event.AllDay> = events.filterIsInstance<Event.AllDay>()
    val multiDayEvents: List<Event.MultiDay> = events.filterIsInstance<Event.MultiDay>()

    /** [start]..[end], widened to fit all timed events; null if that range is empty. */
    val timeSpan: TimeSpan?

    init {
        events.forEach(::requireWithinDateRange)
        val duplicateIds = events.groupingBy { it.id }.eachCount().filterValues { it > 1 }.keys
        require(duplicateIds.isEmpty()) { "Event IDs must be unique, but $duplicateIds are used more than once" }

        val earliestStart = singleEvents.minOfOrNull { it.timeSpan.start }?.let { minOf(it, start) } ?: start
        val latestEnd = singleEvents.maxOfOrNull { it.timeSpan.endExclusive }?.let { maxOf(it, end) } ?: end
        timeSpan = if (earliestStart.isBefore(latestEnd)) TimeSpan(earliestStart, latestEnd) else null
    }

    public fun isEmpty(): Boolean = events.isEmpty()

    private fun requireWithinDateRange(event: Event) {
        when (event) {
            is Event.MultiDay ->
                require(event.date <= dateRange.endInclusive && event.lastDate >= dateRange.start) {
                    "MultiDay event (${event.date}..${event.lastDate}) does not overlap with the allowed range: $dateRange"
                }
            else -> require(event.date in dateRange) { "Event date ${event.date} is outside the allowed range: $dateRange" }
        }
    }
}
