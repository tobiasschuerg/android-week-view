package de.tobiasschuerg.weekview.compose.style

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

/** Colors used by the week view; build one with [defaultWeekViewColors] and override what you need. */
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

@Composable
fun defaultWeekViewColors(
    todayHighlight: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
    nowIndicator: Color = MaterialTheme.colorScheme.error,
    dayHeaderText: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    timeLabelTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    gridLineColor: Color = MaterialTheme.colorScheme.outlineVariant,
    currentDayBackground: Color = MaterialTheme.colorScheme.primary,
    currentDayText: Color = MaterialTheme.colorScheme.onSurface,
    nowIndicatorLabelText: Color = MaterialTheme.colorScheme.onError,
    nowIndicatorHalo: Color = MaterialTheme.colorScheme.surface,
): WeekViewColors =
    remember(
        todayHighlight,
        nowIndicator,
        dayHeaderText,
        timeLabelTextColor,
        gridLineColor,
        currentDayBackground,
        currentDayText,
        nowIndicatorLabelText,
        nowIndicatorHalo,
    ) {
        WeekViewColors(
            todayHighlight = todayHighlight,
            nowIndicator = nowIndicator,
            dayHeaderText = dayHeaderText,
            timeLabelTextColor = timeLabelTextColor,
            gridLineColor = gridLineColor,
            currentDayBackground = currentDayBackground.copy(alpha = 0.2f),
            currentDayText = currentDayText,
            nowIndicatorLabelText = nowIndicatorLabelText,
            nowIndicatorHalo = nowIndicatorHalo,
        )
    }
