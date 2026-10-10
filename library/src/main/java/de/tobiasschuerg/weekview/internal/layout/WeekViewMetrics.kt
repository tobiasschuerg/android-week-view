package de.tobiasschuerg.weekview.internal.layout

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.tobiasschuerg.weekview.model.TimeSpan
import java.time.LocalDate
import java.time.LocalTime

@Immutable
internal data class WeekViewMetrics(
    val days: List<LocalDate>,
    val columnCount: Int,
    val leftOffsetDp: Dp,
    val topOffsetDp: Dp,
    val effectiveStartTime: LocalTime,
    val effectiveEndTime: LocalTime,
    val gridStartTime: LocalTime,
    val rowHeightDp: Dp,
    val totalHours: Float,
    val gridHeightDp: Dp,
    val timeLabels: List<LocalTime>,
    val visibleTimeSpan: TimeSpan,
) {
    companion object {
        /** Minimum width of the time axis column on the left; wide labels grow it, see `TimeAxisDefaults`. */
        val LEFT_OFFSET: Dp = 48.dp

        /** Minimum height of the day header row on top. */
        val TOP_OFFSET: Dp = 36.dp
    }
}
