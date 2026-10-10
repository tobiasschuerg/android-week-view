package de.tobiasschuerg.weekview.style

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

/** Default values used by the week view. */
object WeekViewDefaults {
    /** Colors derived from the current Material theme; override the ones you need. */
    @Composable
    fun colors(
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
}
