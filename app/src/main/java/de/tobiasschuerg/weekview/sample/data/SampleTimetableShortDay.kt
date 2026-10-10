package de.tobiasschuerg.weekview.sample.data

import androidx.compose.ui.graphics.Color
import de.tobiasschuerg.weekview.model.Event
import de.tobiasschuerg.weekview.model.LocalDateRange
import de.tobiasschuerg.weekview.model.WeekData
import java.time.LocalTime

/**
 * Short-day sample: a morning-only schedule (09:00–12:00) that is far shorter than the screen.
 * With `fillViewport` enabled the grid is padded with empty hour rows below noon; toggle
 * "Fill viewport" in the menu to see the grid stop right after the last entry instead.
 */
object SampleTimetableShortDay {
    fun create(dateRange: LocalDateRange): WeekData {
        val days = dateRange.toList()
        val events = mutableListOf<Event>()

        var nextId = 600L
        days.forEachIndexed { index, day ->
            events.add(lesson(nextId++, day, "Morning Class", "Class", "Room 1", 9, 0, 10, 0, Color(0xFF00695C)))
            if (index % 2 == 0) {
                events.add(lesson(nextId++, day, "Workshop", "Shop", "Lab", 10, 15, 11, 45, Color(0xFFEF6C00)))
            } else {
                events.add(lesson(nextId++, day, "Tutorial", "Tut", "Room 4", 10, 30, 12, 0, Color(0xFF283593)))
            }
        }
        return WeekData(dateRange, LocalTime.of(9, 0), LocalTime.of(12, 0), events)
    }
}
