package de.tobiasschuerg.weekview.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.data.WeekData
import de.tobiasschuerg.weekview.data.WeekViewConfig
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
    onEventContextMenu: ((eventId: Long) -> Unit)? = null,
) {
    // Determine time range from weekData or use defaults
    val timeSpan = weekData.getTimeSpan()
    val startTime = timeSpan?.start ?: LocalTime.of(6, 0)
    val endTime = timeSpan?.endExclusive ?: LocalTime.of(20, 0)

    // Define the visible days for the week view
    val days =
        listOf(
            java.time.DayOfWeek.MONDAY,
            java.time.DayOfWeek.TUESDAY,
            java.time.DayOfWeek.WEDNESDAY,
            java.time.DayOfWeek.THURSDAY,
            java.time.DayOfWeek.FRIDAY,
        )

    Box(modifier = modifier) {
        // Render the background grid with integrated events
        WeekBackgroundCompose(
            weekViewConfig = weekViewConfig,
            modifier = Modifier.fillMaxSize(),
            days = days,
            startTime = startTime,
            endTime = endTime,
            events = weekData.getSingleEvents(),
            eventConfig = eventConfig,
            onEventClick = onEventClick,
        )
    }
}
