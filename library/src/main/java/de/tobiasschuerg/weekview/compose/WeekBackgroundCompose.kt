package de.tobiasschuerg.weekview.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.tobiasschuerg.weekview.data.WeekViewConfig
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

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
    val todayHighlightColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    val nowIndicatorColor = MaterialTheme.colorScheme.error
    val columnCount = days.size
    val today = LocalDate.now().dayOfWeek
    val leftOffsetDp = 48.dp
    val topOffsetDp = 32.dp
    val rowHeightDp = 60.dp * weekViewConfig.scalingFactor
    val hourCount = endTime.hour - startTime.hour
    val timeLabels = (startTime.hour..endTime.hour).map { LocalTime.of(it, 0) }
    val scrollState = androidx.compose.foundation.rememberScrollState()
    val gridHeightDp = rowHeightDp * hourCount

    var now by remember { mutableStateOf(LocalTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = LocalTime.now()
            delay(1000) // update every second
        }
    }

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
                        modifier = Modifier,
                    )
                }
            }
        }
        // Scrollable area: time labels (left) and grid (right)
        Row(modifier = Modifier.weight(1f)) {
            // Time labels (scrollable vertically)
            Box(
                modifier =
                    Modifier
                        .width(leftOffsetDp)
                        .height(gridHeightDp),
            ) {
                // Regular time labels (hours)
                Column(
                    modifier = Modifier.verticalScroll(scrollState),
                ) {
                    for (timeLabel in timeLabels) {
                        Box(modifier = Modifier.size(leftOffsetDp, rowHeightDp)) {
                            Text(
                                text = timeLabel.toString(),
                                style = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, color = Color.Gray),
                                modifier = Modifier,
                            )
                        }
                    }
                }

                // Current time indicator label (HH:mm)
                if (showNowIndicator && now.isAfter(startTime) && now.isBefore(endTime)) {
                    val nowPositionFloat = ((now.hour + now.minute / 60f) - startTime.hour)
                    val nowPositionDp = (nowPositionFloat * rowHeightDp.value).dp
                    Box(
                        modifier =
                            Modifier
                                .offset(y = nowPositionDp - scrollState.value.dp)
                                .width(leftOffsetDp),
                    ) {
                        Text(
                            text = String.format(Locale.getDefault(), "%02d:%02d", now.hour, now.minute),
                            style =
                                androidx.compose.ui.text.TextStyle(
                                    fontSize = 11.sp,
                                    color = nowIndicatorColor,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.End,
                                ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
            // Grid (scrollable vertically)
            Box(
                modifier =
                    Modifier
                        .verticalScroll(scrollState)
                        .width((days.size * 80).dp)
                        .height(gridHeightDp),
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val columnWidth = size.width / columnCount
                    // Calculate row height in pixels from Dp to match the time label boxes exactly
                    val rowHeightPx = with(this@Canvas) { rowHeightDp.toPx() }
                    // Vertical lines (day columns)
                    for (i in 0..columnCount) {
                        val x = i * columnWidth
                        drawLine(
                            color = Color.LightGray,
                            start = Offset(x, 0f),
                            end = Offset(x, size.height),
                            strokeWidth = 2f,
                        )
                    }
                    // Horizontal lines (hours) - full width
                    for (i in 0..hourCount) {
                        val y = i * rowHeightPx
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
                    // Now indicator - always draw when time is in range
                    if (showNowIndicator) {
                        if (now.isAfter(startTime) && now.isBefore(endTime)) {
                            val y = ((now.hour + now.minute / 60f) - startTime.hour) * rowHeightPx
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
