package de.tobiasschuerg.weekview.internal.layout

/** Where the scrollable grid's content puts each time of day, as last laid out. */
internal interface GridGeometry {
    /** Time at [contentY] (px from the top of the grid content), in minutes after midnight. */
    fun minuteAt(contentY: Float): Float

    /** Offset of [minute] (minutes after midnight) from the top of the grid content, in px. */
    fun yOf(minute: Float): Float
}
