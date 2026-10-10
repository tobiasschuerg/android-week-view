package de.tobiasschuerg.weekview.internal.layout

/** The time of day kept under the user's fingers while pinch-zooming. */
internal data class ZoomAnchor(
    /** Anchored time, in minutes after midnight. */
    val minute: Float,
    /** Where that time should stay, in px from the top of the grid viewport. */
    val viewportY: Float,
)
