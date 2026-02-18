package de.tobiasschuerg.weekview.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.tobiasschuerg.weekview.data.Event
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
internal fun MultiDayEventsRow(
    days: List<LocalDate>,
    multiDayEvents: List<Event.MultiDay>,
    leftOffsetDp: Dp,
    columnWidth: Dp,
    onEventClick: ((event: Event) -> Unit)? = null,
    onEventLongPress: ((event: Event) -> Unit)? = null,
) {
    if (days.isEmpty() || multiDayEvents.isEmpty()) return

    val firstDay = days.first()
    val lastDay = days.last()

    // Pack events into rows where events don't horizontally overlap
    val rows = packEventsIntoRows(multiDayEvents, firstDay, lastDay)

    Column(modifier = Modifier.fillMaxWidth()) {
        rows.forEach { row ->
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(24.dp),
            ) {
                row.forEach { event ->
                    val clippedStart = maxOf(event.date, firstDay)
                    val clippedEnd = minOf(event.lastDate, lastDay)
                    val startIndex = ChronoUnit.DAYS.between(firstDay, clippedStart).toInt()
                    val spanDays = ChronoUnit.DAYS.between(clippedStart, clippedEnd).toInt() + 1

                    Box(
                        modifier =
                            Modifier
                                .offset(x = leftOffsetDp + columnWidth * startIndex)
                                .width(columnWidth * spanDays)
                                .height(24.dp)
                                .padding(horizontal = 1.dp, vertical = 1.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(event.backgroundColor))
                                .pointerInput(event.id) {
                                    detectTapGestures(
                                        onTap = { onEventClick?.invoke(event) },
                                        onLongPress = { onEventLongPress?.invoke(event) },
                                    )
                                }
                                .padding(horizontal = 4.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Text(
                            text = event.title,
                            color = Color(event.textColor),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Packs multi-day events into rows where no two events in the same row overlap horizontally.
 * Events are sorted by start date, then by span length (longest first) for stable packing.
 */
private fun packEventsIntoRows(
    events: List<Event.MultiDay>,
    firstDay: LocalDate,
    lastDay: LocalDate,
): List<List<Event.MultiDay>> {
    val sorted =
        events.sortedWith(
            compareBy<Event.MultiDay> { it.date }
                .thenByDescending { ChronoUnit.DAYS.between(it.date, it.lastDate) },
        )

    val rows = mutableListOf<MutableList<Event.MultiDay>>()

    for (event in sorted) {
        val clippedStart = maxOf(event.date, firstDay)
        val clippedEnd = minOf(event.lastDate, lastDay)

        val placed =
            rows.firstOrNull { row ->
                row.all { existing ->
                    val existingStart = maxOf(existing.date, firstDay)
                    val existingEnd = minOf(existing.lastDate, lastDay)
                    clippedEnd.isBefore(existingStart) || clippedStart.isAfter(existingEnd)
                }
            }

        if (placed != null) {
            placed.add(event)
        } else {
            rows.add(mutableListOf(event))
        }
    }

    return rows
}
