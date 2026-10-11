package de.tobiasschuerg.weekview.internal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.tobiasschuerg.weekview.EventConfig
import de.tobiasschuerg.weekview.WeekLayout
import de.tobiasschuerg.weekview.WeekViewConfig
import de.tobiasschuerg.weekview.WeekViewState
import de.tobiasschuerg.weekview.content.EventContent
import de.tobiasschuerg.weekview.internal.components.EmptyPeriodCells
import de.tobiasschuerg.weekview.internal.components.NowIndicatorLine
import de.tobiasschuerg.weekview.internal.components.PeriodAxisColumn
import de.tobiasschuerg.weekview.internal.components.PeriodBreakRows
import de.tobiasschuerg.weekview.internal.components.PeriodEventsPane
import de.tobiasschuerg.weekview.internal.components.PeriodTodayHighlight
import de.tobiasschuerg.weekview.internal.layout.PeriodGridGeometry
import de.tobiasschuerg.weekview.internal.layout.PeriodGridLayout
import de.tobiasschuerg.weekview.internal.time.isStrictlyBetween
import de.tobiasschuerg.weekview.model.Event
import de.tobiasschuerg.weekview.model.Period
import de.tobiasschuerg.weekview.style.WeekViewColors
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

/**
 * The scrollable part of the week view in [WeekLayout.Periods]: period labels on the left, the
 * entries on the right, with a dashed placeholder in every empty lesson cell instead of grid lines. Lessons grow to fill [minHeight] and zoom with the scaling factor.
 */
@Composable
internal fun PeriodGridRow(
    periods: WeekLayout.Periods,
    days: List<LocalDate>,
    events: List<Event.Single>,
    minHeight: Dp,
    axisWidth: Dp,
    columnWidth: Dp,
    eventConfig: EventConfig,
    weekViewConfig: WeekViewConfig,
    locale: Locale,
    today: LocalDate,
    now: LocalTime,
    state: WeekViewState,
    onEventClick: ((event: Event) -> Unit)?,
    onEventLongPress: ((event: Event) -> Unit)?,
    onEmptyPeriodClick: ((date: LocalDate, period: Period) -> Unit)?,
    colors: WeekViewColors,
    eventContent: EventContent,
) {
    val layout =
        remember(periods, weekViewConfig.scalingFactor, minHeight) {
            PeriodGridLayout.of(periods.periods, periods.periodHeight * weekViewConfig.scalingFactor, periods.breakHeight, minHeight)
        }

    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .onPlaced { state.viewportCoordinates = it }
                .reportGridGeometry(state) { PeriodGridGeometry(layout, density) }
                .verticalScroll(state.scrollState),
    ) {
        PeriodAxisColumn(layout = layout, width = axisWidth, showTimes = periods.showTimes, locale = locale, colors = colors)
        Box(modifier = Modifier.weight(1f).height(layout.height)) {
            PeriodBreakRows(layout = layout, colors = colors)
            if (weekViewConfig.highlightCurrentDay) {
                PeriodTodayHighlight(days = days, today = today, colors = colors, modifier = Modifier.fillMaxSize())
            }
            EmptyPeriodCells(
                layout = layout,
                days = days,
                events = events,
                columnWidth = columnWidth,
                spacing = eventConfig.eventSpacingDp.coerceAtLeast(0).dp,
                color = colors.gridLineColor,
                locale = locale,
                onEmptyPeriodClick = onEmptyPeriodClick,
            )
            PeriodEventsPane(layout, days, events, columnWidth, eventConfig, locale, onEventClick, onEventLongPress, eventContent)
            val first = layout.periods.first().timeSpan.start
            val last = layout.periods.last().timeSpan.endExclusive
            if (weekViewConfig.showCurrentTimeIndicator && now.isStrictlyBetween(first, last)) {
                // Drawn last so the line stays visible on top of the events it crosses.
                NowIndicatorLine(
                    modifier = Modifier.fillMaxSize(),
                    columnCount = days.size,
                    days = days,
                    today = today,
                    lineY = layout.yOf(now.toSecondOfDay() / SECONDS_PER_MINUTE),
                    onlyToday = weekViewConfig.currentTimeLineOnlyToday,
                    colors = colors,
                )
            }
        }
    }
}

private const val SECONDS_PER_MINUTE = 60f
