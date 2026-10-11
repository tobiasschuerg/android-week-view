package de.tobiasschuerg.weekview.sample.data

import de.tobiasschuerg.weekview.model.Period
import de.tobiasschuerg.weekview.model.TimeSpan
import java.time.LocalTime

/** A school day's periods, matching the lesson times of [SampleTimetableSchool]. */
object SamplePeriods {
    val school: List<Period> =
        listOf(
            lesson("1", 8, 0, 8, 45),
            lesson("2", 8, 50, 9, 35),
            Period("Break", span(9, 35, 9, 50), isBreak = true),
            lesson("3", 9, 50, 10, 35),
            lesson("4", 10, 40, 11, 25),
            Period("Lunch", span(11, 25, 12, 0), isBreak = true),
            lesson("5", 12, 0, 12, 45),
            lesson("6", 12, 50, 13, 35),
            lesson("7", 13, 40, 14, 25),
        )

    private fun lesson(
        label: String,
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int,
    ) = Period(label, span(startHour, startMinute, endHour, endMinute))

    private fun span(
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int,
    ) = TimeSpan(LocalTime.of(startHour, startMinute), LocalTime.of(endHour, endMinute))
}
