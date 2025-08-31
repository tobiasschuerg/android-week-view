package de.tobiasschuerg.weekview.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.data.WeekData
import de.tobiasschuerg.weekview.data.WeekViewConfig

@Composable
fun WeekViewCompose(
    weekData: WeekData,
    eventConfig: EventConfig = EventConfig(),
    weekViewConfig: WeekViewConfig,
    modifier: Modifier = Modifier,
    onEventClick: ((eventId: Long) -> Unit)? = null,
    onEventContextMenu: ((eventId: Long) -> Unit)? = null,
) {
    // Basisstruktur: Hintergrundraster und Events
    WeekBackgroundCompose(
        weekViewConfig = weekViewConfig,
        modifier = modifier,
    )
    // TODO: Events rendern
}
