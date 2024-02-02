package de.tobiasschuerg.weekview.util

import java.time.Duration
import java.time.LocalTime

/**
 * Holds a duration of time.
 */
data class TimeSpan(
    val start: LocalTime,
    val endExclusive: LocalTime
) {

    init {
        require(start.isBefore(endExclusive)) {
            "Start time $start must be before end time $endExclusive!"
        }
    }

    val duration: Duration by lazy { Duration.between(start, endExclusive) }

    companion object {
        fun of(start: LocalTime, duration: Duration): TimeSpan {
            return TimeSpan(start, start.plus(duration))
        }
    }
}
