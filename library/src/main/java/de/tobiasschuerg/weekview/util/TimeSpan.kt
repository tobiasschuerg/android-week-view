package de.tobiasschuerg.weekview.util

import java.time.Duration
import java.time.LocalTime

/**
 * Holds a duration of time.
 */
data class TimeSpan(
    val start: LocalTime,
    val endExclusive: LocalTime,
) {
    init {
        require(start.isBefore(endExclusive)) {
            "Start time $start must be before end time $endExclusive!"
        }
    }

    val duration: Duration by lazy { Duration.between(start, endExclusive) }

    /**
     * Returns a sequence of hourly time labels within this time span.
     * Each time is normalized to the hour boundary (minute = 0).
     *
     * Example: TimeSpan from 08:30 to 12:15 would return [08:00, 09:00, 10:00, 11:00, 12:00]
     */
    fun hourlyTimes(): Sequence<LocalTime> =
        sequence {
            var currentHour = start.hour
            val endHour = endExclusive.hour

            // Always yield the starting hour
            yield(LocalTime.of(currentHour, 0))

            // Generate subsequent hours until we reach the end
            while (currentHour < endHour) {
                currentHour++
                yield(LocalTime.of(currentHour, 0))
            }
        }

    companion object {
        fun of(
            start: LocalTime,
            duration: Duration,
        ): TimeSpan {
            return TimeSpan(start, start.plus(duration))
        }
    }
}
