package de.tobiasschuerg.weekview.model

import androidx.compose.ui.graphics.Color
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
        val textColor: Color,
        val backgroundColor: Color,
    ) : Event() {
        val duration: Duration = timeSpan.duration
    }

    data class AllDay(
        override val id: String,
        override val date: LocalDate,
        override val title: String,
        override val shortTitle: String,
        val textColor: Color,
        val backgroundColor: Color,
    ) : Event()

    data class MultiDay(
        override val id: String,
        override val date: LocalDate,
        override val title: String,
        override val shortTitle: String,
        val lastDate: LocalDate,
        val textColor: Color,
        val backgroundColor: Color,
    ) : Event() {
        init {
            require(date <= lastDate) { "date ($date) must be <= lastDate ($lastDate)" }
        }
    }
}
