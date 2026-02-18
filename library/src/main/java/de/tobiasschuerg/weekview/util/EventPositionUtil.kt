package de.tobiasschuerg.weekview.util

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.tobiasschuerg.weekview.data.Event
import java.time.LocalTime

/**
 * Utility for vertical positioning of events in the week view.
 * Calculates offset and height of an event based on the visible column start, duration, and scaling factor.
 *
 * Note: startTime must always be the configured column start (e.g. 8:00), not the first event start.
 * If you see events at the top that should not be there, check the startTime passed to this function.
 */
object EventPositionUtil {
    /**
     * Calculates the vertical offset and height of an event.
     *
     * @param event The Event.Single object
     * @param startTime The start time of the visible column
     * @param scalingFactor The scaling factor for height
     * @return Pair(topOffset, eventHeight)
     */
    fun calculateVerticalOffsets(
        event: Event.Single,
        startTime: LocalTime,
        scalingFactor: Float,
    ): Pair<Dp, Dp> {
        // Minutes since the start of the visible column
        val startMinutes =
            (event.timeSpan.start.hour - startTime.hour) * 60 +
                (event.timeSpan.start.minute - startTime.minute)
        // Duration of the event in minutes
        val durationMinutes = event.timeSpan.duration.toMinutes().toInt()

        // Clamp negative offsets to zero so events before the visible start are not shown above the grid
        val clampedStartMinutes = startMinutes.coerceAtLeast(0)
        // Reduce duration by the amount clamped so the event's bottom edge stays correct
        val adjustedDurationMinutes = (durationMinutes - (clampedStartMinutes - startMinutes)).coerceAtLeast(0)

        // Convert to dp
        val topOffset = (clampedStartMinutes * scalingFactor).dp
        val eventHeight = (adjustedDurationMinutes * scalingFactor).dp

        return Pair(topOffset, eventHeight)
    }
}
