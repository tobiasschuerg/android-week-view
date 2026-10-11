package de.tobiasschuerg.weekview.internal.layout

/** Geometry of the timeline: every hour is a row of the same height. */
internal data class HourGridGeometry(
    /** Time at the top of the grid content, in minutes after midnight. */
    val startMinute: Float,
    val rowHeightPx: Float,
) : GridGeometry {
    override fun minuteAt(contentY: Float): Float = startMinute + contentY / rowHeightPx * MINUTES_PER_HOUR

    override fun yOf(minute: Float): Float = (minute - startMinute) / MINUTES_PER_HOUR * rowHeightPx

    private companion object {
        const val MINUTES_PER_HOUR = 60f
    }
}
