package de.tobiasschuerg.weekview

import de.tobiasschuerg.weekview.model.Event
import java.time.LocalDate

/**
 * Grouped callbacks for the Compose WeekView API.
 * Kept small and nullable so callers can provide only the callbacks they need.
 */
public data class WeekViewActions(
    val onEventClick: ((event: Event) -> Unit)? = null,
    val onEventLongPress: ((event: Event) -> Unit)? = null,
    val onScalingFactorChange: ((Float) -> Unit)? = null,
    val onDayClick: ((date: LocalDate) -> Unit)? = null,
)
