package de.tobiasschuerg.weekview.internal

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.tobiasschuerg.weekview.EventConfig
import de.tobiasschuerg.weekview.WeekViewConfig
import de.tobiasschuerg.weekview.content.EventContent
import de.tobiasschuerg.weekview.content.FilledEventContent
import de.tobiasschuerg.weekview.internal.components.AllDayEventsRow
import de.tobiasschuerg.weekview.internal.components.DayHeaderRow
import de.tobiasschuerg.weekview.internal.components.EventsPane
import de.tobiasschuerg.weekview.internal.components.GridCanvas
import de.tobiasschuerg.weekview.internal.components.MultiDayEventsRow
import de.tobiasschuerg.weekview.internal.components.NowIndicatorOverlay
import de.tobiasschuerg.weekview.internal.components.TimeAxisColumn
import de.tobiasschuerg.weekview.internal.components.TimeAxisDefaults
import de.tobiasschuerg.weekview.internal.components.TimeAxisLabelMetrics
import de.tobiasschuerg.weekview.internal.layout.WeekViewMetrics
import de.tobiasschuerg.weekview.internal.layout.gridHeightDp
import de.tobiasschuerg.weekview.internal.layout.rememberWeekViewMetrics
import de.tobiasschuerg.weekview.internal.layout.totalHours
import de.tobiasschuerg.weekview.model.Event
import de.tobiasschuerg.weekview.model.LocalDateRange
import de.tobiasschuerg.weekview.model.TimeSpan
import de.tobiasschuerg.weekview.style.WeekViewColors
import de.tobiasschuerg.weekview.style.WeekViewDefaults
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

/**
 * Composable that renders the background grid for the week view.
 * This includes the day columns, hour rows, day labels, time labels,
 * today highlight, optional now indicator, and events.
 */
@Composable
internal fun WeekBackgroundCompose(
    modifier: Modifier = Modifier,
    dateRange: LocalDateRange,
    timeRange: TimeSpan,
    events: List<Event.Single> = emptyList(),
    allDayEvents: List<Event.AllDay> = emptyList(),
    multiDayEvents: List<Event.MultiDay> = emptyList(),
    eventConfig: EventConfig = EventConfig(),
    weekViewConfig: WeekViewConfig,
    onEventClick: ((event: Event) -> Unit)? = null,
    onEventLongPress: ((event: Event) -> Unit)? = null,
    colors: WeekViewColors = WeekViewDefaults.colors(),
    scrollState: ScrollState = rememberScrollState(),
    onDayClick: ((date: LocalDate) -> Unit)? = null,
    eventContent: EventContent = { FilledEventContent(it) },
) {
    val days = remember(dateRange) { dateRange.toList() }
    var today by remember { mutableStateOf(LocalDate.now()) }
    var now by remember { mutableStateOf(LocalTime.now()) }

    LaunchedEffect(weekViewConfig.showCurrentTimeIndicator, weekViewConfig.highlightCurrentDay) {
        if (!weekViewConfig.showCurrentTimeIndicator && !weekViewConfig.highlightCurrentDay) return@LaunchedEffect

        while (true) {
            val currentDateTime = java.time.LocalDateTime.now()
            today = currentDateTime.toLocalDate()
            if (weekViewConfig.showCurrentTimeIndicator) {
                now = currentDateTime.toLocalTime()
            }
            val millisUntilNextMinute = 60_000L - (System.currentTimeMillis() % 60_000L)
            delay(millisUntilNextMinute)
        }
    }

    val locale = weekViewConfig.locale ?: LocalConfiguration.current.locales[0]

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        // The axis has to fit its widest label, which is wider in 12-hour locales than in 24-hour ones.
        // Measured once here and handed to the axis, which needs the same sizes.
        val labelMetrics = TimeAxisDefaults.rememberLabelMetrics(locale)
        val leftOffsetDp = labelMetrics.axisWidth
        val availableWidth = maxWidth - leftOffsetDp
        val dynamicColumnWidthDp = if (days.isNotEmpty()) (availableWidth / days.size) else availableWidth

        Column(modifier = Modifier.fillMaxSize()) {
            DayHeaderRow(
                days = days,
                today = today,
                leftOffsetDp = leftOffsetDp,
                topOffsetDp = WeekViewMetrics.TOP_OFFSET,
                columnWidth = dynamicColumnWidthDp,
                colors = colors,
                highlightCurrentDay = weekViewConfig.highlightCurrentDay,
                eventConfig = eventConfig,
                locale = locale,
                onDayClick = onDayClick,
            )

            if (multiDayEvents.isNotEmpty()) {
                MultiDayEventsRow(
                    days = days,
                    multiDayEvents = multiDayEvents,
                    leftOffsetDp = leftOffsetDp,
                    columnWidth = dynamicColumnWidthDp,
                    locale = locale,
                    onEventClick = onEventClick,
                    onEventLongPress = onEventLongPress,
                )
            }

            if (allDayEvents.isNotEmpty()) {
                AllDayEventsRow(
                    days = days,
                    allDayEvents = allDayEvents,
                    leftOffsetDp = leftOffsetDp,
                    columnWidth = dynamicColumnWidthDp,
                    onEventClick = onEventClick,
                    onEventLongPress = onEventLongPress,
                )
            }

            // The grid is measured against the space left below the header rows so that a short
            // schedule is padded with extra hour rows instead of leaving that space blank.
            BoxWithConstraints(modifier = Modifier.weight(1f)) {
                val metrics =
                    rememberWeekViewMetrics(
                        dateRange = dateRange,
                        timeRange = timeRange,
                        events = events,
                        scalingFactor = weekViewConfig.scalingFactor,
                        leftOffsetDp = leftOffsetDp,
                        minGridHeightDp = if (weekViewConfig.fillViewport) maxHeight else 0.dp,
                    )
                WeekGridRow(
                    metrics = metrics,
                    labelMetrics = labelMetrics,
                    events = events,
                    eventConfig = eventConfig,
                    weekViewConfig = weekViewConfig,
                    locale = locale,
                    columnWidth = dynamicColumnWidthDp,
                    today = today,
                    now = now,
                    scrollState = scrollState,
                    onEventClick = onEventClick,
                    onEventLongPress = onEventLongPress,
                    colors = colors,
                    eventContent = eventContent,
                )
            }
        }
    }
}

/** The scrollable part of the week view: time axis on the left, grid canvas and events on the right. */
@Composable
private fun WeekGridRow(
    metrics: WeekViewMetrics,
    labelMetrics: TimeAxisLabelMetrics,
    events: List<Event.Single>,
    eventConfig: EventConfig,
    weekViewConfig: WeekViewConfig,
    locale: Locale,
    columnWidth: Dp,
    today: LocalDate,
    now: LocalTime,
    scrollState: ScrollState,
    onEventClick: ((event: Event) -> Unit)?,
    onEventLongPress: ((event: Event) -> Unit)?,
    colors: WeekViewColors,
    eventContent: EventContent,
) {
    // Axis and grid scroll as one container, so the hour labels always stay level with their rows.
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
    ) {
        TimeAxisColumn(
            timeLabels = metrics.timeLabels,
            now = now,
            gridStartTime = metrics.gridStartTime,
            gridEndTime = metrics.effectiveEndTime,
            rowHeightDp = metrics.rowHeightDp,
            gridHeightDp = metrics.gridHeightDp,
            labelMetrics = labelMetrics,
            showNowIndicator = weekViewConfig.showCurrentTimeIndicator,
            locale = locale,
            colors = colors,
        )

        // Grid area (canvas + events). Clipped because the canvas draws hour lines up to the next full
        // hour, which would otherwise show below a grid that ends mid-hour.
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .height(metrics.gridHeightDp)
                    .clipToBounds(),
        ) {
            GridCanvas(
                modifier = Modifier.fillMaxSize(),
                columnCount = metrics.columnCount,
                rowHeightDp = metrics.rowHeightDp,
                totalHours = metrics.totalHours,
                days = metrics.days,
                today = today,
                highlightCurrentDay = weekViewConfig.highlightCurrentDay,
                colors = colors,
            )
            EventsPane(
                days = metrics.days,
                events = events,
                eventConfig = eventConfig,
                onEventClick = onEventClick,
                onEventLongPress = onEventLongPress,
                columnWidth = columnWidth,
                gridHeightDp = metrics.gridHeightDp,
                gridStartTime = metrics.gridStartTime,
                effectiveEndTime = metrics.effectiveEndTime,
                scalingFactor = weekViewConfig.scalingFactor,
                locale = locale,
                colors = colors,
                eventContent = eventContent,
            )
            if (weekViewConfig.showCurrentTimeIndicator) {
                // Drawn last so the line and its dot stay visible on top of the events they cross.
                NowIndicatorOverlay(
                    modifier = Modifier.fillMaxSize(),
                    columnCount = metrics.columnCount,
                    rowHeightDp = metrics.rowHeightDp,
                    days = metrics.days,
                    today = today,
                    now = now,
                    gridStartTime = metrics.gridStartTime,
                    effectiveEndTime = metrics.effectiveEndTime,
                    onlyToday = weekViewConfig.currentTimeLineOnlyToday,
                    colors = colors,
                )
            }
        }
    }
}
