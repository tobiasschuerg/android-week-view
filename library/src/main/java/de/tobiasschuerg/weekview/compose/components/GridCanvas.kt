package de.tobiasschuerg.weekview.compose.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import de.tobiasschuerg.weekview.compose.style.WeekViewStyle
import de.tobiasschuerg.weekview.compose.style.defaultWeekViewStyle
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@Composable
internal fun GridCanvas(
    modifier: Modifier = Modifier,
    columnCount: Int,
    rowHeightDp: Dp,
    totalHours: Float,
    days: List<LocalDate>,
    today: LocalDate,
    showNowIndicator: Boolean,
    now: LocalTime,
    gridStartTime: LocalTime,
    effectiveEndTime: LocalTime,
    style: WeekViewStyle = defaultWeekViewStyle(),
) {
    Canvas(modifier = modifier) {
        val columnWidthPx = if (columnCount > 0) size.width / columnCount else size.width // Avoid division by zero
        val rowHeightPx = rowHeightDp.toPx()

        // Vertical lines (day columns)
        for (i in 0..columnCount) {
            val x = i * columnWidthPx
            drawLine(
                color = style.colors.gridLineColor,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 2f,
            )
        }

        // Horizontal lines (hours) - full width
        val hourLineCount = kotlin.math.ceil(totalHours).toInt()
        for (i in 0..hourLineCount) {
            val y = i * rowHeightPx
            drawLine(
                color = style.colors.gridLineColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 2f,
            )
        }

        // Today highlight
        if (days.contains(today)) {
            val todayColumnIndex = days.indexOf(today)
            val left = todayColumnIndex * columnWidthPx
            drawRect(
                color = style.colors.todayHighlight,
                topLeft = Offset(left, 0f),
                size = androidx.compose.ui.geometry.Size(columnWidthPx, size.height),
            )
        }

        // Now indicator line (full width)
        if (showNowIndicator && now.isAfter(gridStartTime) && now.isBefore(effectiveEndTime)) {
            val nowPositionMinutes = ChronoUnit.MINUTES.between(gridStartTime, now)
            val nowY = (nowPositionMinutes / 60f) * rowHeightPx
            // Ensure line is within canvas bounds
            if (nowY >= 0 && nowY <= size.height) {
                drawLine(
                    color = style.colors.nowIndicator,
                    start = Offset(0f, nowY),
                    end = Offset(size.width, nowY),
                    strokeWidth = 4f,
                )
            }
        }
    }
}
