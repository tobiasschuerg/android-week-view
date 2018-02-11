package de.tobiasschuerg.timetableview.data

import org.threeten.bp.LocalDate

sealed class TimetableItem {

    abstract val date: LocalDate

    data class Regular(
            override val date: LocalDate,
            val lesson: Lesson
    ) : TimetableItem()

    data class Holiday(
            override val date: LocalDate,
            val name: String,
            val firstDate: LocalDate,
            val lastDate: LocalDate
    ) : TimetableItem()

}