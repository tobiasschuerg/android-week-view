package de.tobiasschuerg.weekview.compose.state

import androidx.compose.ui.unit.Dp
import de.tobiasschuerg.weekview.util.TimeSpan
import java.time.LocalTime

/**
 * Extends [visibleTimeSpan] in whole hours until the grid it describes is at least
 * [minGridHeightDp] tall, so a short schedule still fills the viewport with grid
 * rows instead of leaving a blank area below the last event.
 *
 * Extra hours are appended after the end first (up to midnight) and only then
 * prepended before the start (down to 00:00). A span that already covers the
 * whole day cannot grow any further and is returned as is.
 */
internal fun TimeSpan.extendToFillHeight(
    rowHeightDp: Dp,
    minGridHeightDp: Dp,
): TimeSpan {
    var start = start
    var end = endExclusive
    if (rowHeightDp.value <= 0f || gridHeightDp(start, end, rowHeightDp) >= minGridHeightDp) return this

    end = end.ceilToHour()
    while (gridHeightDp(start, end, rowHeightDp) < minGridHeightDp && end != LocalTime.MAX) {
        end = if (end.hour == 23) LocalTime.MAX else end.plusHours(1)
    }
    while (gridHeightDp(start, end, rowHeightDp) < minGridHeightDp && start.hour > 0) {
        start = LocalTime.of(start.hour - 1, 0)
    }
    return TimeSpan(start, end)
}

/** Height of a grid spanning [start] to [end] in dp, using the same fractional-hour maths as the metrics. */
internal fun gridHeightDp(
    start: LocalTime,
    end: LocalTime,
    rowHeightDp: Dp,
): Dp = rowHeightDp * totalHours(TimeSpan(start, end))

internal fun totalHours(span: TimeSpan): Float = span.duration.toHours().toFloat() + (span.duration.toMinutes() % 60 / 60f)

/** Rounds up to the next full hour; anything past 23:00 becomes midnight, represented as [LocalTime.MAX]. */
private fun LocalTime.ceilToHour(): LocalTime =
    when {
        minute == 0 && second == 0 && nano == 0 -> this
        hour == 23 -> LocalTime.MAX
        else -> LocalTime.of(hour + 1, 0)
    }
