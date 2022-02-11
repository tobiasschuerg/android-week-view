package de.tobiasschuerg.weekview.data

import de.tobiasschuerg.weekview.util.TimeSpan
import org.threeten.bp.LocalTime

class WeekData {

    private val singleEvents: MutableList<Event.Single> = mutableListOf()
    private val allDays: MutableList<Event.AllDay> = mutableListOf()
    private var earliestStart: LocalTime? = null
    private var latestEnd: LocalTime? = null

    fun getTimeSpan(): TimeSpan? {
        return if (earliestStart != null && latestEnd != null) {
            TimeSpan(earliestStart!!, latestEnd!!)
        } else {
            null
        }
    }

    fun add(item: Event.AllDay) {
        allDays.add(item)
    }

    fun add(item: Event.Single) {
        singleEvents.add(item)

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
    }
}
