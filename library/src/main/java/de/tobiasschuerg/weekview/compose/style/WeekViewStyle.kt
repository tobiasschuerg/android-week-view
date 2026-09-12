package de.tobiasschuerg.weekview.compose.style

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember

/**
 * Main styling object passed to the week view. Currently holds the [WeekViewColors];
 * typography and dimensions may be added later.
 */
@Immutable
data class WeekViewStyle(
    val colors: WeekViewColors,
)

@Composable
fun defaultWeekViewStyle(colors: WeekViewColors = defaultWeekViewColors()): WeekViewStyle =
    remember(colors) {
        WeekViewStyle(
            colors = colors,
        )
    }
