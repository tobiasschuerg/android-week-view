package de.tobiasschuerg.weekview.content

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import de.tobiasschuerg.weekview.EventConfig
import de.tobiasschuerg.weekview.model.Event
import java.util.Locale

/**
 * Everything an [EventContent] slot gets to draw one timed entry.
 *
 * The week view positions and sizes the entry, handles clicks and sets its accessibility
 * description; the content only draws inside it. [width] and [height] are the entry's size
 * after [EventConfig.eventSpacingDp], so the content should fill exactly that space.
 */
@Immutable
data class EventContentScope(
    val event: Event.Single,
    val width: Dp,
    val height: Dp,
    val eventConfig: EventConfig,
    val locale: Locale,
)
