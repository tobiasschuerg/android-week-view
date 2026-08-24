package de.tobiasschuerg.weekview.data

import de.tobiasschuerg.weekview.util.TimeSpan
import java.time.Duration
import java.time.LocalDate

sealed class Event {
    abstract val id: String
    abstract val date: LocalDate
    abstract val title: String
    abstract val shortTitle: String

    data class Single(
        override val id: String,
        override val date: LocalDate,
        override val title: String,
        override val shortTitle: String,
        val subTitle: String? = null,
        val timeSpan: TimeSpan,
        val upperText: String? = null,
        val lowerText: String? = null,
        val textColor: Int,
        val backgroundColor: Int,
    ) : Event() {
        val duration: Duration = timeSpan.duration
    }

    data class AllDay(
        override val id: String,
        override val date: LocalDate,
        override val title: String,
        override val shortTitle: String,
        val textColor: Int,
        val backgroundColor: Int,
    ) : Event()

    data class MultiDay(
        override val id: String,
        override val date: LocalDate,
        override val title: String,
        override val shortTitle: String,
        val lastDate: LocalDate,
        val textColor: Int,
        val backgroundColor: Int,
    ) : Event() {
        init {
            require(date <= lastDate) { "date ($date) must be <= lastDate ($lastDate)" }
        }
    }
}
