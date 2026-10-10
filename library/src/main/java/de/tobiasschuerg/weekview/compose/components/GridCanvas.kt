package de.tobiasschuerg.weekview.compose.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.tobiasschuerg.weekview.compose.style.WeekViewStyle
import de.tobiasschuerg.weekview.compose.style.defaultWeekViewStyle
import java.time.LocalDate

@Composable
internal fun GridCanvas(
    modifier: Modifier = Modifier,
    columnCount: Int,
    rowHeightDp: Dp,
    totalHours: Float,
    days: List<LocalDate>,
    today: LocalDate,
    highlightCurrentDay: Boolean,
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
                strokeWidth = GRID_LINE_WIDTH.toPx(),
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
                strokeWidth = GRID_LINE_WIDTH.toPx(),
            )
        }

        // Today highlight
        if (highlightCurrentDay && days.contains(today)) {
            val todayColumnIndex = days.indexOf(today)
            val left = todayColumnIndex * columnWidthPx
            drawRect(
                color = style.colors.todayHighlight,
                topLeft = Offset(left, 0f),
                size = Size(columnWidthPx, size.height),
            )
        }
    }
}

/** Grid line thickness; in dp so it looks the same on every screen density. */
internal val GRID_LINE_WIDTH = 0.75.dp
