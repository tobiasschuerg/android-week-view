package de.tobiasschuerg.weekview.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.util.EventOverlapCalculator
import java.time.LocalTime

/**
 * Composable for rendering multiple events with overlap handling.
 * Calculates overlap layouts and renders each event with proper positioning.
 */
@Composable
fun EventsWithOverlapHandling(
    modifier: Modifier = Modifier,
    scalingFactor: Float = 1f,
    events: List<Event.Single>,
    eventConfig: EventConfig,
    startTime: LocalTime,
    endTime: LocalTime,
    columnWidth: Dp,
    onEventClick: ((event: Event) -> Unit)? = null,
    onEventLongPress: ((event: Event) -> Unit)? = null,
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
            val layout: EventOverlapCalculator.EventLayout? = eventLayouts[event.id]
            if (layout != null) {
                EventCompose(
                    event = event,
                    scalingFactor = scalingFactor,
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
