package de.tobiasschuerg.weekview.internal.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import de.tobiasschuerg.weekview.style.WeekViewColors
import java.time.LocalDate

/**
 * Tints today's column of a period grid. The grid draws no lines: the entries and the placeholders
 * of the empty periods mark the cells themselves.
 */
@Composable
internal fun PeriodTodayHighlight(
    days: List<LocalDate>,
    today: LocalDate,
    colors: WeekViewColors,
    modifier: Modifier = Modifier,
) {
    val todayIndex = days.indexOf(today)
    if (todayIndex < 0) return
    Canvas(modifier = modifier) {
        val columnWidthPx = size.width / days.size
        drawRect(
            color = colors.todayHighlight,
            topLeft = Offset(todayIndex * columnWidthPx, 0f),
            size = Size(columnWidthPx, size.height),
        )
    }
}
