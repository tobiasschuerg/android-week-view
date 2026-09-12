package de.tobiasschuerg.weekview.util

/** How the start/end time labels of an entry are laid out, see [EventFieldLayout.resolve]. */
enum class TimeLabelMode {
    /** Start on its own line above the title, end pinned below the rest of the fields. */
    STACKED,

    /** Small labels in the top-left and bottom-right corners, title centred between them. */
    CORNERS,

    /** Entry too low for any time label; only the title (and whatever else fits) is shown. */
    NONE,
}
