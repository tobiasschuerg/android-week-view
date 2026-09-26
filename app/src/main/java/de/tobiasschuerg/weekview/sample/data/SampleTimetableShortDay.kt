package de.tobiasschuerg.weekview.sample.data

import androidx.core.graphics.toColorInt
import de.tobiasschuerg.weekview.data.LocalDateRange
import de.tobiasschuerg.weekview.data.WeekData
import java.time.LocalTime

/**
 * Short-day sample: a morning-only schedule (09:00–12:00) that is far shorter than the screen.
 * With `fillViewport` enabled the grid is padded with empty hour rows below noon; toggle
 * "Fill viewport" in the menu to see the grid stop right after the last entry instead.
 */
object SampleTimetableShortDay {
    fun create(dateRange: LocalDateRange): WeekData {
        val days = dateRange.toList()
        val weekData = WeekData(dateRange, LocalTime.of(9, 0), LocalTime.of(12, 0))

        var nextId = 600L
        days.forEachIndexed { index, day ->
            weekData.add(lesson(nextId++, day, "Morning Class", "Class", "Room 1", 9, 0, 10, 0, "#00695C".toColorInt()))
            if (index % 2 == 0) {
                weekData.add(lesson(nextId++, day, "Workshop", "Shop", "Lab", 10, 15, 11, 45, "#EF6C00".toColorInt()))
            } else {
                weekData.add(lesson(nextId++, day, "Tutorial", "Tut", "Room 4", 10, 30, 12, 0, "#283593".toColorInt()))
            }
        }
        return weekData
    }
}
