package de.tobiasschuerg.weekview.sample.data

import android.graphics.Color
import androidx.core.graphics.toColorInt
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.LocalDateRange
import de.tobiasschuerg.weekview.data.WeekData
import de.tobiasschuerg.weekview.util.TimeSpan
import java.time.LocalTime

/**
 * Special Cases sample: entries chosen to stress-test rendering edge cases rather than
 * represent a realistic timetable.
 *
 * - Monday/Tuesday: very short (10-25 minute) passing periods, breaks and lessons to check
 *   the shrunk title, the corner time labels and priority-based field dropping.
 * - Wednesday: the same long title on a tall entry (wraps to two lines) and a short one
 *   (ellipsized), plus a tall entry carrying every field (room, teacher, lower text).
 * - Thursday: a 30-60 minute duration ladder with rooms and dark text on bright fills -
 *   the range in which start/end switch between corner and stacked mode and the room
 *   appears - so the transitions can be checked at any zoom level and system font scale.
 * - Friday: overlapping entries, so the corner labels and shrunk titles are also checked
 *   in half-width columns, including a 10-minute duty on top of a lesson.
 */
object SampleTimetableSpecialCases {
    fun create(dateRange: LocalDateRange): WeekData {
        val days = dateRange.toList()
        val weekData = WeekData(dateRange, LocalTime.of(8, 0), LocalTime.of(11, 0))

        val mon = days[0]
        val tue = days.getOrNull(1)
        val wed = days.getOrNull(2)
        val thu = days.getOrNull(3)
        val fri = days.getOrNull(4)

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

            // Tall entry with every field so the lowest-priority tiers (teacher, lower text) show.
            weekData.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Physics Lab",
                    shortTitle = "Phys Lab",
                    subTitle = "Lab B01",
                    upperText = "Dr. Braun",
                    lowerText = "Bring goggles",
                    timeSpan = TimeSpan(LocalTime.of(11, 0), LocalTime.of(12, 30)),
                    textColor = Color.WHITE,
                    backgroundColor = "#00838F".toColorInt(),
                ),
            )
        }

        // Thursday: duration ladder 30 -> 60 min in 5-min steps with 5-min gaps, dark text on bright fills.
        thu?.let { d ->
            val fills = listOf("#FFE000", "#00AEFF", "#2FFF2F", "#FFE000", "#00AEFF", "#2FFF2F", "#FFE000")
            var startMinutes = 8 * 60
            (30..60 step 5).forEachIndexed { index, duration ->
                val endMinutes = startMinutes + duration
                weekData.add(
                    lesson(
                        nextId++,
                        d,
                        "$duration min",
                        "$duration",
                        "Room ${index + 1}",
                        startMinutes / 60,
                        startMinutes % 60,
                        endMinutes / 60,
                        endMinutes % 60,
                        fills[index].toColorInt(),
                        Color.BLACK,
                    ),
                )
                startMinutes = endMinutes + 5
            }
        }

        // Friday: overlapping entries share the column width.
        fri?.let { d ->
            // Two 40-min lessons offset by 20 min -> both at half width.
            weekData.add(lesson(nextId++, d, "Overlap A", "Ovl A", "Room 1", 8, 0, 8, 40, "#1565C0".toColorInt()))
            weekData.add(lesson(nextId++, d, "Overlap B", "Ovl B", "Room 2", 8, 20, 9, 0, "#2E7D32".toColorInt()))
            // A 10-min duty on top of a 55-min lesson (e.g. a teacher supervising during their own class).
            weekData.add(lesson(nextId++, d, "Geography", "Geo", "Room 9", 9, 15, 10, 10, "#BF360C".toColorInt()))
            weekData.add(lesson(nextId++, d, "Hall Duty", "Duty", "", 9, 15, 9, 25, "#D50000".toColorInt()))
            // Three-way overlap of short entries -> third-width columns with corner labels.
            weekData.add(lesson(nextId++, d, "Tutoring", "Tut", "Room 4", 10, 30, 11, 10, "#6A1B9A".toColorInt()))
            weekData.add(lesson(nextId++, d, "Office Hours", "Office", "Room 5", 10, 40, 11, 20, "#E65100".toColorInt()))
            weekData.add(lesson(nextId++, d, "Meeting", "Meet", "", 10, 50, 11, 30, "#78909C".toColorInt()))
        }

        return weekData
    }
}
