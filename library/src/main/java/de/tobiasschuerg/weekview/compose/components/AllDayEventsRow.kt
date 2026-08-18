package de.tobiasschuerg.weekview.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.tobiasschuerg.weekview.data.Event
import java.time.LocalDate

@Composable
internal fun AllDayEventsRow(
    days: List<LocalDate>,
    allDayEvents: List<Event.AllDay>,
    leftOffsetDp: Dp,
    columnWidth: Dp,
    onEventClick: ((event: Event) -> Unit)? = null,
    onEventLongPress: ((event: Event) -> Unit)? = null,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.width(leftOffsetDp))
        days.forEach { date ->
            val eventsForDay = allDayEvents.filter { it.date == date }
            key(date) {
                Column(
                    modifier = Modifier.width(columnWidth).padding(horizontal = 1.dp),
                ) {
                    eventsForDay.forEach { event ->
                        key(event.id) {
                            Box(
                                modifier =
                                    Modifier
                                        .testTag("AllDayEventView_${event.id}")
                                        .fillMaxWidth()
                                        .height(24.dp)
                                        .padding(vertical = 1.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(event.backgroundColor))
                                        .combinedClickable(
                                            enabled = onEventClick != null || onEventLongPress != null,
                                            role = Role.Button,
                                            onClick = { onEventClick?.invoke(event) },
                                            onLongClick = onEventLongPress?.let { { it(event) } },
                                        )
                                        .semantics {
                                            contentDescription = "${event.title}, all day"
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
    }
}
