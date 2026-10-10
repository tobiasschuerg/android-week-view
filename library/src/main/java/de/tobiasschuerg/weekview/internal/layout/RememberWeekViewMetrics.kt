package de.tobiasschuerg.weekview.internal.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.tobiasschuerg.weekview.model.Event
import de.tobiasschuerg.weekview.model.LocalDateRange
import de.tobiasschuerg.weekview.model.TimeSpan
import java.time.LocalTime
import java.time.temporal.ChronoUnit

/**
 * @param leftOffsetDp width of the time axis column, measured from the labels it has to fit.
 * @param minGridHeightDp the height the grid should at least fill (typically the viewport height);
 *   the visible time span is extended in whole hours until it does, see [extendToFillHeight].
 */
@Composable
internal fun rememberWeekViewMetrics(
    dateRange: LocalDateRange,
    timeRange: TimeSpan,
    events: List<Event.Single>,
    scalingFactor: Float,
    leftOffsetDp: Dp = WeekViewMetrics.LEFT_OFFSET,
    minGridHeightDp: Dp = 0.dp,
): WeekViewMetrics {
    // Derive stable keys from the event list so remember uses value equality (LocalTime)
    // instead of list reference equality which changes every recomposition
    val earliestEventStart = events.minOfOrNull { it.timeSpan.start } ?: timeRange.start
    val latestEventEnd = events.maxOfOrNull { it.timeSpan.endExclusive } ?: timeRange.endExclusive

    return remember(
        dateRange,
        timeRange,
        earliestEventStart,
        latestEventEnd,
        scalingFactor,
        leftOffsetDp,
        minGridHeightDp,
    ) {
        val days = dateRange.toList()
        val columnCount = days.size
        val topOffsetDp = WeekViewMetrics.TOP_OFFSET

        val effectiveStartTime = if (earliestEventStart.isBefore(timeRange.start)) earliestEventStart else timeRange.start

        val rowHeightDp = 60.dp * scalingFactor

        val gridEndTime =
            if (latestEventEnd.hour < 23) {
                LocalTime.of(latestEventEnd.hour + 1, 0)
            } else {
                LocalTime.MAX
            }
        val eventEndTime = if (gridEndTime.isAfter(timeRange.endExclusive)) gridEndTime else timeRange.endExclusive

        val visibleTimeSpan =
            TimeSpan(effectiveStartTime.truncatedTo(ChronoUnit.HOURS), eventEndTime)
                .extendToFillHeight(rowHeightDp, minGridHeightDp)
        val gridStartTime = visibleTimeSpan.start
        val effectiveEndTime = visibleTimeSpan.endExclusive

        val totalHours = totalHours(visibleTimeSpan)
        val gridHeightDp = rowHeightDp * totalHours
        val timeLabels = visibleTimeSpan.hourlyTimes().toList()

        WeekViewMetrics(
            days = days,
            columnCount = columnCount,
            leftOffsetDp = leftOffsetDp,
            topOffsetDp = topOffsetDp,
            effectiveStartTime = effectiveStartTime,
            effectiveEndTime = effectiveEndTime,
            gridStartTime = gridStartTime,
            rowHeightDp = rowHeightDp,
            totalHours = totalHours,
            gridHeightDp = gridHeightDp,
            timeLabels = timeLabels,
            visibleTimeSpan = visibleTimeSpan,
        )
    }
}
