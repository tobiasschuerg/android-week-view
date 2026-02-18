package de.tobiasschuerg.weekview.compose

import de.tobiasschuerg.weekview.data.Event

/**
 * Grouped callbacks for the Compose WeekView API.
 * Kept small and nullable so callers can provide only the callbacks they need.
 */
data class WeekViewActions(
    val onEventClick: ((event: Event) -> Unit)? = null,
    val onEventLongPress: ((event: Event) -> Unit)? = null,
    val onSwipeLeft: (() -> Unit)? = null,
    val onSwipeRight: (() -> Unit)? = null,
    val onScalingFactorChange: ((Float) -> Unit)? = null,
)
