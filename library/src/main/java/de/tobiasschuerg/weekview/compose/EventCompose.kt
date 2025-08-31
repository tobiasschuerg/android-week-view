package de.tobiasschuerg.weekview.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.data.WeekViewConfig
import java.time.DayOfWeek
import java.time.LocalTime

/**
 * Composable that renders individual events on the week view grid.
 * Handles positioning, sizing, and styling of single events based on their time spans.
 */
@Composable
fun EventCompose(
    event: Event.Single,
    dayOfWeek: DayOfWeek,
    days: List<DayOfWeek>,
    startTime: LocalTime,
    endTime: LocalTime,
    rowHeightDp: Dp,
    columnWidthDp: Dp,
    leftOffsetDp: Dp,
    eventConfig: EventConfig,
    weekViewConfig: WeekViewConfig,
    onEventClick: ((eventId: Long) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    // Calculate event position and dimensions
    val dayIndex = days.indexOf(dayOfWeek)
    if (dayIndex < 0) return // Event not in visible days

    // Event should only be displayed if it's within the visible time range
    if (event.timeSpan.start.isAfter(endTime) || event.timeSpan.endExclusive.isBefore(startTime)) {
        return
    }

    // Calculate vertical position based on start time with proper alignment
    val startOffsetHours =
        maxOf(
            0f,
            (event.timeSpan.start.hour + event.timeSpan.start.minute / 60f) - startTime.hour,
        )
    // Add topOffsetDp to account for the day labels at the top of the grid
    val topOffsetDp = 32.dp
    val yOffsetDp = topOffsetDp + (startOffsetHours * rowHeightDp.value).dp

    // Calculate event height based on duration
    val durationHours = event.timeSpan.duration.toMinutes() / 60f
    val eventHeightDp = maxOf(8.dp, (durationHours * rowHeightDp.value).dp) // Minimum height

    // Calculate horizontal position to match grid exactly
    // Grid draws columns at: timeLabelWidth + (i * columnWidth)
    // We need to position events between grid lines with 1dp padding (half of 2dp grid line)
    val gridLinePadding = 1.dp
    val xOffsetDp = leftOffsetDp + (dayIndex * columnWidthDp.value).dp + gridLinePadding

    // Calculate event width to fit exactly between grid lines
    val eventWidthDp = columnWidthDp - (gridLinePadding * 2)

    // Event content
    Box(
        modifier =
            modifier
                .offset(x = xOffsetDp, y = yOffsetDp)
                .width(eventWidthDp)
                .height(eventHeightDp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(event.backgroundColor))
                .clickable { onEventClick?.invoke(event.id) }
                .padding(horizontal = 2.dp, vertical = 1.dp),
    ) {
        Column {
            // Event title
            Text(
                text = if (eventConfig.useShortNames) event.shortTitle else event.title,
                color = Color(event.textColor),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )

            // Event subtitle if enabled and available
            if (eventConfig.showSubtitle && !event.subTitle.isNullOrBlank()) {
                Text(
                    text = event.subTitle,
                    color = Color(event.textColor),
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Event time if enabled
            if (eventConfig.showTimeEnd) {
                val timeText = "${event.timeSpan.start} - ${event.timeSpan.endExclusive}"
                Text(
                    text = timeText,
                    color = Color(event.textColor).copy(alpha = 0.8f),
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Upper text if available
            if (!event.upperText.isNullOrBlank()) {
                Text(
                    text = event.upperText,
                    color = Color(event.textColor),
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Lower text if available
            if (!event.lowerText.isNullOrBlank()) {
                Text(
                    text = event.lowerText,
                    color = Color(event.textColor),
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

/**
 * Composable that renders all events for the week view.
 * Manages event positioning and handles event collections.
 */
@Composable
fun EventsCompose(
    events: List<Event.Single>,
    days: List<DayOfWeek>,
    startTime: LocalTime,
    endTime: LocalTime,
    rowHeightDp: Dp,
    columnWidthDp: Dp,
    leftOffsetDp: Dp,
    eventConfig: EventConfig,
    weekViewConfig: WeekViewConfig,
    onEventClick: ((eventId: Long) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        // Group events by day and render each
        events.forEach { event ->
            val eventDayOfWeek = event.date.dayOfWeek
            if (days.contains(eventDayOfWeek)) {
                EventCompose(
                    event = event,
                    dayOfWeek = eventDayOfWeek,
                    days = days,
                    startTime = startTime,
                    endTime = endTime,
                    rowHeightDp = rowHeightDp,
                    columnWidthDp = columnWidthDp,
                    leftOffsetDp = leftOffsetDp,
                    eventConfig = eventConfig,
                    weekViewConfig = weekViewConfig,
                    onEventClick = onEventClick,
                )
            }
        }
    }
}
