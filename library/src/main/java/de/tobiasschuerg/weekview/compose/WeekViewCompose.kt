package de.tobiasschuerg.weekview.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.tobiasschuerg.weekview.data.WeekData
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.data.WeekViewConfig

/**
 * Main Composable for the WeekView component.
 * This serves as the entry point for the Compose-based week view implementation.
 * Currently displays the background grid; event rendering will be added in upcoming steps.
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
    // TODO: Implement event rendering using weekData and eventConfig
    // TODO: Implement event interactions using onEventClick and onEventContextMenu

    // Render the background grid
    WeekBackgroundCompose(
        weekViewConfig = weekViewConfig,
        modifier = modifier
    )
}
