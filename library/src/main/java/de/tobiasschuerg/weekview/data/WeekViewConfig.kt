package de.tobiasschuerg.weekview.data

data class WeekViewConfig(
    val scalingFactor: Float = 1f,
    val minScalingFactor: Float = 0.5f,
    val maxScalingFactor: Float = 2f,
    val showCurrentTimeIndicator: Boolean = true,
    val highlightCurrentDay: Boolean = true,
    val currentTimeLineOnlyToday: Boolean = false,
)
