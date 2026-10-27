package de.tobiasschuerg.weekview.internal.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.tobiasschuerg.weekview.R
import de.tobiasschuerg.weekview.internal.layout.AllDayLaneItem
import de.tobiasschuerg.weekview.internal.layout.AllDayLanes
import de.tobiasschuerg.weekview.model.Event
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

/**
 * Draws all-day and multi-day events above the grid, packed into shared lanes so a single-day
 * event sits next to a multi-day bar whenever they don't overlap.
 */
@Composable
internal fun AllDayEventsRow(
    days: List<LocalDate>,
    allDayEvents: List<Event.AllDay>,
    multiDayEvents: List<Event.MultiDay>,
    leftOffsetDp: Dp,
    columnWidth: Dp,
    locale: Locale,
    onEventClick: ((event: Event) -> Unit)? = null,
    onEventLongPress: ((event: Event) -> Unit)? = null,
) {
    if (days.isEmpty()) return

    val lanes =
        remember(days, allDayEvents, multiDayEvents) {
            AllDayLanes.pack(multiDayEvents + allDayEvents, days.first(), days.last())
        }
    if (lanes.isEmpty()) return
    val dateFormatter = remember(locale) { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale) }

    Column(modifier = Modifier.fillMaxWidth()) {
        lanes.forEachIndexed { laneIndex, lane ->
            key(laneIndex) {
                Box(modifier = Modifier.fillMaxWidth().height(24.dp)) {
                    lane.forEach { item ->
                        key(item.event.id) {
                            AllDayEventBox(item, leftOffsetDp, columnWidth, dateFormatter, onEventClick, onEventLongPress)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AllDayEventBox(
    item: AllDayLaneItem,
    leftOffsetDp: Dp,
    columnWidth: Dp,
    dateFormatter: DateTimeFormatter,
    onEventClick: ((event: Event) -> Unit)?,
    onEventLongPress: ((event: Event) -> Unit)?,
) {
    val event = item.event
    val testTag: String
    val description: String
    val textColor: Color
    val backgroundColor: Color
    when (event) {
        is Event.MultiDay -> {
            testTag = "MultiDayEventView_${event.id}"
            description =
                stringResource(
                    R.string.weekview_multi_day_event_description,
                    event.title,
                    dateFormatter.format(event.date),
                    dateFormatter.format(event.lastDate),
                )
            textColor = event.textColor
            backgroundColor = event.backgroundColor
        }
        is Event.AllDay -> {
            testTag = "AllDayEventView_${event.id}"
            description = stringResource(R.string.weekview_all_day_event_description, event.title)
            textColor = event.textColor
            backgroundColor = event.backgroundColor
        }
        is Event.Single -> return
    }

    Box(
        modifier =
            Modifier
                .testTag(testTag)
                .offset(x = leftOffsetDp + columnWidth * item.startIndex)
                .width(columnWidth * item.spanDays)
                .height(24.dp)
                .padding(horizontal = 1.dp, vertical = 1.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(backgroundColor)
                .combinedClickable(
                    enabled = onEventClick != null || onEventLongPress != null,
                    role = Role.Button,
                    onClick = { onEventClick?.invoke(event) },
                    onLongClick = onEventLongPress?.let { { it(event) } },
                )
                .semantics {
                    contentDescription = description
                }
                .padding(horizontal = 4.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = event.title,
            color = textColor,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
