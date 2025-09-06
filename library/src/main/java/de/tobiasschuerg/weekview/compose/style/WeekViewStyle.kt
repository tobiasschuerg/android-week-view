package de.tobiasschuerg.weekview.compose.style

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

@Immutable
data class WeekViewColors(
    val todayHighlight: Color,
    val nowIndicator: Color,
    val dayHeaderText: Color,
    val timeLabelTextColor: Color,
    val gridLineColor: Color,
)

@Composable
fun defaultWeekViewColors(
    todayHighlight: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
    nowIndicator: Color = MaterialTheme.colorScheme.error,
    dayHeaderText: Color = Color.Gray,
    timeLabelTextColor: Color = Color.Gray,
    gridLineColor: Color = Color.LightGray,
): WeekViewColors =
    remember(todayHighlight, nowIndicator, dayHeaderText, timeLabelTextColor, gridLineColor) {
        WeekViewColors(
            todayHighlight = todayHighlight,
            nowIndicator = nowIndicator,
            dayHeaderText = dayHeaderText,
            timeLabelTextColor = timeLabelTextColor,
            gridLineColor = gridLineColor,
        )
    }

// This is the main configuration object you'd pass around.
// It can be expanded later to include typography, dimensions, etc.
@Immutable
data class WeekViewStyle(
    val colors: WeekViewColors,
    // Future: val typography: WeekViewTypography,
    // Future: val dimensions: WeekViewDimensions
)

@Composable
fun defaultWeekViewStyle(colors: WeekViewColors = defaultWeekViewColors()): WeekViewStyle =
    remember(colors) {
        WeekViewStyle(
            colors = colors,
        )
    }
