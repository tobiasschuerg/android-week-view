package de.tobiasschuerg.weekview.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.times
import de.tobiasschuerg.weekview.compose.EventsWithOverlapHandling
import de.tobiasschuerg.weekview.compose.style.WeekViewStyle
import de.tobiasschuerg.weekview.compose.style.defaultWeekViewStyle
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.EventConfig
import java.time.LocalDate
import java.time.LocalTime

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
    style: WeekViewStyle = defaultWeekViewStyle(),
) {
    days.forEachIndexed { dayIndex, date ->
        val eventsForDay = events.filter { it.date == date }
        if (eventsForDay.isNotEmpty()) {
            Box(
                modifier =
                    Modifier
                        .offset(x = dayIndex * columnWidth)
                        .size(columnWidth, gridHeightDp),
                // Height must be total grid height for proper event positioning
            ) {
                EventsWithOverlapHandling(
                    events = eventsForDay,
                    scalingFactor = scalingFactor,
                    eventConfig = eventConfig,
                    startTime = gridStartTime,
                    endTime = effectiveEndTime,
                    columnWidth = columnWidth,
                    onEventClick = onEventClick,
                    onEventLongPress = onEventLongPress,
                    // style = style // Pass style to EventsWithOverlapHandling if it needs it in the future
                )
            }
        }
    }
}
