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
)

@Composable
fun defaultWeekViewColors(
    todayHighlight: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
    nowIndicator: Color = MaterialTheme.colorScheme.error,
    dayHeaderText: Color = Color.Gray,
    timeLabelTextColor: Color = Color.Gray,
    gridLineColor: Color = Color.LightGray,
    currentDayBackground: Color = MaterialTheme.colorScheme.primary,
    currentDayText: Color = Color(0xFF000000),
): WeekViewColors =
    remember(todayHighlight, nowIndicator, dayHeaderText, timeLabelTextColor, gridLineColor, currentDayBackground, currentDayText) {
        WeekViewColors(
            todayHighlight = todayHighlight,
            nowIndicator = nowIndicator,
            dayHeaderText = dayHeaderText,
            timeLabelTextColor = timeLabelTextColor,
            gridLineColor = gridLineColor,
            currentDayBackground = currentDayBackground.copy(alpha = 0.2f),
            currentDayText = currentDayText,
        )
    }
