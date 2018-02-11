package de.tobiasschuerg.weekview.data

import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

sealed class Event {

    abstract val id: Long
    abstract val date: LocalDate
    abstract val fullName: String
    abstract val shortName: String

    data class Single(
            override val id: Long,
            override val date: LocalDate,
            override val fullName: String,
            override val shortName: String,

            val day: Int,
            val startTime: LocalTime,
            val endTime: LocalTime,

            val type: String?,
            val teacher: String?,
            val location: String?,

            val textColor: Int,
            val backgroundColor: Int
    ) : Event() {
        val duration: Duration = Duration.between(startTime, endTime)
    }

    data class AllDay(
            override val id: Long,
            override val date: LocalDate,
            override val fullName: String,
            override val shortName: String
    ) : Event()

    data class MultiDay(
            override val id: Long,
            override val date: LocalDate,
            override val fullName: String,
            override val shortName: String,

            val lastDate: LocalDate
    ) : Event()

}