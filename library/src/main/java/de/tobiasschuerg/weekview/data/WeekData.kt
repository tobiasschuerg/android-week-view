package de.tobiasschuerg.weekview.data

import de.tobiasschuerg.weekview.util.TimeSpan
import java.time.LocalTime

/**
 * Container für Events einer Woche bzw. eines beliebigen Datumsbereichs.
 * Nur Events innerhalb des dateRange werden aufgenommen.
 */
class WeekData(val dateRange: LocalDateRange) {
    private val singleEvents: MutableList<Event.Single> = mutableListOf()
    private val allDays: MutableList<Event.AllDay> = mutableListOf()
    private var earliestStart: LocalTime? = null
    private var latestEnd: LocalTime? = null

    fun getTimeSpan(): TimeSpan? {
        val start = earliestStart ?: return null
        val end = latestEnd ?: return null
        // The TimeSpan constructor already validates that the span is not overnight.
        // Since individual events cannot be overnight, the combined span for a day won't be either,
        // unless an event ends exactly at midnight (00:00), which is handled as the end of the day.
        return TimeSpan(start, end)
    }

    fun add(item: Event.AllDay) {
        if (!dateRange.contains(item.date)) throw IllegalArgumentException("Event date is outside the allowed range: ${item.date}")
        allDays.add(item)
    }

    fun add(item: Event.Single) {
        if (!dateRange.contains(item.date)) throw IllegalArgumentException("Event date is outside the allowed range: ${item.date}")
        singleEvents.add(item)
        // Update earliestStart and latestEnd
        if (earliestStart == null || item.timeSpan.start.isBefore(earliestStart)) {
            earliestStart = item.timeSpan.start
        }
        if (latestEnd == null || item.timeSpan.endExclusive.isAfter(latestEnd)) {
            latestEnd = item.timeSpan.endExclusive
        }
    }

    fun getSingleEvents(): List<Event.Single> = singleEvents.toList()

    fun getAllDayEvents(): List<Event.AllDay> = allDays.toList()

    fun isEmpty() = singleEvents.isEmpty() && allDays.isEmpty()

    fun clear() {
        singleEvents.clear()
        allDays.clear()
        earliestStart = null
        latestEnd = null
    }
}
