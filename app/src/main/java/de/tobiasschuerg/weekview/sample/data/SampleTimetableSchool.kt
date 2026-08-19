package de.tobiasschuerg.weekview.sample.data

import android.graphics.Color
import androidx.core.graphics.toColorInt
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.LocalDateRange
import de.tobiasschuerg.weekview.data.WeekData
import java.time.LocalTime

/** School sample: a compact secondary-school day plan across five weekdays. */
object SampleTimetableSchool {
    fun create(dateRange: LocalDateRange): WeekData {
        val days = dateRange.toList()
        val weekData = WeekData(dateRange, LocalTime.of(7, 30), LocalTime.of(15, 30))

        val mon = days[0]
        val tue = days.getOrNull(1)
        val wed = days.getOrNull(2)
        val thu = days.getOrNull(3)
        val fri = days.getOrNull(4)

        var nextId = 200L

        val mathColor = "#1565C0".toColorInt()
        val englishColor = "#2E7D32".toColorInt()
        val historyColor = "#BF360C".toColorInt()
        val scienceColor = "#6A1B9A".toColorInt()
        val artColor = "#E65100".toColorInt()
        val peColor = "#00838F".toColorInt()
        val musicColor = "#AD1457".toColorInt()
        val germanColor = "#283593".toColorInt()

        // Monday: 4 lessons + lunch
        weekData.add(lesson(nextId++, mon, "Mathematics", "Math", "Room 12", 8, 0, 8, 45, mathColor))
        weekData.add(lesson(nextId++, mon, "English", "Eng", "Room 7", 8, 50, 9, 35, englishColor))
        weekData.add(lesson(nextId++, mon, "History", "Hist", "Room 21", 9, 50, 10, 35, historyColor))
        weekData.add(lesson(nextId++, mon, "Science", "Sci", "Lab 2", 10, 40, 11, 25, scienceColor))
        weekData.add(lesson(nextId++, mon, "Art", "Art", "Art Room", 12, 0, 12, 45, artColor))
        weekData.add(lesson(nextId++, mon, "PE", "PE", "Gym", 12, 50, 13, 35, peColor))

        // Tuesday
        tue?.let { d ->
            weekData.add(lesson(nextId++, d, "German", "Ger", "Room 5", 8, 0, 8, 45, germanColor))
            weekData.add(lesson(nextId++, d, "Mathematics", "Math", "Room 12", 8, 50, 9, 35, mathColor))
            weekData.add(lesson(nextId++, d, "Science", "Sci", "Lab 2", 9, 50, 10, 35, scienceColor))
            weekData.add(lesson(nextId++, d, "Music", "Mus", "Music Room", 10, 40, 11, 25, musicColor))
            weekData.add(lesson(nextId++, d, "English", "Eng", "Room 7", 12, 0, 12, 45, englishColor))
        }

        // Wednesday
        wed?.let { d ->
            weekData.add(lesson(nextId++, d, "English", "Eng", "Room 7", 8, 0, 8, 45, englishColor))
            weekData.add(lesson(nextId++, d, "History", "Hist", "Room 21", 8, 50, 9, 35, historyColor))
            weekData.add(lesson(nextId++, d, "Mathematics", "Math", "Room 12", 9, 50, 10, 35, mathColor))
            weekData.add(lesson(nextId++, d, "German", "Ger", "Room 5", 10, 40, 11, 25, germanColor))
            weekData.add(lesson(nextId++, d, "PE", "PE", "Gym", 12, 0, 13, 30, peColor))
        }

        // Thursday
        thu?.let { d ->
            weekData.add(lesson(nextId++, d, "Science", "Sci", "Lab 2", 8, 0, 8, 45, scienceColor))
            weekData.add(lesson(nextId++, d, "Mathematics", "Math", "Room 12", 8, 50, 9, 35, mathColor))
            weekData.add(lesson(nextId++, d, "Art", "Art", "Art Room", 9, 50, 10, 35, artColor))
            weekData.add(lesson(nextId++, d, "German", "Ger", "Room 5", 10, 40, 11, 25, germanColor))
            weekData.add(lesson(nextId++, d, "History", "Hist", "Room 21", 12, 0, 12, 45, historyColor))
            weekData.add(lesson(nextId++, d, "Music", "Mus", "Music Room", 12, 50, 13, 35, musicColor))
        }

        // Friday (short day)
        fri?.let { d ->
            weekData.add(lesson(nextId++, d, "German", "Ger", "Room 5", 8, 0, 8, 45, germanColor))
            weekData.add(lesson(nextId++, d, "English", "Eng", "Room 7", 8, 50, 9, 35, englishColor))
            weekData.add(lesson(nextId++, d, "Mathematics", "Math", "Room 12", 9, 50, 10, 35, mathColor))
            weekData.add(lesson(nextId++, d, "Science", "Sci", "Lab 2", 10, 40, 11, 25, scienceColor))
        }

        // All-day: school trip
        thu?.let { d ->
            weekData.add(
                Event.AllDay(
                    id = nextId,
                    date = d,
                    title = "Parent-Teacher Day",
                    shortTitle = "PT Day",
                    textColor = Color.WHITE,
                    backgroundColor = "#FF6F00".toColorInt(),
                ),
            )
        }

        return weekData
    }
}
