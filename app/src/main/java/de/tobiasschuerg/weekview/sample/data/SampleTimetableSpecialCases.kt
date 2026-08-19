package de.tobiasschuerg.weekview.sample.data

import androidx.core.graphics.toColorInt
import de.tobiasschuerg.weekview.data.LocalDateRange
import de.tobiasschuerg.weekview.data.WeekData
import java.time.LocalTime

/**
 * Special Cases sample: entries chosen to stress-test rendering edge cases rather than
 * represent a realistic timetable - very short (10-25 minute) passing periods/breaks/lessons
 * to check the combined time label and priority-based field dropping at small entry heights,
 * plus a pair of long-titled lessons to check that the title wraps to two lines when there's
 * room and ellipsizes to one when there isn't.
 */
object SampleTimetableSpecialCases {
    fun create(dateRange: LocalDateRange): WeekData {
        val days = dateRange.toList()
        val weekData = WeekData(dateRange, LocalTime.of(8, 0), LocalTime.of(11, 0))

        val mon = days[0]
        val tue = days.getOrNull(1)
        val wed = days.getOrNull(2)

        var nextId = 400L

        // Monday: a normal lesson, a passing period, a short lesson, a break, then a normal lesson.
        weekData.add(lesson(nextId++, mon, "Mathematics", "Math", "Room 12", 8, 0, 8, 45, "#1565C0".toColorInt()))
        weekData.add(lesson(nextId++, mon, "Passing Period", "Pass", "", 8, 45, 8, 55, "#78909C".toColorInt()))
        weekData.add(lesson(nextId++, mon, "Quick Quiz", "Quiz", "Room 7", 8, 55, 9, 10, "#2E7D32".toColorInt()))
        weekData.add(lesson(nextId++, mon, "Short Break", "Break", "", 9, 10, 9, 20, "#78909C".toColorInt()))
        weekData.add(lesson(nextId++, mon, "History", "Hist", "Room 21", 9, 20, 10, 5, "#BF360C".toColorInt()))
        weekData.add(lesson(nextId++, mon, "10-min Break", "Break", "", 10, 5, 10, 15, "#78909C".toColorInt()))
        weekData.add(lesson(nextId++, mon, "Science", "Sci", "Lab 2", 10, 15, 11, 0, "#6A1B9A".toColorInt()))

        // Tuesday: back-to-back short (10 min) entries only, to stress-test small entry heights.
        tue?.let { d ->
            var start = 8 to 0
            repeat(6) { index ->
                val (h, m) = start
                val endMinTotal = h * 60 + m + 10
                val endH = endMinTotal / 60
                val endM = endMinTotal % 60
                weekData.add(lesson(nextId++, d, "Slot ${index + 1}", "S${index + 1}", "", h, m, endH, endM, "#E65100".toColorInt()))
                start = endH to endM
            }
        }

        // Wednesday: same long title on a tall entry (wraps to 2 lines) and a short one (ellipsized).
        wed?.let { d ->
            weekData.add(
                lesson(
                    nextId++,
                    d,
                    "Introduction to Machine Learning and Neural Networks",
                    "Intro ML & Neural Networks",
                    "Lab 3",
                    8,
                    0,
                    10,
                    0,
                    "#6A1B9A".toColorInt(),
                ),
            )
            weekData.add(
                lesson(
                    nextId++,
                    d,
                    "Introduction to Machine Learning and Neural Networks",
                    "Intro ML & Neural Networks",
                    "Lab 3",
                    10,
                    15,
                    10,
                    45,
                    "#6A1B9A".toColorInt(),
                ),
            )
        }

        return weekData
    }
}
