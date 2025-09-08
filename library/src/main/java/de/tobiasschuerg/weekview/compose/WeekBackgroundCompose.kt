package de.tobiasschuerg.weekview.compose

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
import de.tobiasschuerg.weekview.compose.components.DayHeaderRow
import de.tobiasschuerg.weekview.compose.components.EventsPane
import de.tobiasschuerg.weekview.compose.components.GridCanvas
import de.tobiasschuerg.weekview.compose.components.TimeAxisColumn
import de.tobiasschuerg.weekview.compose.state.rememberWeekViewMetrics
import de.tobiasschuerg.weekview.compose.style.WeekViewStyle
import de.tobiasschuerg.weekview.compose.style.defaultWeekViewStyle
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.data.LocalDateRange
import de.tobiasschuerg.weekview.util.TimeSpan
import kotlinx.coroutines.delay
import java.time.LocalTime

/**
 * Composable that renders the background grid for the week view.
 * This includes the day columns, hour rows, day labels, time labels,
 * today highlight, optional now indicator, and events.
 */
@Composable
fun WeekBackgroundCompose(
    modifier: Modifier = Modifier,
    scalingFactor: Float = 1f,
    dateRange: LocalDateRange,
    timeRange: TimeSpan,
    showNowIndicator: Boolean = true,
    highlightCurrentDay: Boolean = true,
    events: List<Event.Single> = emptyList(),
    eventConfig: EventConfig = EventConfig(),
    onEventClick: ((eventId: Long) -> Unit)? = null,
    onEventLongPress: ((eventId: Long) -> Unit)? = null,
    style: WeekViewStyle = defaultWeekViewStyle(),
) {
    val metrics = rememberWeekViewMetrics(dateRange, timeRange, events, scalingFactor)
    val scrollState = rememberScrollState()
    var now by remember { mutableStateOf(LocalTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            now = LocalTime.now()
            delay(1000) // update every second
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val availableWidth = maxWidth - metrics.leftOffsetDp
        val dynamicColumnWidthDp = if (metrics.columnCount > 0) (availableWidth / metrics.columnCount) else availableWidth

        Column(modifier = Modifier.fillMaxSize()) {
            DayHeaderRow(
                days = metrics.days,
                leftOffsetDp = metrics.leftOffsetDp,
                topOffsetDp = metrics.topOffsetDp,
                columnWidth = dynamicColumnWidthDp,
                style = style,
                highlightCurrentDay = highlightCurrentDay,
            )

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
                    showNowIndicator = showNowIndicator,
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
                        showNowIndicator = showNowIndicator,
                        highlightCurrentDay = highlightCurrentDay,
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
                        scalingFactor = scalingFactor,
                        style = style,
                    )
                }
            }
        }
    }
}
