package de.tobiasschuerg.weekview.compose

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
import de.tobiasschuerg.weekview.compose.components.AllDayEventsRow
import de.tobiasschuerg.weekview.compose.components.DayHeaderRow
import de.tobiasschuerg.weekview.compose.components.EventsPane
import de.tobiasschuerg.weekview.compose.components.GridCanvas
import de.tobiasschuerg.weekview.compose.components.MultiDayEventsRow
import de.tobiasschuerg.weekview.compose.components.TimeAxisColumn
import de.tobiasschuerg.weekview.compose.state.rememberWeekViewMetrics
import de.tobiasschuerg.weekview.compose.style.WeekViewStyle
import de.tobiasschuerg.weekview.compose.style.defaultWeekViewStyle
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.data.LocalDateRange
import de.tobiasschuerg.weekview.data.WeekViewConfig
import de.tobiasschuerg.weekview.util.TimeSpan
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime

/**
 * Composable that renders the background grid for the week view.
 * This includes the day columns, hour rows, day labels, time labels,
 * today highlight, optional now indicator, and events.
 */
@Composable
fun WeekBackgroundCompose(
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
    style: WeekViewStyle = defaultWeekViewStyle(),
    scrollState: ScrollState = rememberScrollState(),
    onDayClick: ((date: LocalDate) -> Unit)? = null,
) {
    val metrics =
        rememberWeekViewMetrics(
            dateRange = dateRange,
            timeRange = timeRange,
            events = events,
            scalingFactor = weekViewConfig.scalingFactor,
        )
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

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val availableWidth = maxWidth - metrics.leftOffsetDp
        val dynamicColumnWidthDp = if (metrics.columnCount > 0) (availableWidth / metrics.columnCount) else availableWidth

        Column(modifier = Modifier.fillMaxSize()) {
            DayHeaderRow(
                days = metrics.days,
                today = today,
                leftOffsetDp = metrics.leftOffsetDp,
                topOffsetDp = metrics.topOffsetDp,
                columnWidth = dynamicColumnWidthDp,
                style = style,
                highlightCurrentDay = weekViewConfig.highlightCurrentDay,
                eventConfig = eventConfig,
                locale = weekViewConfig.locale,
                onDayClick = onDayClick,
            )

            if (multiDayEvents.isNotEmpty()) {
                MultiDayEventsRow(
                    days = metrics.days,
                    multiDayEvents = multiDayEvents,
                    leftOffsetDp = metrics.leftOffsetDp,
                    columnWidth = dynamicColumnWidthDp,
                    onEventClick = onEventClick,
                    onEventLongPress = onEventLongPress,
                )
            }

            if (allDayEvents.isNotEmpty()) {
                AllDayEventsRow(
                    days = metrics.days,
                    allDayEvents = allDayEvents,
                    leftOffsetDp = metrics.leftOffsetDp,
                    columnWidth = dynamicColumnWidthDp,
                    onEventClick = onEventClick,
                    onEventLongPress = onEventLongPress,
                )
            }

            Row(modifier = Modifier.weight(1f)) {
                TimeAxisColumn(
                    timeLabels = metrics.timeLabels,
                    now = now,
                    gridStartTime = metrics.gridStartTime,
                    gridEndTime = metrics.effectiveEndTime,
                    rowHeightDp = metrics.rowHeightDp,
                    gridHeightDp = metrics.gridHeightDp,
                    leftOffsetDp = metrics.leftOffsetDp,
                    scrollState = scrollState,
                    showNowIndicator = weekViewConfig.showCurrentTimeIndicator,
                    style = style,
                )

                // Scrollable Grid Area (Canvas + Events)
                Box(
                    modifier =
                        Modifier
                            .verticalScroll(scrollState)
                            .weight(1f)
                            .height(metrics.gridHeightDp),
                ) {
                    GridCanvas(
                        modifier = Modifier.fillMaxSize(),
                        columnCount = metrics.columnCount,
                        rowHeightDp = metrics.rowHeightDp,
                        totalHours = metrics.totalHours,
                        days = metrics.days,
                        today = today,
                        showNowIndicator = weekViewConfig.showCurrentTimeIndicator,
                        highlightCurrentDay = weekViewConfig.highlightCurrentDay,
                        currentTimeLineOnlyToday = weekViewConfig.currentTimeLineOnlyToday,
                        now = now,
                        gridStartTime = metrics.gridStartTime,
                        effectiveEndTime = metrics.effectiveEndTime,
                        style = style,
                    )
                    EventsPane(
                        days = metrics.days,
                        events = events,
                        eventConfig = eventConfig,
                        onEventClick = onEventClick,
                        onEventLongPress = onEventLongPress,
                        columnWidth = dynamicColumnWidthDp,
                        gridHeightDp = metrics.gridHeightDp,
                        gridStartTime = metrics.gridStartTime,
                        effectiveEndTime = metrics.effectiveEndTime,
                        scalingFactor = weekViewConfig.scalingFactor,
                        locale = weekViewConfig.locale,
                        style = style,
                    )
                }
            }
        }
    }
}
