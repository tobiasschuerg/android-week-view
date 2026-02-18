package de.tobiasschuerg.weekview.data

/**
 * Configures the appearance of an event in the week view.
 */
data class EventConfig(
    /** If true, always uses the full event title in both portrait and landscape mode.
     * If false (default), uses short event names in portrait mode and full names in landscape mode. */
    val alwaysUseFullName: Boolean = false,
    /** Show the event start time in the event view. */
    val showTimeStart: Boolean = true,
    /** Show the upper text field of the event. */
    val showUpperText: Boolean = true,
    /** Show the event subtitle below the title. */
    val showSubtitle: Boolean = true,
    /** Show the lower text field of the event. */
    val showLowerText: Boolean = true,
    /** Show the event end time in the event view. */
    val showTimeEnd: Boolean = true,
)
