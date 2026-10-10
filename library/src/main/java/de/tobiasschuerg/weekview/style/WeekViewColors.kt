package de.tobiasschuerg.weekview.style

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** Colors used by the week view; build one with [WeekViewDefaults.colors] and override what you need. */
@Immutable
data class WeekViewColors(
    val todayHighlight: Color,
    val nowIndicator: Color,
    val dayHeaderText: Color,
    val timeLabelTextColor: Color,
    val gridLineColor: Color,
    val currentDayBackground: Color,
    val currentDayText: Color,
    /** Text of the current-time pill in the time axis, drawn on [nowIndicator]. */
    val nowIndicatorLabelText: Color = Color.White,
    /** Outline behind the current-time line, keeping it readable where it crosses an event. */
    val nowIndicatorHalo: Color = Color.White,
)
