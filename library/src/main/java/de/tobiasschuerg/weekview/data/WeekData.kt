package de.tobiasschuerg.weekview.data

import org.threeten.bp.LocalTime

class WeekData {

    private val singleEvents: MutableList<Event.Single> = mutableListOf()

    private val allDays: MutableList<Event.AllDay> = mutableListOf()

    var earliestStart: LocalTime = LocalTime.MAX

    var latestEnd: LocalTime = LocalTime.MIN

    fun add(item: Event.AllDay) {
        allDays.add(item)
    }

    fun add(item: Event.Single) {
        singleEvents.add(item)

        if (item.startTime.isBefore(earliestStart)) {
            earliestStart = item.startTime
        }

        if (item.endTime.isAfter(latestEnd)) {
            latestEnd = item.endTime
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