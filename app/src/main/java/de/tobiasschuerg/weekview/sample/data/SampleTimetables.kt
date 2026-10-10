package de.tobiasschuerg.weekview.sample.data

import androidx.compose.ui.graphics.Color
import de.tobiasschuerg.weekview.model.Event
import de.tobiasschuerg.weekview.model.LocalDateRange
import de.tobiasschuerg.weekview.model.TimeSpan
import de.tobiasschuerg.weekview.model.WeekData
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
        SHORT_DAY("Short Day"),
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
            Timetable.SHORT_DAY -> SampleTimetableShortDay.create(dateRange)
        }
    }
}

/** Shared helper for the compact `lesson(...)` call style used by [SampleTimetableSchool], [SampleTimetableSpecialCases] and [SampleTimetableShortDay]. */
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
    color: Color,
    textColor: Color = Color.White,
): Event.Single =
    Event.Single(
        id = id.toString(),
        date = date,
        title = title,
        shortTitle = shortTitle,
        subTitle = room,
        timeSpan = TimeSpan(LocalTime.of(startHour, startMin), LocalTime.of(endHour, endMin)),
        textColor = textColor,
        backgroundColor = color,
    )
