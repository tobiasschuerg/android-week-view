package de.tobiasschuerg.weekview.compose.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
    showNowIndicator: Boolean,
    highlightCurrentDay: Boolean,
    currentTimeLineOnlyToday: Boolean,
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
        if (highlightCurrentDay && days.contains(LocalDate.now())) {
            val todayColumnIndex = days.indexOf(LocalDate.now())
            val left = todayColumnIndex * columnWidthPx
            drawRect(
                color = style.colors.todayHighlight,
                topLeft = Offset(left, 0f),
                size = Size(columnWidthPx, size.height),
            )
        }

        // Now indicator line
        if (showNowIndicator && now.isAfter(gridStartTime) && now.isBefore(effectiveEndTime)) {
            val nowPositionMinutes = ChronoUnit.MINUTES.between(gridStartTime, now)
            val nowY = (nowPositionMinutes / 60f) * rowHeightPx
            if (nowY >= 0 && nowY <= size.height) {
                val dotRadius = 8f
                if (currentTimeLineOnlyToday && days.contains(LocalDate.now())) {
                    val todayColumnIndex = days.indexOf(LocalDate.now())
                    val left = todayColumnIndex * columnWidthPx
                    val right = left + columnWidthPx
                    // Linie nur in der heutigen Spalte
                    drawLine(
                        color = style.colors.nowIndicator,
                        start = Offset(left, nowY),
                        end = Offset(right, nowY),
                        strokeWidth = 4f,
                    )
                    // Punkt am Anfang der Linie
                    drawCircle(
                        color = style.colors.nowIndicator,
                        radius = dotRadius,
                        center = Offset(left, nowY),
                    )
                } else {
                    // Linie über alle Spalten
                    drawLine(
                        color = style.colors.nowIndicator,
                        start = Offset(0f, nowY),
                        end = Offset(size.width, nowY),
                        strokeWidth = 4f,
                    )
                    // Punkt am Anfang der Linie (erste Spalte)
                    drawCircle(
                        color = style.colors.nowIndicator,
                        radius = dotRadius,
                        center = Offset(0f, nowY),
                    )
                }
            }
        }
    }
}
