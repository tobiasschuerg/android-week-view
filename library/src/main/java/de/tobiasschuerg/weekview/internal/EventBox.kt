package de.tobiasschuerg.weekview.internal

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.tobiasschuerg.weekview.EventConfig
import de.tobiasschuerg.weekview.content.EventContent
import de.tobiasschuerg.weekview.content.EventContentScope
import de.tobiasschuerg.weekview.internal.time.toLocalString
import de.tobiasschuerg.weekview.model.Event
import java.util.Locale

/**
 * Places a timed entry at [x], [y] with the given size, handles its clicks and accessibility, and lets
 * [eventContent] draw it inside the spacing from [EventConfig.eventSpacingDp].
 */
@Composable
internal fun EventBox(
    event: Event.Single,
    x: Dp,
    y: Dp,
    width: Dp,
    height: Dp,
    eventConfig: EventConfig,
    locale: Locale,
    onEventClick: ((event: Event) -> Unit)?,
    onEventLongPress: ((event: Event) -> Unit)?,
    eventContent: EventContent,
    modifier: Modifier = Modifier,
) {
    val spacing = eventConfig.eventSpacingDp.coerceAtLeast(0).dp
    Box(
        modifier =
            modifier
                .testTag("EventView_${event.id}")
                .offset(x = x, y = y)
                .size(width = width, height = height)
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
                width = width - spacing * 2,
                height = height - spacing * 2,
                eventConfig = eventConfig,
                locale = locale,
            ),
        )
    }
}
