package de.tobiasschuerg.weekview.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.data.WeekViewConfig
import de.tobiasschuerg.weekview.util.EventOverlapCalculator
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Composable that renders individual events on the week view grid.
 * Handles positioning, sizing, and styling of single events based on their time spans.
 */
@Composable
fun EventCompose(
    event: Event.Single,
    weekViewConfig: WeekViewConfig,
    eventConfig: EventConfig,
    startTime: LocalTime,
    columnWidth: Dp,
    eventLayout: EventOverlapCalculator.EventLayout,
    modifier: Modifier = Modifier,
    onEventClick: ((eventId: Long) -> Unit)? = null,
    onEventLongPress: ((eventId: Long) -> Unit)? = null,
) {
    val density = LocalDensity.current

    // Calculate event positioning based on time
    val startMinutes =
        (event.timeSpan.start.hour - startTime.hour) * 60 +
            (event.timeSpan.start.minute - startTime.minute)
    val durationMinutes = event.timeSpan.duration.toMinutes().toInt()

    // Calculate dimensions with scaling factor
    val topOffset = with(density) { (startMinutes * weekViewConfig.scalingFactor).dp }
    val eventHeight = with(density) { (durationMinutes * weekViewConfig.scalingFactor).dp }

    // Apply overlap layout calculations
    val eventWidth = columnWidth * eventLayout.widthFraction
    val horizontalOffset = columnWidth * eventLayout.offsetFraction

    // Event styling
    val backgroundColor = Color(event.backgroundColor)
    val textColor = Color(event.textColor)
    val cornerRadius = 4.dp
    val eventPadding = 4.dp

    // Determine which title to show based on config
    val displayTitle =
        if (eventConfig.useShortNames && event.shortTitle.isNotBlank()) {
            event.shortTitle
        } else {
            event.title
        }

    Box(
        modifier =
            modifier
                .padding(1.dp)
                .offset(x = horizontalOffset, y = topOffset)
                .size(width = eventWidth, height = eventHeight)
                .clip(RoundedCornerShape(cornerRadius))
                .background(backgroundColor)
                .pointerInput(event.id) {
                    detectTapGestures(
                        onTap = { onEventClick?.invoke(event.id) },
                        onLongPress = { onEventLongPress?.invoke(event.id) },
                    )
                }
                .padding(eventPadding),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
        ) {
            // Main title
            Text(
                text = displayTitle,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = if (eventConfig.showSubtitle || eventConfig.showTimeEnd) 1 else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )

            // Subtitle (if enabled and available)
            if (eventConfig.showSubtitle && event.subTitle?.isNotBlank() == true) {
                Text(
                    text = event.subTitle,
                    color = textColor.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Time information (if enabled)
            if (eventConfig.showTimeEnd) {
                val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                val timeText =
                    if (eventConfig.showTimeEnd) {
                        "${event.timeSpan.start.format(timeFormatter)} - ${event.timeSpan.endExclusive.format(timeFormatter)}"
                    } else {
                        event.timeSpan.start.format(timeFormatter)
                    }

                Text(
                    text = timeText,
                    color = textColor.copy(alpha = 0.7f),
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
 * Composable for rendering multiple events with overlap handling.
 * Calculates overlap layouts and renders each event with proper positioning.
 */
@Composable
fun EventsWithOverlapHandling(
    events: List<Event.Single>,
    weekViewConfig: WeekViewConfig,
    eventConfig: EventConfig,
    startTime: LocalTime,
    endTime: LocalTime,
    columnWidth: Dp,
    modifier: Modifier = Modifier,
    onEventClick: ((eventId: Long) -> Unit)? = null,
    onEventLongPress: ((eventId: Long) -> Unit)? = null,
) {
    // Filter events for the current day and time range
    val visibleEvents =
        events.filter { event ->
            // Check if event is within the visible time range
            event.timeSpan.start < endTime && event.timeSpan.endExclusive > startTime
        }

    if (visibleEvents.isEmpty()) return

    // Calculate overlap layouts for all visible events
    val eventLayouts = EventOverlapCalculator.calculateEventLayouts(visibleEvents)

    Box(modifier = modifier) {
        visibleEvents.forEach { event ->
            val layout = eventLayouts[event.id]
            if (layout != null) {
                EventCompose(
                    event = event,
                    weekViewConfig = weekViewConfig,
                    eventConfig = eventConfig,
                    startTime = startTime,
                    columnWidth = columnWidth,
                    eventLayout = layout,
                    onEventClick = onEventClick,
                    onEventLongPress = onEventLongPress,
                )
            }
        }
    }
}
