package de.tobiasschuerg.timetableview.data

import org.threeten.bp.Duration
import org.threeten.bp.LocalTime

data class Lesson(
        val id: Long,

        val fullName: String,
        val shortName: String,

        val day: Int,
        val startTime: LocalTime,
        val endTime: LocalTime,

        val type: String?,
        val teacher: String?,
        val location: String?,

        val textColor: Int, // subject.color.getTextBlackWhite()
        val backgroundColor: Int // subject.color.get500().toColor()
) {
    val duration: Duration = Duration.between(startTime, endTime)
}