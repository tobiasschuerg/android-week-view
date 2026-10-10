package de.tobiasschuerg.weekview.model

import androidx.compose.ui.graphics.Color
import java.time.Duration
import java.time.LocalDate

public sealed class Event {
    public abstract val id: String
    public abstract val date: LocalDate
    public abstract val title: String
    public abstract val shortTitle: String

    public data class Single(
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

    public data class AllDay(
        override val id: String,
        override val date: LocalDate,
        override val title: String,
        override val shortTitle: String,
        val textColor: Color,
        val backgroundColor: Color,
    ) : Event()

    public data class MultiDay(
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
