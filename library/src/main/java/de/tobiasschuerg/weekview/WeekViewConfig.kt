package de.tobiasschuerg.weekview

import java.util.Locale

data class WeekViewConfig(
    val scalingFactor: Float = 1f,
    val minScalingFactor: Float = 0.5f,
    val maxScalingFactor: Float = 2f,
    val showCurrentTimeIndicator: Boolean = true,
    val highlightCurrentDay: Boolean = true,
    val currentTimeLineOnlyToday: Boolean = false,
    /** Locale for day names, dates and times; null follows the app's current UI locale (`LocalConfiguration`). */
    val locale: Locale? = null,
    /**
     * Pads a short schedule with whole hours so the grid fills the available height instead of
     * leaving blank space below it. When false, the grid shows only the requested time range and
     * the hours its events need.
     */
    val fillViewport: Boolean = true,
) {
    init {
        require(minScalingFactor > 0f) { "minScalingFactor must be positive, but was $minScalingFactor" }
        require(minScalingFactor <= maxScalingFactor) {
            "minScalingFactor ($minScalingFactor) must be <= maxScalingFactor ($maxScalingFactor)"
        }
        require(scalingFactor in minScalingFactor..maxScalingFactor) {
            "scalingFactor ($scalingFactor) must be between minScalingFactor ($minScalingFactor) and maxScalingFactor ($maxScalingFactor)"
        }
    }
}
