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

    /** Minimum entry height for a title to be allowed to wrap onto a second line instead of eliding. */
    private val MIN_HEIGHT_FOR_TWO_LINE_TITLE = 72.dp

    /**
     * Whether an event entry of the given height has enough room to let its title
     * wrap onto a second line instead of being ellipsized to one.
     */
    fun allowsTwoLineTitle(eventHeight: Dp): Boolean = eventHeight >= MIN_HEIGHT_FOR_TWO_LINE_TITLE

    /**
     * Minimum entry height for the start and end time to be split into their own lines
     * (start at the top, end pinned to the bottom) instead of one combined line at the top.
     */
    private val MIN_HEIGHT_FOR_SPLIT_TIME_LABELS = 90.dp

    /**
     * Whether an event entry of the given height has enough room to show the start
     * and end time as two separate labels instead of one combined "start - end" line.
     */
    fun allowsSplitTimeLabels(eventHeight: Dp): Boolean = eventHeight >= MIN_HEIGHT_FOR_SPLIT_TIME_LABELS

    // Below the minimum height, fields are dropped by priority so the most useful
    // information survives on very short entries instead of whatever happens to be
    // first in the stack: name (always shown) > time > location > teacher.
    private val MIN_HEIGHT_FOR_TIME_FIELD = 24.dp
    private val MIN_HEIGHT_FOR_LOCATION_FIELD = 40.dp
    private val MIN_HEIGHT_FOR_TEACHER_FIELD = 56.dp

    /** Whether there's enough room to show the time field at all (priority 2, after the name). */
    fun allowsTimeField(eventHeight: Dp): Boolean = eventHeight >= MIN_HEIGHT_FOR_TIME_FIELD

    /** Whether there's enough room to show the location field (priority 3). */
    fun allowsLocationField(eventHeight: Dp): Boolean = eventHeight >= MIN_HEIGHT_FOR_LOCATION_FIELD

    /** Whether there's enough room to show the teacher/lower-text fields (priority 4, lowest). */
    fun allowsTeacherField(eventHeight: Dp): Boolean = eventHeight >= MIN_HEIGHT_FOR_TEACHER_FIELD
}
