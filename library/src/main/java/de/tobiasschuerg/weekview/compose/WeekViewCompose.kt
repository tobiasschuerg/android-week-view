package de.tobiasschuerg.weekview.compose

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.data.WeekData
import de.tobiasschuerg.weekview.data.WeekViewConfig
import de.tobiasschuerg.weekview.util.TimeSpan
import java.time.Duration
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
    onSwipeLeft: (() -> Unit)? = null,
    onSwipeRight: (() -> Unit)? = null,
) {
    var scale: Float by remember { mutableFloatStateOf(weekViewConfig.scalingFactor) }
    val transformableState =
        rememberTransformableState { zoomChange, _, _ ->
            scale = (scale * zoomChange).coerceIn(0.5f, 2f)
            weekViewConfig.scalingFactor = scale
        }

    var offsetX by remember { mutableFloatStateOf(0f) }

    Box(
        modifier =
            modifier
                .transformable(state = transformableState)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount
                        },
                        onDragEnd = {
                            val threshold = size.width / 4
                            when {
                                offsetX > threshold -> onSwipeRight?.invoke()
                                offsetX < -threshold -> onSwipeLeft?.invoke()
                            }
                            offsetX = 0f
                        },
                    )
                },
    ) {
        // Render the background grid with integrated events
        WeekBackgroundCompose(
            scalingFactor = scale,
            modifier = Modifier.fillMaxSize(),
            dateRange = weekData.dateRange,
            timeRange = weekData.getTimeSpan() ?: TimeSpan.of(LocalTime.of(6, 0), Duration.ofHours(12)),
            events = weekData.getSingleEvents(),
            eventConfig = eventConfig,
            onEventClick = onEventClick,
            onEventLongPress = onEventLongPress,
        )
    }
}
