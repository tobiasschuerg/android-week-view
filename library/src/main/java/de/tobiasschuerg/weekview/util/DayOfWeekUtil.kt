package de.tobiasschuerg.weekview.util

import org.threeten.bp.DayOfWeek
import org.threeten.bp.temporal.WeekFields
import java.util.*

internal object DayOfWeekUtil {

    /**
     * Creates a list of all week days with a given start day.
     */
    fun createList(firstDay: DayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek): List<DayOfWeek> {
        return (0..6L).toList().map { firstDay.plus(it) }
    }

    /**
     * Maps a [DayOfWeek] to the corresponding column.
     * Considers the first day of the week for the given [Locale],
     * as well as if a day is 'enabled' and skips it.
     */
    fun mapDayToColumn(calendarDay: DayOfWeek, saturdayEnabled: Boolean, sundayEnabled: Boolean): Int {
        val firstDayOfTheWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek

        if (calendarDay == DayOfWeek.SATURDAY && !saturdayEnabled) {
            throw java.lang.IllegalStateException("Passed saturday although it is disabled")
        }

        if (calendarDay == DayOfWeek.SUNDAY && !sundayEnabled) {
            throw java.lang.IllegalStateException("Passed sunday although it is disabled")
        }

        when (firstDayOfTheWeek) {

            DayOfWeek.MONDAY -> {
                // mo: 0, fr:4, su:6
                val column = calendarDay.value
                return if (!saturdayEnabled && calendarDay == DayOfWeek.SUNDAY) {
                    5
                } else {
                    column - 1
                }
            }

            DayOfWeek.SATURDAY -> {
                // sa: 0, su: 1, fr: 6,
                if (saturdayEnabled) {
                    return if (sundayEnabled) {
                        when (calendarDay) {
                            DayOfWeek.SATURDAY -> 0
                            DayOfWeek.SUNDAY -> 1
                            else -> calendarDay.value + 1
                        }
                    } else {
                        return when (calendarDay) {
                            DayOfWeek.SATURDAY -> 0
                            else -> calendarDay.value
                        }
                    }
                } else {
                    return if (sundayEnabled) {
                        when (calendarDay) {
                            DayOfWeek.SUNDAY -> 0
                            else -> calendarDay.value
                        }
                    } else {
                        return calendarDay.value - 1
                    }
                }
            }

            DayOfWeek.SUNDAY -> {
                return if (sundayEnabled) {
                    // su: 0, mo: 1 fr: 5, sa: 6
                    if (calendarDay == DayOfWeek.SUNDAY) {
                        0
                    } else {
                        calendarDay.value
                    }
                } else {
                    // mo: 0 fr: 4, sa: 5, su: -1
                    calendarDay.value - 1
                }
            }
            else -> throw IllegalStateException("$firstDayOfTheWeek das is not supported as start day")
        }
    }

}