package de.tobiasschuerg.weekview.compose

import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.TextStyle.SHORT
import java.util.Locale

/**
 * Composable that renders the day and time labels for the week view grid.
 * Day labels are positioned in the top margin, time labels in the left margin.
 */
@Composable
fun WeekLabels(
    days: List<DayOfWeek>,
    timeLabels: List<LocalTime>,
    boxSize: IntSize,
    leftOffsetDp: Dp,
    topOffsetDp: Dp,
    density: Density,
    modifier: Modifier = Modifier
) {
    val columnCount = days.size
    val hourCount = timeLabels.size

    // Calculate content area dimensions
    val leftOffsetPx = with(density) { leftOffsetDp.toPx() }
    val topOffsetPx = with(density) { topOffsetDp.toPx() }
    val contentWidth = boxSize.width - leftOffsetPx
    val contentHeight = boxSize.height - topOffsetPx

    // Render day labels (positioned in top margin, centered above each column)
    for ((i, day) in days.withIndex()) {
        val shortName = day.getDisplayName(SHORT, Locale.getDefault())
        val columnWidth = contentWidth / columnCount
        val xPx = leftOffsetPx + (i * columnWidth) + (columnWidth / 2)
        val xDp = with(density) { xPx.toDp() }
        val yDp = topOffsetDp / 2 // Center vertically in top margin

        Text(
            text = shortName,
            style = TextStyle(fontSize = 14.sp, color = Color.Gray),
            modifier = modifier.absoluteOffset(x = xDp, y = yDp)
        )
    }

    // Render time labels (positioned in left margin, centered next to each row)
    for ((i, time) in timeLabels.withIndex()) {
        val hourHeight = contentHeight / hourCount
        val yPx = topOffsetPx + (i * hourHeight) + (hourHeight / 2)
        val yDp = with(density) { yPx.toDp() }
        val xDp = 4.dp // Small padding from left edge

        Text(
            text = time.toString(),
            style = TextStyle(fontSize = 12.sp, color = Color.Gray),
            modifier = modifier.absoluteOffset(x = xDp, y = yDp)
        )
    }
}
