package de.tobiasschuerg.weekview.compose.state

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import de.tobiasschuerg.weekview.util.TimeSpan
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
)
