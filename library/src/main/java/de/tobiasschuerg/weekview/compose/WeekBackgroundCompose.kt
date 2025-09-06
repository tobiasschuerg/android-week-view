package de.tobiasschuerg.weekview.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
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
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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

@Immutable
internal data class WeekViewMetrics(
    val days: List<LocalDate>,
    val columnCount: Int,
    val today: LocalDate,
    val leftOffsetDp: Dp,
    val topOffsetDp: Dp,
    val effectiveStartTime: LocalTime,
    val effectiveEndTime: LocalTime,
    val gridStartTime: LocalTime,
    val rowHeightDp: Dp,
    val totalHours: Float,
    val gridHeightDp: Dp,
    val timeLabels: List<LocalTime>,
    val visibleTimeSpan: TimeSpan,
)

@Composable
internal fun rememberWeekViewMetrics(
    dateRange: LocalDateRange,
    timeRange: TimeSpan,
    events: List<Event.Single>,
    scalingFactor: Float,
): WeekViewMetrics {
    return remember(dateRange, timeRange, events, scalingFactor) {
        val days = dateRange.toList()
        val columnCount = days.size
        val today = LocalDate.now()
        val leftOffsetDp = 48.dp
        val topOffsetDp = 36.dp

        val earliestEventStart = events.minOfOrNull { it.timeSpan.start } ?: timeRange.start
        val effectiveStartTime = if (earliestEventStart.isBefore(timeRange.start)) earliestEventStart else timeRange.start

        val rowHeightDp = 60.dp * scalingFactor

        val latestEventEnd = events.maxOfOrNull { it.timeSpan.endExclusive } ?: timeRange.endExclusive
        val gridEndTime = if (latestEventEnd.hour < 23) {
            LocalTime.of(latestEventEnd.hour + 1, 0)
        } else {
            LocalTime.MAX
        }
        val effectiveEndTime = if (gridEndTime.isAfter(timeRange.endExclusive)) gridEndTime else timeRange.endExclusive

        val gridStartTime = effectiveStartTime.truncatedTo(ChronoUnit.HOURS)
        val visibleTimeSpan = TimeSpan(gridStartTime, effectiveEndTime)

        val totalHours = visibleTimeSpan.duration.toHours().toFloat() + (visibleTimeSpan.duration.toMinutesPart() / 60f)
        val gridHeightDp = rowHeightDp * totalHours
        val timeLabels = visibleTimeSpan.hourlyTimes().toList()

        WeekViewMetrics(
            days = days,
            columnCount = columnCount,
            today = today,
            leftOffsetDp = leftOffsetDp,
            topOffsetDp = topOffsetDp,
            effectiveStartTime = effectiveStartTime,
            effectiveEndTime = effectiveEndTime,
            gridStartTime = gridStartTime,
            rowHeightDp = rowHeightDp,
            totalHours = totalHours,
            gridHeightDp = gridHeightDp,
            timeLabels = timeLabels,
            visibleTimeSpan = visibleTimeSpan
        )
    }
}

@Composable
internal fun DayHeaderRow(
    days: List<LocalDate>,
    leftOffsetDp: Dp,
    topOffsetDp: Dp,
    columnWidth: Dp,
) {
    Row {
        Box(modifier = Modifier.size(leftOffsetDp, topOffsetDp)) // Spacer for time column
        days.forEach { date ->
            Box(
                modifier = Modifier
                    .size(columnWidth, topOffsetDp)
                    .padding(vertical = 2.dp),
                contentAlignment = Alignment.Center,
            ) {
                val shortName = date.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault())
                val shortDate = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)).replace(Regex("[^0-9]*[0-9]+$"), "")
                Text(
                    text = "$shortName\n$shortDate",
                    style = TextStyle(
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
}

@Composable
internal fun TimeAxisColumn(
    timeLabels: List<LocalTime>,
    now: LocalTime,
    gridStartTime: LocalTime,
    gridEndTime: LocalTime,
    rowHeightDp: Dp,
    gridHeightDp: Dp,
    leftOffsetDp: Dp,
    scrollState: ScrollState,
    showNowIndicator: Boolean,
    nowIndicatorColor: Color,
) {
    Box(
        modifier = Modifier
            .width(leftOffsetDp)
            .height(gridHeightDp), // Total height of the scrollable grid
    ) {
        // Regular time labels (hours)
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            timeLabels.forEach { timeLabel ->
                Box(modifier = Modifier.size(leftOffsetDp, rowHeightDp)) {
                    Text(
                        text = timeLabel.toString(),
                        style = TextStyle(fontSize = 12.sp, color = Color.Gray),
                        modifier = Modifier, // Align to top right or similar if needed
                    )
                }
            }
        }

        // Current time indicator label (HH:mm)
        if (showNowIndicator && now.isAfter(gridStartTime) && now.isBefore(gridEndTime)) {
            val nowPositionMinutes = ChronoUnit.MINUTES.between(gridStartTime, now)
            val nowPositionDp = (nowPositionMinutes / 60f * rowHeightDp.value).dp
            val density = LocalDensity.current.density

            Box(
                modifier = Modifier
                    .offset(y = nowPositionDp - (scrollState.value / density).dp) // Adjust for scroll
                    .width(leftOffsetDp),
            ) {
                Text(
                    text = String.format(Locale.getDefault(), "%02d:%02d", now.hour, now.minute),
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = nowIndicatorColor,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 4.dp),
                )
            }
        }
    }
}

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

@Composable
internal fun EventsPane(
    days: List<LocalDate>,
    events: List<Event.Single>,
    eventConfig: EventConfig,
    onEventClick: ((eventId: Long) -> Unit)?,
    onEventLongPress: ((eventId: Long) -> Unit)?,
    columnWidth: Dp,
    gridHeightDp: Dp,
    gridStartTime: LocalTime,
    effectiveEndTime: LocalTime,
    scalingFactor: Float,
) {
    days.forEachIndexed { dayIndex, date ->
        val eventsForDay = events.filter { it.date == date }
        if (eventsForDay.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .offset(x = dayIndex * columnWidth)
                    .size(columnWidth, gridHeightDp), // Height must be total grid height for proper event positioning
            ) {
                EventsWithOverlapHandling(
                    events = eventsForDay,
                    scalingFactor = scalingFactor,
                    eventConfig = eventConfig,
                    startTime = gridStartTime,
                    endTime = effectiveEndTime, // Pass effectiveEndTime for consistent boundary
                    columnWidth = columnWidth,
                    onEventClick = onEventClick,
                    onEventLongPress = onEventLongPress,
                )
            }
        }
    }
}

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
    val metrics = rememberWeekViewMetrics(dateRange, timeRange, events, scalingFactor)
    val scrollState = rememberScrollState()
    var now by remember { mutableStateOf(LocalTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            now = LocalTime.now()
            delay(1000) // update every second
        }
    }

    val todayHighlightColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    val nowIndicatorColor = MaterialTheme.colorScheme.error

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val availableWidth = maxWidth - metrics.leftOffsetDp
        val dynamicColumnWidthDp = if (metrics.columnCount > 0) (availableWidth / metrics.columnCount) else availableWidth // Avoid division by zero

        Column(modifier = Modifier.fillMaxSize()) {
            DayHeaderRow(
                days = metrics.days,
                leftOffsetDp = metrics.leftOffsetDp,
                topOffsetDp = metrics.topOffsetDp,
                columnWidth = dynamicColumnWidthDp
            )

            Row(modifier = Modifier.weight(1f)) {
                TimeAxisColumn(
                    timeLabels = metrics.timeLabels,
                    now = now,
                    gridStartTime = metrics.gridStartTime,
                    gridEndTime = metrics.effectiveEndTime,
                    rowHeightDp = metrics.rowHeightDp,
                    gridHeightDp = metrics.gridHeightDp,
                    leftOffsetDp = metrics.leftOffsetDp,
                    scrollState = scrollState,
                    showNowIndicator = showNowIndicator,
                    nowIndicatorColor = nowIndicatorColor
                )

                // Scrollable Grid Area (Canvas + Events)
                Box(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .weight(1f) // Fill remaining width
                        .height(metrics.gridHeightDp) // Match the height of the time axis
                ) {
                    GridCanvas(
                        modifier = Modifier.fillMaxSize(),
                        columnCount = metrics.columnCount,
                        rowHeightDp = metrics.rowHeightDp,
                        totalHours = metrics.totalHours,
                        days = metrics.days,
                        today = metrics.today,
                        todayHighlightColor = todayHighlightColor,
                        showNowIndicator = showNowIndicator,
                        now = now,
                        gridStartTime = metrics.gridStartTime,
                        effectiveEndTime = metrics.effectiveEndTime,
                        nowIndicatorColor = nowIndicatorColor
                    )
                    EventsPane(
                        days = metrics.days,
                        events = events,
                        eventConfig = eventConfig,
                        onEventClick = onEventClick,
                        onEventLongPress = onEventLongPress,
                        columnWidth = dynamicColumnWidthDp,
                        gridHeightDp = metrics.gridHeightDp,
                        gridStartTime = metrics.gridStartTime,
                        effectiveEndTime = metrics.effectiveEndTime,
                        scalingFactor = scalingFactor
                    )
                }
            }
        }
    }
}
