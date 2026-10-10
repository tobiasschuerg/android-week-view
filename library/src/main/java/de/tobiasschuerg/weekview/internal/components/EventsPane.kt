package de.tobiasschuerg.weekview.internal.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.times
import de.tobiasschuerg.weekview.EventConfig
import de.tobiasschuerg.weekview.content.EventContent
import de.tobiasschuerg.weekview.content.FilledEventContent
import de.tobiasschuerg.weekview.internal.EventsWithOverlapHandling
import de.tobiasschuerg.weekview.internal.layout.gridHeightDp
import de.tobiasschuerg.weekview.model.Event
import de.tobiasschuerg.weekview.style.WeekViewStyle
import de.tobiasschuerg.weekview.style.defaultWeekViewStyle
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

@Composable
internal fun EventsPane(
    days: List<LocalDate>,
    events: List<Event.Single>,
    eventConfig: EventConfig,
    onEventClick: ((event: Event) -> Unit)?,
    onEventLongPress: ((event: Event) -> Unit)?,
    columnWidth: Dp,
    gridHeightDp: Dp,
    gridStartTime: LocalTime,
    effectiveEndTime: LocalTime,
    scalingFactor: Float,
    locale: Locale,
    style: WeekViewStyle = defaultWeekViewStyle(),
    eventContent: EventContent = { FilledEventContent(it) },
) {
    val eventsByDate = remember(events) { events.groupBy { it.date } }

    days.forEachIndexed { dayIndex, date ->
        val eventsForDay = eventsByDate[date].orEmpty()
        if (eventsForDay.isNotEmpty()) {
            key(date) {
                Box(
                    modifier =
                        Modifier
                            .offset(x = dayIndex * columnWidth)
                            .size(columnWidth, gridHeightDp),
                ) {
                    EventsWithOverlapHandling(
                        events = eventsForDay,
                        scalingFactor = scalingFactor,
                        eventConfig = eventConfig,
                        startTime = gridStartTime,
                        endTime = effectiveEndTime,
                        columnWidth = columnWidth,
                        locale = locale,
                        onEventClick = onEventClick,
                        onEventLongPress = onEventLongPress,
                        eventContent = eventContent,
                    )
                }
            }
        }
    }
}
