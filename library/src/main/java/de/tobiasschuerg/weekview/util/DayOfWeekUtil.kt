package de.tobiasschuerg.weekview.util

import java.time.DayOfWeek
import java.time.temporal.WeekFields
import java.util.Locale

/**
 * Utility class for working with DayOfWeek.
 */
internal object DayOfWeekUtil {
    /**
     * Creates a list of all week days starting from the provided [firstDay].
     *
     * @param firstDay The first day of the week.
     * @return A list of DayOfWeek starting from the given first day.
     */
    fun createList(firstDay: DayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek): List<DayOfWeek> {
        return (0..6L).toList().map { firstDay.plus(it) }
    }

    /**
     * Maps a [DayOfWeek] to the corresponding column index in a week view.
     *
     * @param day The DayOfWeek to map.
     * @param saturdayEnabled Indicates if Saturday should be enabled.
     * @param sundayEnabled Indicates if Sunday should be enabled.
     * @return The column index corresponding to the day.
     * @throws IllegalStateException if the provided first day of the week is not supported.
     * @throws IllegalStateException if Saturday or Sunday is passed but not enabled.
     */
    fun mapDayToColumn(
        day: DayOfWeek,
        saturdayEnabled: Boolean,
        sundayEnabled: Boolean,
    ): Int {
        val firstDayOfTheWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek

        when (firstDayOfTheWeek) {
            DayOfWeek.MONDAY -> {
                if (day == DayOfWeek.SATURDAY && !saturdayEnabled) {
                    throw IllegalStateException("Passed Saturday although it is disabled")
                }
                if (day == DayOfWeek.SUNDAY && !sundayEnabled) {
                    throw IllegalStateException("Passed Sunday although it is disabled")
                }
                // mo: 0, tu: 1, ... fr: 4, sa: 5, su: 6
                val column = day.value - 1
                return when {
                    !saturdayEnabled && day == DayOfWeek.SUNDAY -> 5
                    else -> column
                }
            }

            DayOfWeek.SATURDAY -> {
                // sa: 0, su: 1, fr: 6,
                if (day == DayOfWeek.SATURDAY && !saturdayEnabled) {
                    throw IllegalStateException("Passed Saturday although it is disabled")
                }

                if (day == DayOfWeek.SUNDAY && !sundayEnabled) {
                    throw IllegalStateException("Passed Sunday although it is disabled")
                }

                return if (saturdayEnabled) {
                    if (sundayEnabled) {
                        when (day) {
                            DayOfWeek.SATURDAY -> 0
                            DayOfWeek.SUNDAY -> 1
                            else -> day.value + 1
                        }
                    } else {
                        when (day) {
                            DayOfWeek.SATURDAY -> 0
                            else -> day.value
                        }
                    }
                } else {
                    if (sundayEnabled) {
                        when (day) {
                            DayOfWeek.SUNDAY -> 0
                            else -> day.value
                        }
                    } else {
                        day.value - 1
                    }
                }
            }

            DayOfWeek.SUNDAY -> {
                if (day == DayOfWeek.SATURDAY && !saturdayEnabled) {
                    throw IllegalStateException("Passed Saturday although it is disabled")
                }
                if (day == DayOfWeek.SUNDAY && !sundayEnabled) {
                    throw IllegalStateException("Passed Sunday although it is disabled")
                }
                return if (sundayEnabled) {
                    if (saturdayEnabled) {
                        // su: 0, mo: 1, ... fr: 5, sa: 6
                        if (day == DayOfWeek.SUNDAY) 0 else day.value
                    } else {
                        // su: 0, mo: 1, ... fr: 5 (saturday disabled)
                        if (day == DayOfWeek.SUNDAY) 0 else day.value
                    }
                } else {
                    if (saturdayEnabled) {
                        // mo: 0, ... fr: 4, sa: 5 (sunday disabled)
                        day.value - 1
                    } else {
                        // mo: 0, ... fr: 4 (both disabled)
                        day.value - 1
                    }
                }
            }
            else -> throw IllegalStateException("$firstDayOfTheWeek is not supported as start day")
        }
    }
}
