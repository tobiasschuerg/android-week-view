package de.tobiasschuerg.weekview

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import de.tobiasschuerg.weekview.content.EventContent
import de.tobiasschuerg.weekview.content.FilledEventContent
import de.tobiasschuerg.weekview.internal.WeekBackgroundCompose
import de.tobiasschuerg.weekview.model.TimeSpan
import de.tobiasschuerg.weekview.model.WeekData
import de.tobiasschuerg.weekview.style.WeekViewColors
import de.tobiasschuerg.weekview.style.WeekViewDefaults
import java.time.Duration
import java.time.LocalTime

/**
 * Main Composable for the WeekView component.
 * This serves as the entry point for the Compose-based week view implementation.
 * Displays the background grid and renders events from the provided weekData.
 * Timed entries are drawn by [eventContent], which defaults to [FilledEventContent].
 * Colors come from [colors], which default to the current Material theme.
 *
 * Pinch-to-zoom only activates on multi-touch (2+ fingers) so that single-finger
 * horizontal swipes pass through to a parent HorizontalPager or similar container.
 */
@Composable
public fun WeekView(
    weekData: WeekData,
    weekViewConfig: WeekViewConfig,
    modifier: Modifier = Modifier,
    eventConfig: EventConfig = EventConfig(),
    actions: WeekViewActions = WeekViewActions(),
    state: WeekViewState = rememberWeekViewState(weekViewConfig.scalingFactor),
    colors: WeekViewColors = WeekViewDefaults.colors(),
    eventContent: EventContent = { FilledEventContent(it) },
) {
    LaunchedEffect(weekViewConfig.scalingFactor) {
        state.syncConfiguredScalingFactor(weekViewConfig.scalingFactor)
    }
    val clampedScalingFactor =
        state.scalingFactor.coerceIn(weekViewConfig.minScalingFactor, weekViewConfig.maxScalingFactor)
    val activeWeekConfig = weekViewConfig.copy(scalingFactor = clampedScalingFactor)
    // The gesture handler outlives recompositions, so it reads the latest callback instead of capturing the first one.
    val onScalingFactorChange by rememberUpdatedState(actions.onScalingFactorChange)

    Box(
        modifier =
            modifier
                .pointerInput(state, weekViewConfig.minScalingFactor, weekViewConfig.maxScalingFactor) {
                    awaitEachGesture {
                        awaitFirstDown(requireUnconsumed = false)
                        do {
                            val event = awaitPointerEvent()
                            if (event.changes.size >= 2) {
                                val zoom = event.calculateZoom()
                                if (zoom != 1f) {
                                    state.applyZoom(
                                        zoom = zoom,
                                        minScalingFactor = weekViewConfig.minScalingFactor,
                                        maxScalingFactor = weekViewConfig.maxScalingFactor,
                                    )?.let { newScalingFactor ->
                                        onScalingFactorChange?.invoke(newScalingFactor)
                                    }
                                    event.changes.forEach { it.consume() }
                                }
                            }
                        } while (event.changes.any { it.pressed })
                    }
                },
    ) {
        // Render the background grid with integrated events
        WeekBackgroundCompose(
            modifier = Modifier.fillMaxSize(),
            dateRange = weekData.dateRange,
            timeRange =
                weekData.timeSpan ?: TimeSpan.of(
                    LocalTime.of(6, 0),
                    Duration.ofHours(12),
                ),
            events = weekData.singleEvents,
            allDayEvents = weekData.allDayEvents,
            multiDayEvents = weekData.multiDayEvents,
            eventConfig = eventConfig,
            onEventClick = actions.onEventClick,
            onEventLongPress = actions.onEventLongPress,
            onDayClick = actions.onDayClick,
            weekViewConfig = activeWeekConfig,
            colors = colors,
            scrollState = state.scrollState,
            eventContent = eventContent,
        )
    }
}
