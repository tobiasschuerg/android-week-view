package de.tobiasschuerg.weekview.data

import de.tobiasschuerg.weekview.util.TimeSpan
import java.time.LocalTime

/**
 * Container for events of a week or any date range.
 * Only events within the dateRange are accepted.
 */
data class WeekData(
    val dateRange: LocalDateRange,
    val start: LocalTime,
    val end: LocalTime,
    private val singleEvents: List<Event.Single> = emptyList(),
    private val allDays: List<Event.AllDay> = emptyList(),
    private val earliestStart: LocalTime = start,
    private val latestEnd: LocalTime = end,
) {

    fun getTimeSpan(): TimeSpan? {
        val start = earliestStart
        val end = latestEnd
        // The TimeSpan constructor already validates that the span is not overnight.
        // Since individual events cannot be overnight, the combined span for a day won't be either,
        // unless an event ends exactly at midnight (00:00), which is handled as the end of the day.
        return TimeSpan(start, end)
    }

    fun add(item: Event.AllDay): WeekData {
        if (!dateRange.contains(item.date)) throw IllegalArgumentException("Event date is outside the allowed range: ${item.date}")
        return copy(allDays = allDays + item)
    }

    fun add(item: Event.Single): WeekData {
        if (!dateRange.contains(item.date)) {
            throw IllegalArgumentException("Event date ${item.date} is outside the allowed range: $dateRange")
        }
        
        // Calculate new time bounds
        val eventStart = item.timeSpan.start
        val eventEnd = item.timeSpan.endExclusive
        val newEarliestStart = if (eventStart.isBefore(earliestStart)) eventStart else earliestStart
        val newLatestEnd = if (eventEnd.isAfter(latestEnd)) eventEnd else latestEnd

        return copy(
            singleEvents = singleEvents + item,
            earliestStart = newEarliestStart,
            latestEnd = newLatestEnd
        )
    }

    fun getSingleEvents(): List<Event.Single> = singleEvents

    fun getAllDayEvents(): List<Event.AllDay> = allDays

    fun isEmpty() = singleEvents.isEmpty() && allDays.isEmpty()

    fun clear(): WeekData = copy(
        singleEvents = emptyList(),
        allDays = emptyList(),
        earliestStart = start,
        latestEnd = end
    )
}
