package de.tobiasschuerg.weekview.data

import de.tobiasschuerg.weekview.util.TimeSpan
import java.time.LocalTime

/**
 * Container for events of a week or any date range.
 * Only events within the dateRange are accepted.
 */
class WeekData(
    val dateRange: LocalDateRange,
    val start: LocalTime,
    val end: LocalTime,
) {
    private val singleEvents: MutableList<Event.Single> = mutableListOf()
    private val allDays: MutableList<Event.AllDay> = mutableListOf()
    private val multiDayEvents: MutableList<Event.MultiDay> = mutableListOf()
    private var earliestStart: LocalTime = start
    private var latestEnd: LocalTime = end

    fun getTimeSpan(): TimeSpan? {
        val start = earliestStart
        val end = latestEnd
        if (!start.isBefore(end)) return null
        return TimeSpan(start, end)
    }

    fun add(item: Event.AllDay) {
        require(dateRange.contains(item.date)) { "Event date is outside the allowed range: ${item.date}" }
        allDays.add(item)
    }

    fun add(item: Event.MultiDay) {
        val overlaps = item.date <= dateRange.endInclusive && item.lastDate >= dateRange.start
        require(overlaps) { "MultiDay event (${item.date}..${item.lastDate}) does not overlap with the allowed range: $dateRange" }
        multiDayEvents.add(item)
    }

    fun add(item: Event.Single) {
        require(dateRange.contains(item.date)) { "Event date ${item.date} is outside the allowed range: $dateRange" }
        singleEvents.add(item)

        // Automatically adjust TimeSpan to accommodate the new event
        updateTimeSpanForEvent(item)
    }

    /**
     * Updates the earliest start and latest end times to accommodate the given event.
     * This ensures that the TimeSpan automatically expands when events are added
     * that fall outside the current time range.
     */
    private fun updateTimeSpanForEvent(event: Event.Single) {
        val eventStart = event.timeSpan.start
        val eventEnd = event.timeSpan.endExclusive

        // Update earliest start if this event starts earlier
        if (eventStart.isBefore(earliestStart)) {
            earliestStart = eventStart
        }

        // Update latest end if this event ends later
        if (eventEnd.isAfter(latestEnd)) {
            latestEnd = eventEnd
        }
    }

    fun getSingleEvents(): List<Event.Single> = singleEvents.toList()

    fun getAllDayEvents(): List<Event.AllDay> = allDays.toList()

    fun getMultiDayEvents(): List<Event.MultiDay> = multiDayEvents.toList()

    fun isEmpty() = singleEvents.isEmpty() && allDays.isEmpty() && multiDayEvents.isEmpty()

    fun clear() {
        singleEvents.clear()
        allDays.clear()
        multiDayEvents.clear()
        earliestStart = start
        latestEnd = end
    }
}
