package de.tobiasschuerg.weekview.compose

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.util.EventOverlapCalculator
import de.tobiasschuerg.weekview.util.EventPositionUtil
import de.tobiasschuerg.weekview.util.toLocalString
import java.time.LocalTime
import java.util.Locale

/**
 * Places a single event on the week view grid.
 * Handles positioning, sizing, clicks and accessibility; what the entry looks like is drawn by [eventContent].
 */
@Composable
fun EventCompose(
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

    // Apply overlap layout calculations
    val eventWidth = columnWidth * eventLayout.widthFraction
    val horizontalOffset = columnWidth * eventLayout.offsetFraction
    val spacing = eventConfig.eventSpacingDp.coerceAtLeast(0).dp

    Box(
        modifier =
            modifier
                .testTag("EventView_${event.id}")
                .offset(x = horizontalOffset, y = topOffset)
                .size(width = eventWidth, height = eventHeight)
                .padding(spacing)
                .combinedClickable(
                    enabled = onEventClick != null || onEventLongPress != null,
                    role = Role.Button,
                    onClick = { onEventClick?.invoke(event) },
                    onLongClick = onEventLongPress?.let { { it(event) } },
                ).semantics {
                    contentDescription =
                        "${event.title}, ${event.timeSpan.start.toLocalString(locale)} - " +
                        event.timeSpan.endExclusive.toLocalString(locale)
                },
    ) {
        eventContent(
            EventContentScope(
                event = event,
                width = eventWidth - spacing * 2,
                height = eventHeight - spacing * 2,
                eventConfig = eventConfig,
                locale = locale,
            ),
        )
    }
}
