package de.tobiasschuerg.weekview.internal.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import de.tobiasschuerg.weekview.internal.layout.gridHeightDp
import de.tobiasschuerg.weekview.internal.time.AxisTimeLabel
import de.tobiasschuerg.weekview.internal.time.isStrictlyBetween
import de.tobiasschuerg.weekview.internal.time.toAxisLabel
import de.tobiasschuerg.weekview.style.WeekViewStyle
import de.tobiasschuerg.weekview.style.defaultWeekViewStyle
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.Locale

/**
 * The hour labels and the current-time pill on the left of the grid.
 *
 * It does not scroll by itself: it is placed in the same scroll container as the grid, so the two
 * share one scroll range and cannot drift apart.
 */
@Composable
internal fun TimeAxisColumn(
    timeLabels: List<LocalTime>,
    now: LocalTime,
    gridStartTime: LocalTime,
    gridEndTime: LocalTime,
    rowHeightDp: Dp,
    gridHeightDp: Dp,
    /** Measured by the caller, which also needs the axis width to lay out the columns next to it. */
    labelMetrics: TimeAxisLabelMetrics,
    showNowIndicator: Boolean,
    locale: Locale,
    style: WeekViewStyle = defaultWeekViewStyle(),
) {
    // Formatted once per set of hours: the axis recomposes on every zoom step, the hours rarely change.
    val hourLabels = remember(timeLabels, locale) { timeLabels.map { it.toAxisLabel(locale) } }
    val showNowPill = showNowIndicator && now.isStrictlyBetween(gridStartTime, gridEndTime)

    Box(
        modifier =
            Modifier
                .width(labelMetrics.axisWidth)
                .height(gridHeightDp),
    ) {
        // One row per hour. The last label can reach past the end of a grid that stops mid-hour, so
        // the labels are laid out unbounded instead of being squeezed into the grid height.
        Column(modifier = Modifier.wrapContentHeight(align = Alignment.Top, unbounded = true)) {
            hourLabels.forEach { hourLabel ->
                Box(modifier = Modifier.size(labelMetrics.axisWidth, rowHeightDp)) {
                    HourLabel(
                        label = hourLabel,
                        color = style.colors.timeLabelTextColor,
                        modifier = Modifier.padding(horizontal = TimeAxisDefaults.horizontalPadding),
                    )
                }
            }
        }

        // Current time as a filled pill in the gutter, centered on the indicator line
        if (showNowPill) {
            val nowPositionMinutes = ChronoUnit.MINUTES.between(gridStartTime, now)
            val nowPositionDp = (nowPositionMinutes / 60f * rowHeightDp.value).dp

            Box(
                modifier =
                    Modifier
                        .offset {
                            // Kept inside the axis, so the pill is not clipped by the edges of the scroll container.
                            val maxY = (gridHeightDp - labelMetrics.pillHeight).roundToPx().coerceAtLeast(0)
                            IntOffset(
                                x = 0,
                                y = (nowPositionDp - labelMetrics.pillHeight / 2).roundToPx().coerceIn(0, maxY),
                            )
                        }
                        .width(labelMetrics.axisWidth)
                        // Padded on the start side only, so the pill ends where the line begins.
                        .padding(start = TimeAxisDefaults.horizontalPadding / 2),
            ) {
                NowPill(
                    label = now.toAxisLabel(locale),
                    style = style,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

/** An hour of the axis: the clock time with the AM/PM marker stacked below it in 12-hour locales. */
@Composable
private fun HourLabel(
    label: AxisTimeLabel,
    color: Color,
    modifier: Modifier = Modifier,
) {
    StackedLabel(
        label = label,
        timeStyle = TimeAxisDefaults.timeTextStyle.copy(color = color),
        dayPeriodStyle = TimeAxisDefaults.dayPeriodTextStyle.copy(color = color),
        modifier = modifier,
    )
}

/**
 * The current time, filled in the indicator color so it stands out against the hour labels.
 *
 * Only the clock time is shown: which half of the day it is in is obvious from the hour labels
 * around the pill, so the AM/PM line would only make it taller.
 */
@Composable
private fun NowPill(
    label: AxisTimeLabel,
    style: WeekViewStyle,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .background(style.colors.nowIndicator, RoundedCornerShape(percent = 50))
                .padding(
                    horizontal = TimeAxisDefaults.pillHorizontalPadding,
                    vertical = TimeAxisDefaults.pillVerticalPadding,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label.time,
            style =
                TimeAxisDefaults.timeTextStyle.copy(
                    color = style.colors.nowIndicatorLabelText,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
            maxLines = 1,
            softWrap = false,
        )
    }
}

@Composable
private fun StackedLabel(
    label: AxisTimeLabel,
    timeStyle: TextStyle,
    dayPeriodStyle: TextStyle,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
) {
    Column(modifier = modifier, horizontalAlignment = horizontalAlignment) {
        Text(text = label.time, style = timeStyle, maxLines = 1, softWrap = false)
        if (label.dayPeriod != null) {
            Text(text = label.dayPeriod, style = dayPeriodStyle, maxLines = 1, softWrap = false)
        }
    }
}
