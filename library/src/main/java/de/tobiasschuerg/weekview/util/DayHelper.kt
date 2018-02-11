package de.tobiasschuerg.weekview.util

import java.text.DateFormatSymbols
import java.util.*

internal object DayHelper {

    val fistDayOfTheWeek = Calendar.getInstance().firstDayOfWeek

    // TODO: can this be replaced with something type safe?
    fun createListStartingOn(firstDay: Int = fistDayOfTheWeek): List<Int> {
        val days = ArrayList<Int>(7)
        val weekdays: Array<out String> = DateFormatSymbols().weekdays
        var id = firstDay
        while (id - firstDay < 7) {
            val dayId = (id - 1) % 7 + 1
            val dayName: String = weekdays[dayId]
            if (dayName.isNotBlank()) {
                days.add(dayId)
            } else {
                throw IllegalStateException("Invalid day id: " + dayId)
            }
            id++
        }
        return days
    }

}