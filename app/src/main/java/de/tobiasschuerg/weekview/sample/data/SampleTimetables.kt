package de.tobiasschuerg.weekview.sample.data

import android.graphics.Color
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.LocalDateRange
import de.tobiasschuerg.weekview.data.WeekData
import de.tobiasschuerg.weekview.util.TimeSpan
import java.time.LocalDate
import java.time.LocalTime

/**
 * Entry point for the demo app's selectable sample timetables. Each timetable's data
 * lives in its own `SampleTimetable*` object; this just dispatches to them.
 */
object SampleTimetables {
    enum class Timetable(
        val label: String,
        val days: Int = 5,
    ) {
        UNIVERSITY("University"),
        WORK("Work"),
        SCHOOL("School"),
        CONFERENCE("Conference", days = 3),
        SPECIAL_CASES("Special Cases"),
    }

    fun create(
        timetable: Timetable,
        dateRange: LocalDateRange,
    ): WeekData {
        return when (timetable) {
            Timetable.UNIVERSITY -> SampleTimetableUniversity.create(dateRange)
            Timetable.WORK -> SampleTimetableWork.create(dateRange)
            Timetable.SCHOOL -> SampleTimetableSchool.create(dateRange)
            Timetable.CONFERENCE -> SampleTimetableConference.create(dateRange)
            Timetable.SPECIAL_CASES -> SampleTimetableSpecialCases.create(dateRange)
        }
    }
}

/** Shared helper for the compact `lesson(...)` call style used by [SampleTimetableSchool] and [SampleTimetableSpecialCases]. */
internal fun lesson(
    id: Long,
    date: LocalDate,
    title: String,
    shortTitle: String,
    room: String,
    startHour: Int,
    startMin: Int,
    endHour: Int,
    endMin: Int,
    color: Int,
): Event.Single =
    Event.Single(
        id = id,
        date = date,
        title = title,
        shortTitle = shortTitle,
        subTitle = room,
        timeSpan = TimeSpan(LocalTime.of(startHour, startMin), LocalTime.of(endHour, endMin)),
        textColor = Color.WHITE,
        backgroundColor = color,
    )
