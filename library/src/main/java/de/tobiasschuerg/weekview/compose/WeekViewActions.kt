package de.tobiasschuerg.weekview.compose

/**
 * Grouped callbacks for the Compose WeekView API.
 * Kept small and nullable so callers can provide only the callbacks they need.
 */
data class WeekViewActions(
    val onEventClick: ((eventId: Long) -> Unit)? = null,
    val onEventLongPress: ((eventId: Long) -> Unit)? = null,
    val onSwipeLeft: (() -> Unit)? = null,
    val onSwipeRight: (() -> Unit)? = null,
    val onScalingFactorChange: ((Float) -> Unit)? = null,
)
