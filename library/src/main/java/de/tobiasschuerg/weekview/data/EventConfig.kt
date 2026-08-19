package de.tobiasschuerg.weekview.data

/**
 * Configures the appearance of an event in the week view.
 *
 * The fields below enable a piece of content; whether it actually renders can still
 * depend on the entry's height. The title is always shown; time, then location
 * (subtitle), then upper text are dropped in that order on entries too short to fit
 * everything, so the most useful information survives rather than whatever happens to
 * be first in the layout. See [de.tobiasschuerg.weekview.util.EventPositionUtil] for
 * the height thresholds.
 */
data class EventConfig(
    /** If true, always uses the full event title in both portrait and landscape mode.
     * If false (default), uses short event names in portrait mode and full names in landscape mode. */
    val alwaysUseFullName: Boolean = false,
    /** Show the event start time in the event view, room permitting. */
    val showTimeStart: Boolean = true,
    /** Show the upper text field of the event, room permitting. */
    val showUpperText: Boolean = true,
    /** Show the event subtitle below the title, room permitting. */
    val showSubtitle: Boolean = true,
    /** Show the lower text field of the event, room permitting. */
    val showLowerText: Boolean = true,
    /** Show the event end time in the event view, room permitting. */
    val showTimeEnd: Boolean = true,
    /** Spacing in dp between adjacent events. Set to 0 for no spacing. */
    val eventSpacingDp: Int = 1,
)
