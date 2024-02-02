package de.tobiasschuerg.weekview.data

import de.tobiasschuerg.weekview.util.TimeSpan
import java.time.Duration
import java.time.LocalDate

sealed class Event {

    abstract val id: Long
    abstract val date: LocalDate
    abstract val title: String
    abstract val shortTitle: String

    data class Single(
        override val id: Long,
        override val date: LocalDate,
        override val title: String,
        override val shortTitle: String,
        val subTitle: String? = null,

        val timeSpan: TimeSpan,

        val upperText: String? = null,
        val lowerText: String? = null,

        val textColor: Int,
        val backgroundColor: Int
    ) : Event() {
        val duration: Duration = timeSpan.duration
    }

    data class AllDay(
        override val id: Long,
        override val date: LocalDate,
        override val title: String,
        override val shortTitle: String
    ) : Event()

    data class MultiDay(
        override val id: Long,
        override val date: LocalDate,
        override val title: String,
        override val shortTitle: String,

        val lastDate: LocalDate
    ) : Event()
}
