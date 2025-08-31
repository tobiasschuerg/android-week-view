package de.tobiasschuerg.weekview.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    days: List<DayOfWeek> = listOf(
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
    val todayHighlightColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    val nowIndicatorColor = MaterialTheme.colorScheme.error
    val columnCount = days.size
    val today = LocalDate.now().dayOfWeek
    val timeLabels = (startTime.hour..endTime.hour).map { LocalTime.of(it, 0) }
    val leftOffsetDp = 48.dp
    val topOffsetDp = 32.dp
    val density = LocalDensity.current
    val scrollState = rememberScrollState()
    val hourCount = endTime.hour - startTime.hour
    val gridHeightDp: Dp = (hourCount * 60 * weekViewConfig.scalingFactor).dp

    Column(modifier = modifier.fillMaxSize()) {
        // Day labels (fixed at top)
        Row {
            Box(modifier = Modifier.size(leftOffsetDp, topOffsetDp)) // Empty top-left corner
            for (day in days) {
                Box(modifier = Modifier.size(80.dp, topOffsetDp)) {
                    val shortName = day.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault())
                    Text(
                        text = shortName,
                        style = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = Color.Gray),
                        modifier = Modifier
                    )
                }
            }
        }
        // Scrollable area: time labels (left) and grid (right)
        Row(modifier = Modifier.weight(1f)) {
            // Time labels (scrollable vertically)
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .width(leftOffsetDp)
                    .height(gridHeightDp)
            ) {
                for (i in timeLabels.indices) {
                    Box(modifier = Modifier.size(leftOffsetDp, 60.dp * weekViewConfig.scalingFactor)) {
                        Text(
                            text = timeLabels[i].toString(),
                            style = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, color = Color.Gray),
                            modifier = Modifier
                        )
                    }
                }
            }
            // Grid (scrollable vertically)
            Box(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .size((days.size * 80).dp, gridHeightDp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val columnWidth = size.width / columnCount
                    val hourHeightPx = size.height / hourCount
                    val scaledHourHeight = hourHeightPx * weekViewConfig.scalingFactor
                    // Draw vertical lines (day columns)
                    for (i in 0..columnCount) {
                        val x = i * columnWidth
                        drawLine(
                            color = Color.LightGray,
                            start = Offset(x, 0f),
                            end = Offset(x, size.height),
                            strokeWidth = 2f,
                        )
                    }
                    // Draw horizontal lines (hour rows)
                    for (i in 0..hourCount) {
                        val y = i * scaledHourHeight
                        drawLine(
                            color = Color.LightGray,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 2f,
                        )
                    }
                    // Highlight today's column
                    val todayIndex = days.indexOf(today)
                    if (todayIndex >= 0) {
                        val left = todayIndex * columnWidth
                        drawRect(
                            color = todayHighlightColor,
                            topLeft = Offset(left, 0f),
                            size = androidx.compose.ui.geometry.Size(columnWidth, size.height),
                        )
                    }
                    // Draw now indicator only if today is visible
                    if (showNowIndicator && todayIndex >= 0) {
                        val now = LocalTime.now()
                        if (now.isAfter(startTime) && now.isBefore(endTime)) {
                            val y = ((now.hour + now.minute / 60f) - startTime.hour) * scaledHourHeight
                            drawLine(
                                color = nowIndicatorColor,
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 4f,
                            )
                        }
                    }
                }
            }
        }
    }
}
