package de.tobiasschuerg.weekview.internal.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.times
import de.tobiasschuerg.weekview.EventConfig
import de.tobiasschuerg.weekview.content.EventContent
import de.tobiasschuerg.weekview.internal.EventBox
import de.tobiasschuerg.weekview.internal.layout.EventOverlapCalculator
import de.tobiasschuerg.weekview.internal.layout.PeriodGridLayout
import de.tobiasschuerg.weekview.model.Event
import de.tobiasschuerg.weekview.model.TimeSpan
import java.time.LocalDate
import java.util.Locale

/**
 * The timed entries of a period grid, each filling the rows it snaps to (see [PeriodGridLayout.rowsOf]).
 * Entries sharing a row sit side by side, the same way overlapping entries do on the timeline.
 */
@Composable
internal fun PeriodEventsPane(
    layout: PeriodGridLayout,
    days: List<LocalDate>,
    events: List<Event.Single>,
    columnWidth: Dp,
    eventConfig: EventConfig,
    locale: Locale,
    onEventClick: ((event: Event) -> Unit)?,
    onEventLongPress: ((event: Event) -> Unit)?,
    eventContent: EventContent,
) {
    val rows = remember(events, layout.periods) { events.associate { it.id to layout.rowsOf(it.timeSpan) } }
    // Overlaps are worked out on the snapped rows, so two entries in the same row share it even when
    // their own times don't overlap.
    val overlaps =
        remember(rows) {
            EventOverlapCalculator.calculateEventLayouts(events.map { it.copy(timeSpan = layout.spanOf(rows.getValue(it.id))) })
        }

    events.forEach { event ->
        val dayIndex = days.indexOf(event.date)
        val eventRows = rows.getValue(event.id)
        val overlap = overlaps[event.id]
        if (dayIndex >= 0 && overlap != null) {
            key(event.id) {
                val top = layout.tops[eventRows.first]
                EventBox(
                    event = event,
                    x = dayIndex * columnWidth + columnWidth * overlap.offsetFraction,
                    y = top,
                    width = columnWidth * overlap.widthFraction,
                    height = layout.tops[eventRows.last] + layout.heights[eventRows.last] - top,
                    eventConfig = eventConfig,
                    locale = locale,
                    onEventClick = onEventClick,
                    onEventLongPress = onEventLongPress,
                    eventContent = eventContent,
                )
            }
        }
    }
}

/** From the start of the first of [rows] to the end of the last. */
private fun PeriodGridLayout.spanOf(rows: IntRange): TimeSpan =
    TimeSpan(periods[rows.first].timeSpan.start, periods[rows.last].timeSpan.endExclusive)
