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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.data.LocalDateRange
import de.tobiasschuerg.weekview.util.TimeSpan
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

/**
 * Composable that renders the background grid for the week view.
 * This includes the day columns, hour rows, day labels, time labels,
 * today highlight, optional now indicator, and events.
 */
@Composable
fun WeekBackgroundCompose(
    modifier: Modifier = Modifier,
    scalingFactor: Float = 1f,
    dateRange: LocalDateRange,
    timeRange: TimeSpan,
    showNowIndicator: Boolean = true,
    events: List<Event.Single> = emptyList(),
    eventConfig: EventConfig = EventConfig(),
    onEventClick: ((eventId: Long) -> Unit)? = null,
    onEventLongPress: ((eventId: Long) -> Unit)? = null,
) {
    val todayHighlightColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    val nowIndicatorColor = MaterialTheme.colorScheme.error
    val days = dateRange.toList()
    val columnCount = days.size
    val today = LocalDate.now()
    val leftOffsetDp = 48.dp
    val topOffsetDp = 36.dp
    // Calculate the earliest event start to potentially extend the visible range
    val earliestEventStart = events.minOfOrNull { it.timeSpan.start } ?: timeRange.start

    // Determine the effective start time (earlier of timeRange.start and earliest event)
    val effectiveStartTime = if (earliestEventStart.isBefore(timeRange.start)) earliestEventStart else timeRange.start

    val rowHeightDp = 60.dp * scalingFactor

    // Calculate the latest event end.
    val latestEventEnd = events.maxOfOrNull { it.timeSpan.endExclusive } ?: timeRange.endExclusive

    // Determine the grid end time by rounding up to the next hour, clamping at the end of the day.
    val gridEndTime =
        if (latestEventEnd.hour < 23) {
            // Round up to the next full hour.
            LocalTime.of(latestEventEnd.hour + 1, 0)
        } else {
            // If the latest event is at 23:xx, clamp to the end of the day.
            LocalTime.MAX
        }

    // The effective end time is the later of the provided timeRange.endExclusive and the calculated grid end time.
    // This ensures the grid always extends to show all events.
    val effectiveEndTime = if (gridEndTime.isAfter(timeRange.endExclusive)) gridEndTime else timeRange.endExclusive
    // For time labels, we'll start from the truncated hour, but for event positioning we use the actual start
    val gridStartTime = effectiveStartTime.truncatedTo(ChronoUnit.HOURS)
    val visibleTimeSpan = TimeSpan(gridStartTime, effectiveEndTime)

    val totalHours =
        visibleTimeSpan.duration.toHours().toFloat() +
            (visibleTimeSpan.duration.toMinutesPart() / 60f)
    val gridHeightDp = rowHeightDp * totalHours

    // Generate time labels using the elegant TimeSpan API
    val timeLabels = visibleTimeSpan.hourlyTimes().toList()

    val scrollState = rememberScrollState()

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
                Box(modifier = Modifier.size(leftOffsetDp, topOffsetDp))
                for (date in days) {
                    Box(
                        modifier =
                            Modifier
                                .size(dynamicColumnWidthDp, topOffsetDp)
                                .padding(vertical = 2.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        val shortName = date.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault())
                        val shortDate =
                            date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)).replace(Regex("[^0-9]*[0-9]+$"), "")
                        Text(
                            text = shortName + "\n" + shortDate,
                            style =
                                TextStyle(
                                    fontSize = 13.sp,
                                    color = Color.LightGray,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                ),
                            modifier = Modifier.fillMaxWidth(),
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
                                    style = TextStyle(fontSize = 12.sp, color = Color.Gray),
                                    modifier = Modifier,
                                )
                            }
                        }
                    }

                    // Current time indicator label (HH:mm)
                    if (showNowIndicator && now.isAfter(timeRange.start) && now.isBefore(timeRange.endExclusive)) {
                        val nowPositionFloat = ((now.hour + now.minute / 60f) - timeRange.start.hour)
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
                                    TextStyle(
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
                            val left = todayColumnIndex * columnWidth
                            drawRect(
                                color = todayHighlightColor,
                                topLeft = Offset(left, 0f),
                                size = androidx.compose.ui.geometry.Size(columnWidth, size.height),
                            )
                        }

                        // Now indicator line (full width)
                        if (showNowIndicator && now.isAfter(timeRange.start) && now.isBefore(effectiveEndTime)) {
                            val nowPositionFloat = ((now.hour + now.minute / 60f) - timeRange.start.hour)
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
                    days.forEachIndexed { dayIndex, date ->
                        val eventsForDay = events.filter { it.date == date }
                        if (eventsForDay.isNotEmpty()) {
                            Box(
                                modifier =
                                    Modifier
                                        .offset(x = dayIndex * dynamicColumnWidthDp)
                                        .size(dynamicColumnWidthDp, gridHeightDp),
                            ) {
                                EventsWithOverlapHandling(
                                    events = eventsForDay,
                                    scalingFactor = scalingFactor,
                                    eventConfig = eventConfig,
                                    startTime = gridStartTime,
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
