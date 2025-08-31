package de.tobiasschuerg.weekview.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.data.WeekViewConfig
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

/**
 * Composable that renders the background grid for the week view.
 * This includes the day columns, hour rows, day labels, time labels,
 * today highlight, optional now indicator, and events.
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
    events: List<Event.Single> = emptyList(),
    eventConfig: EventConfig = EventConfig(),
    onEventClick: ((eventId: Long) -> Unit)? = null,
    onEventLongPress: ((eventId: Long) -> Unit)? = null,
) {
    val todayHighlightColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    val nowIndicatorColor = MaterialTheme.colorScheme.error
    val columnCount = days.size
    val today = LocalDate.now().dayOfWeek
    val leftOffsetDp = 48.dp
    val topOffsetDp = 32.dp
    val rowHeightDp = 60.dp * weekViewConfig.scalingFactor

    // Calculate the latest event end and round up to the next full hour
    val latestEventEnd = events.maxOfOrNull { it.timeSpan.endExclusive } ?: endTime
    val roundedEventEnd =
        if (latestEventEnd.minute > 0 || latestEventEnd.second > 0) {
            latestEventEnd.plusHours(1).withMinute(0).withSecond(0)
        } else {
            latestEventEnd
        }
    // Use the maximum of configured endTime and rounded event end
    val effectiveEndTime = if (roundedEventEnd.isAfter(endTime)) roundedEventEnd else endTime
    val hourCount = effectiveEndTime.hour - startTime.hour
    val gridHeightDp = rowHeightDp * hourCount
    val timeLabels = (startTime.hour..effectiveEndTime.hour).map { LocalTime.of(it, 0) }
    val scrollState = androidx.compose.foundation.rememberScrollState()

    var now by remember { mutableStateOf(LocalTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = LocalTime.now()
            delay(1000) // update every second
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        // Calculate dynamic column width based on available space
        val availableWidth = maxWidth - leftOffsetDp
        val dynamicColumnWidthDp = (availableWidth / columnCount)
        val totalGridWidthDp = dynamicColumnWidthDp * columnCount

        Column(modifier = Modifier.fillMaxSize()) {
            // Day labels (fixed at top)
            Row {
                Box(modifier = Modifier.size(leftOffsetDp, topOffsetDp)) // Empty top-left corner
                for (day in days) {
                    Box(
                        modifier = Modifier.size(dynamicColumnWidthDp, topOffsetDp),
                        contentAlignment = Alignment.Center,
                    ) {
                        val shortName = day.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault())
                        Text(
                            text = shortName,
                            style = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = Color.Gray),
                            modifier = Modifier,
                        )
                    }
                }
            }
            // Scrollable area: time labels (left) and grid with events (right)
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
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(end = 4.dp),
                            )
                        }
                    }
                }
                // Grid with events (scrollable vertically)
                Box(
                    modifier =
                        Modifier
                            .verticalScroll(scrollState)
                            .width(totalGridWidthDp)
                            .height(gridHeightDp)
                            .fillMaxSize(),
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val columnWidth = size.width / columnCount
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

                        // Today highlight
                        if (days.contains(today)) {
                            val todayColumnIndex = days.indexOf(today)
                            val left = todayColumnIndex * columnWidth
                            left + columnWidth
                            drawRect(
                                color = todayHighlightColor,
                                topLeft = Offset(left, 0f),
                                size = androidx.compose.ui.geometry.Size(columnWidth, size.height),
                            )
                        }

                        // Now indicator line (full width)
                        if (showNowIndicator && now.isAfter(startTime) && now.isBefore(effectiveEndTime)) {
                            val nowPositionFloat = ((now.hour + now.minute / 60f) - startTime.hour)
                            val nowY = nowPositionFloat * rowHeightPx
                            drawLine(
                                color = nowIndicatorColor,
                                start = Offset(0f, nowY),
                                end = Offset(size.width, nowY),
                                strokeWidth = 4f,
                            )
                        }
                    }

                    // Render events with overlap handling for each day column
                    days.forEachIndexed { dayIndex, day ->
                        val eventsForDay = events.filter { it.date.dayOfWeek == day }

                        if (eventsForDay.isNotEmpty()) {
                            Box(
                                modifier =
                                    Modifier
                                        .offset(x = dayIndex * dynamicColumnWidthDp)
                                        .size(dynamicColumnWidthDp, gridHeightDp),
                            ) {
                                EventsWithOverlapHandling(
                                    events = eventsForDay,
                                    weekViewConfig = weekViewConfig,
                                    eventConfig = eventConfig,
                                    startTime = startTime,
                                    endTime = effectiveEndTime,
                                    columnWidth = dynamicColumnWidthDp,
                                    onEventClick = onEventClick,
                                    onEventLongPress = onEventLongPress,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
