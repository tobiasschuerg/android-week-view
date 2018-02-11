package de.tobiasschuerg.weekview.data

import org.threeten.bp.LocalTime

class TimetableData {

    private val lessons: MutableList<Event.Single> = mutableListOf()

    fun getLesssons(): List<Event.Single> = lessons.toList()

    private val allDays: MutableList<Event.AllDay> = mutableListOf()

    fun getHolidays(): List<Event.AllDay> = allDays.toList()

    var earliestStart: LocalTime = LocalTime.MAX
    var latestEnd: LocalTime = LocalTime.MIN

    fun add(item: Event.AllDay) {
        allDays.add(item)
    }

    fun add(item: Event.Single) {
        lessons.add(item)

        if (item.startTime.isBefore(earliestStart)) {
            earliestStart = item.startTime
        }

        if (item.endTime.isAfter(latestEnd)) {
            latestEnd = item.endTime
        }
    }

    fun isEmpty() = lessons.isEmpty() && allDays.isEmpty()
}