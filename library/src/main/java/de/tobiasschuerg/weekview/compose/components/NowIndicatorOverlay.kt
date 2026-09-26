package de.tobiasschuerg.weekview.compose.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.tobiasschuerg.weekview.compose.style.WeekViewStyle
import de.tobiasschuerg.weekview.compose.style.defaultWeekViewStyle
import de.tobiasschuerg.weekview.util.isStrictlyBetween
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

/** Width of the thin line that crosses the whole week. */
private val WEEK_LINE_STROKE = 1.5.dp

/** Width of the solid line marking today. */
private val TODAY_LINE_STROKE = 2.5.dp

/** How far the halo sticks out on either side of a line. */
private val HALO_WIDTH = 0.5.dp

private val DOT_RADIUS = 4.dp
private val DOT_RING = 1.5.dp

/** How much of the indicator color the week-wide line keeps; today's line is drawn fully opaque. */
private const val WEEK_LINE_ALPHA = 0.7f

/**
 * The current-time line, drawn on top of the events so it is never hidden by one.
 *
 * A thin line crosses the whole week and today's column is emphasized with a solid line, a halo that
 * keeps it readable over events, and a ringed dot. With [onlyToday] the week-wide line is left out
 * and only today is marked.
 */
@Composable
internal fun NowIndicatorOverlay(
    modifier: Modifier = Modifier,
    columnCount: Int,
    rowHeightDp: Dp,
    days: List<LocalDate>,
    today: LocalDate,
    now: LocalTime,
    gridStartTime: LocalTime,
    effectiveEndTime: LocalTime,
    onlyToday: Boolean,
    style: WeekViewStyle = defaultWeekViewStyle(),
) {
    // Same bounds as the pill in the time axis, so the line never shows without it.
    if (!now.isStrictlyBetween(gridStartTime, effectiveEndTime)) return

    Canvas(modifier = modifier) {
        val nowY = ChronoUnit.MINUTES.between(gridStartTime, now) / 60f * rowHeightDp.toPx()
        if (nowY < 0f || nowY > size.height) return@Canvas

        val todayColumnIndex = days.indexOf(today)
        val columnWidthPx = if (columnCount > 0) size.width / columnCount else size.width

        if (!onlyToday) {
            // No halo here: a second line next to this faint one is more than the other days need.
            drawLine(
                color = style.colors.nowIndicator.copy(alpha = WEEK_LINE_ALPHA),
                start = Offset(0f, nowY),
                end = Offset(size.width, nowY),
                strokeWidth = WEEK_LINE_STROKE.toPx(),
            )
        }

        if (todayColumnIndex < 0) return@Canvas
        val left = todayColumnIndex * columnWidthPx
        drawHaloedLine(
            start = Offset(left, nowY),
            end = Offset(left + columnWidthPx, nowY),
            color = style.colors.nowIndicator,
            haloColor = style.colors.nowIndicatorHalo,
            strokeWidth = TODAY_LINE_STROKE,
        )
        val dotCenter = Offset(left, nowY)
        drawCircle(color = style.colors.nowIndicatorHalo, radius = (DOT_RADIUS + DOT_RING).toPx(), center = dotCenter)
        drawCircle(color = style.colors.nowIndicator, radius = DOT_RADIUS.toPx(), center = dotCenter)
    }
}

/** Draws a line on a slightly wider line of [haloColor], so it stays readable on top of an event. */
private fun DrawScope.drawHaloedLine(
    start: Offset,
    end: Offset,
    color: Color,
    haloColor: Color,
    strokeWidth: Dp,
) {
    drawLine(color = haloColor, start = start, end = end, strokeWidth = (strokeWidth + HALO_WIDTH * 2).toPx())
    drawLine(color = color, start = start, end = end, strokeWidth = strokeWidth.toPx())
}
