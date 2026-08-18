package de.tobiasschuerg.weekview.data

import java.util.Locale

data class WeekViewConfig(
    val scalingFactor: Float = 1f,
    val minScalingFactor: Float = 0.5f,
    val maxScalingFactor: Float = 2f,
    val showCurrentTimeIndicator: Boolean = true,
    val highlightCurrentDay: Boolean = true,
    val currentTimeLineOnlyToday: Boolean = false,
    val locale: Locale = Locale.getDefault(),
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
