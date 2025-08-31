package de.tobiasschuerg.weekview.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import de.tobiasschuerg.weekview.data.WeekViewConfig
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

/**
 * Composable that renders the background grid for the week view.
 * This includes the day columns, hour rows, day labels, time labels,
 * today highlight, and optional now indicator.
 */
@Composable
fun WeekBackgroundCompose(
    weekViewConfig: WeekViewConfig,
    modifier: Modifier = Modifier,
    days: List<DayOfWeek> =
        listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
        ),
    startTime: LocalTime = LocalTime.of(8, 0),
    endTime: LocalTime = LocalTime.of(18, 0),
    showNowIndicator: Boolean = true,
) {
    // Extract colors outside Canvas context to avoid Composable invocation errors
    val todayHighlightColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    val nowIndicatorColor = MaterialTheme.colorScheme.error

    val columnCount = days.size
    val today = LocalDate.now().dayOfWeek
    val timeLabels = (startTime.hour..endTime.hour).map { LocalTime.of(it, 0) }

    // Layout constants matching classic implementation
    val leftOffsetDp = 48.dp // Space for time labels
    val topOffsetDp = 32.dp // Space for day labels

    val density = LocalDensity.current
    var boxSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    boxSize = coordinates.size
                },
    ) {
        // Draw the background grid using Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val leftOffsetPx = with(density) { leftOffsetDp.toPx() }
            val topOffsetPx = with(density) { topOffsetDp.toPx() }

            // Content area (excluding margins for labels)
            val contentWidth = width - leftOffsetPx
            val contentHeight = height - topOffsetPx
            val columnWidth = contentWidth / columnCount
            val hourCount = endTime.hour - startTime.hour
            val hourHeightPx = contentHeight / hourCount

            // Apply scaling factor from config
            val scaledHourHeight = hourHeightPx * weekViewConfig.scalingFactor

            // Draw vertical lines (day columns) - start from left offset
            for (i in 0..columnCount) {
                val x = leftOffsetPx + (i * columnWidth)
                drawLine(
                    color = Color.LightGray,
                    start = Offset(x, topOffsetPx),
                    end = Offset(x, height),
                    strokeWidth = 2f,
                )
            }

            // Draw horizontal lines (hour rows) - start from top offset
            for (i in 0..hourCount) {
                val y = topOffsetPx + (i * scaledHourHeight)
                drawLine(
                    color = Color.LightGray,
                    start = Offset(leftOffsetPx, y),
                    end = Offset(width, y),
                    strokeWidth = 2f,
                )
            }

            // Highlight today's column
            val todayIndex = days.indexOf(today)
            if (todayIndex >= 0) {
                val left = leftOffsetPx + (todayIndex * columnWidth)
                drawRect(
                    color = todayHighlightColor,
                    topLeft = Offset(left, topOffsetPx),
                    size = androidx.compose.ui.geometry.Size(columnWidth, contentHeight),
                )
            }

            // Draw now indicator if enabled and today is visible
            if (showNowIndicator && todayIndex >= 0) {
                val now = LocalTime.now()
                if (now.isAfter(startTime) && now.isBefore(endTime)) {
                    val y = topOffsetPx + ((now.hour + now.minute / 60f) - startTime.hour) * scaledHourHeight
                    drawLine(
                        color = nowIndicatorColor,
                        start = Offset(leftOffsetPx, y),
                        end = Offset(width, y),
                        strokeWidth = 4f,
                    )
                }
            }
        }

        // Render labels only when box size is known
        if (boxSize.width > 0 && boxSize.height > 0) {
            WeekLabels(
                days = days,
                timeLabels = timeLabels,
                boxSize = boxSize,
                leftOffsetDp = leftOffsetDp,
                topOffsetDp = topOffsetDp,
                density = density,
            )
        }
    }
}
