package de.tobiasschuerg.weekview.compose

import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.data.WeekData
import de.tobiasschuerg.weekview.data.WeekViewConfig
import de.tobiasschuerg.weekview.util.TimeSpan
import java.time.LocalTime

/**
 * Main Composable for the WeekView component.
 * This serves as the entry point for the Compose-based week view implementation.
 * Displays the background grid and renders events from the provided weekData.
 */
@Composable
fun WeekViewCompose(
    weekData: WeekData,
    weekViewConfig: WeekViewConfig,
    modifier: Modifier = Modifier,
    eventConfig: EventConfig = EventConfig(),
    onEventClick: ((eventId: Long) -> Unit)? = null,
    onEventLongPress: ((eventId: Long) -> Unit)? = null,
) {
    // Set a fixed start time and a default end time.
    // The grid will automatically extend beyond this end time if events are present.
    val startTime = LocalTime.of(8, 0)
    val endTime = LocalTime.of(18, 0)

    var scale: Float by remember { mutableFloatStateOf(weekViewConfig.scalingFactor) }
    val transformableState =
        rememberTransformableState { zoomChange, _, _ ->
            scale = (scale * zoomChange).coerceIn(0.5f, 2f)
            weekViewConfig.scalingFactor = scale
        }

    Box(
        modifier =
            modifier
                .transformable(state = transformableState),
    ) {
        // Render the background grid with integrated events
        WeekBackgroundCompose(
            scalingFactor = scale,
            modifier = Modifier.fillMaxSize(),
            dateRange = weekData.dateRange,
            timeRange = TimeSpan(startTime, endTime),
            events = weekData.getSingleEvents(),
            eventConfig = eventConfig,
            onEventClick = onEventClick,
            onEventLongPress = onEventLongPress,
        )
    }
}
