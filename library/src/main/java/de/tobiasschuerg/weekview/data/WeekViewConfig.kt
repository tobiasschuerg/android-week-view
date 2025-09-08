package de.tobiasschuerg.weekview.data

data class WeekViewConfig(
    val scalingFactor: Float = 1f,
    val showCurrentTimeIndicator: Boolean = true,
    val highlightCurrentDay: Boolean = true,
    val currentTimeLineOnlyToday: Boolean = false,
)
