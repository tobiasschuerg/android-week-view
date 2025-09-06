package de.tobiasschuerg.weekview.compose.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
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
    todayHighlightColor: Color,
    showNowIndicator: Boolean,
    now: LocalTime,
    gridStartTime: LocalTime,
    effectiveEndTime: LocalTime,
    nowIndicatorColor: Color,
) {
    Canvas(modifier = modifier) {
        val columnWidthPx = size.width / columnCount
        val rowHeightPx = rowHeightDp.toPx()

        // Vertical lines (day columns)
        for (i in 0..columnCount) {
            val x = i * columnWidthPx
            drawLine(
                color = Color.LightGray,
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
                color = Color.LightGray,
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
                color = todayHighlightColor,
                topLeft = Offset(left, 0f),
                size = androidx.compose.ui.geometry.Size(columnWidthPx, size.height),
            )
        }

        // Now indicator line (full width)
        if (showNowIndicator && now.isAfter(gridStartTime) && now.isBefore(effectiveEndTime)) {
            val nowPositionMinutes = ChronoUnit.MINUTES.between(gridStartTime, now)
            val nowY = (nowPositionMinutes / 60f) * rowHeightPx
            if (nowY >= 0 && nowY <= size.height) { // Ensure line is within canvas bounds
                drawLine(
                    color = nowIndicatorColor,
                    start = Offset(0f, nowY),
                    end = Offset(size.width, nowY),
                    strokeWidth = 4f,
                )
            }
        }
    }
}
