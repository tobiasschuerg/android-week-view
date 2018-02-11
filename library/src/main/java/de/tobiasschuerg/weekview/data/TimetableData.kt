package de.tobiasschuerg.weekview.data

import org.threeten.bp.LocalTime

class TimetableData {

    private val lessons: MutableList<TimetableItem.Regular> = mutableListOf()

    fun getLesssons(): List<TimetableItem.Regular> = lessons.toList()

    private val holidays: MutableList<TimetableItem.Holiday> = mutableListOf()

    fun getHolidays(): List<TimetableItem.Holiday> = holidays.toList()

    var earliestStart: LocalTime = LocalTime.MAX
    var latestEnd: LocalTime = LocalTime.MIN

    fun add(item: TimetableItem.Holiday) {
        holidays.add(item)
    }

    fun add(item: TimetableItem.Regular) {
        lessons.add(item)

        if (item.lesson.startTime.isBefore(earliestStart)) {
            earliestStart = item.lesson.startTime
        }

        if (item.lesson.endTime.isAfter(latestEnd)) {
            latestEnd = item.lesson.endTime
        }
    }

    fun isEmpty() = lessons.isEmpty() && holidays.isEmpty()
}