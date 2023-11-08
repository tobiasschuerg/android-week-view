package de.tobiasschuerg.weekview.util

import org.threeten.bp.DayOfWeek
import org.threeten.bp.temporal.WeekFields
import java.util.Locale

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
    fun mapDayToColumn(day: DayOfWeek, saturdayEnabled: Boolean, sundayEnabled: Boolean): Int {
        val firstDayOfTheWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek

        if (day == DayOfWeek.SATURDAY && !saturdayEnabled) {
            throw java.lang.IllegalStateException("Passed saturday although it is disabled")
        }

        if (day == DayOfWeek.SUNDAY && !sundayEnabled) {
            throw java.lang.IllegalStateException("Passed sunday although it is disabled")
        }

        when (firstDayOfTheWeek) {
            DayOfWeek.MONDAY -> {
                // mo: 0, fr:4, su:6
                val column = day.value
                return if (!saturdayEnabled && day == DayOfWeek.SUNDAY) {
                    5
                } else {
                    column - 1
                }
            }

            DayOfWeek.SATURDAY -> {
                // sa: 0, su: 1, fr: 6,
                if (saturdayEnabled) {
                    return if (sundayEnabled) {
                        when (day) {
                            DayOfWeek.SATURDAY -> 0
                            DayOfWeek.SUNDAY -> 1
                            else -> day.value + 1
                        }
                    } else {
                        return when (day) {
                            DayOfWeek.SATURDAY -> 0
                            else -> day.value
                        }
                    }
                } else {
                    return if (sundayEnabled) {
                        when (day) {
                            DayOfWeek.SUNDAY -> 0
                            else -> day.value
                        }
                    } else {
                        return day.value - 1
                    }
                }
            }

            DayOfWeek.SUNDAY -> {
                return if (sundayEnabled) {
                    // su: 0, mo: 1 fr: 5, sa: 6
                    if (day == DayOfWeek.SUNDAY) {
                        0
                    } else {
                        day.value
                    }
                } else {
                    // mo: 0 fr: 4, sa: 5, su: -1
                    day.value - 1
                }
            }
            else -> throw IllegalStateException("$firstDayOfTheWeek das is not supported as start day")
        }
    }
}
