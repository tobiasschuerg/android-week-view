package de.tobiasschuerg.weekview.internal

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import de.tobiasschuerg.weekview.EventConfig
import de.tobiasschuerg.weekview.content.EventContent
import de.tobiasschuerg.weekview.content.FilledEventContent
import de.tobiasschuerg.weekview.internal.layout.EventOverlapCalculator
import de.tobiasschuerg.weekview.internal.layout.EventPositionUtil
import de.tobiasschuerg.weekview.model.Event
import java.time.LocalTime
import java.util.Locale

/**
 * Places a single event on the timeline grid, as high and as far down as its time span says.
 * What the entry looks like is drawn by [eventContent].
 */
@Composable
internal fun EventCompose(
    modifier: Modifier = Modifier,
    event: Event.Single,
    scalingFactor: Float,
    eventConfig: EventConfig,
    startTime: LocalTime,
    columnWidth: Dp,
    eventLayout: EventOverlapCalculator.EventLayout,
    locale: Locale = LocalConfiguration.current.locales[0],
    onEventClick: ((event: Event) -> Unit)? = null,
    onEventLongPress: ((event: Event) -> Unit)? = null,
    eventContent: EventContent = { FilledEventContent(it) },
) {
    val (topOffset, eventHeight) =
        EventPositionUtil.calculateVerticalOffsets(
            event = event,
            startTime = startTime,
            scalingFactor = scalingFactor,
        )

    EventBox(
        event = event,
        x = columnWidth * eventLayout.offsetFraction,
        y = topOffset,
        width = columnWidth * eventLayout.widthFraction,
        height = eventHeight,
        eventConfig = eventConfig,
        locale = locale,
        onEventClick = onEventClick,
        onEventLongPress = onEventLongPress,
        eventContent = eventContent,
        modifier = modifier,
    )
}
