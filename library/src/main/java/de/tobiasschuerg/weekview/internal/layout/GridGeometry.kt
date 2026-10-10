package de.tobiasschuerg.weekview.internal.layout

/** Where the scrollable grid's content puts each time of day, as last laid out. */
internal data class GridGeometry(
    /** Time at the top of the grid content, in minutes after midnight. */
    val startMinute: Float,
    val rowHeightPx: Float,
) {
    /** Time at [contentY] (px from the top of the grid content), in minutes after midnight. */
    fun minuteAt(contentY: Float): Float = startMinute + contentY / rowHeightPx * MINUTES_PER_HOUR

    /** Offset of [minute] from the top of the grid content, in px. */
    fun yOf(minute: Float): Float = (minute - startMinute) / MINUTES_PER_HOUR * rowHeightPx

    private companion object {
        const val MINUTES_PER_HOUR = 60f
    }
}
