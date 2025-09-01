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
import java.time.DayOfWeek
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
    // Determine time range from weekData or use defaults
    val timeSpan = weekData.getTimeSpan()
    val startTime = timeSpan?.start ?: LocalTime.of(6, 0)
    val endTime = timeSpan?.endExclusive ?: LocalTime.of(20, 0)

    // Define the visible days for the week view
    val days =
        listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY,
        )

    var scale: Float by remember { mutableFloatStateOf(1f) }
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
            days = days,
            startTime = startTime,
            endTime = endTime,
            events = weekData.getSingleEvents(),
            eventConfig = eventConfig,
            onEventClick = onEventClick,
            onEventLongPress = onEventLongPress,
        )
    }
}
