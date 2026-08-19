package de.tobiasschuerg.weekview.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.util.EventOverlapCalculator
import de.tobiasschuerg.weekview.util.EventPositionUtil
import de.tobiasschuerg.weekview.util.toLocalString
import java.time.LocalTime
import java.util.Locale

/**
 * Composable that renders individual events on the week view grid.
 * Handles positioning, sizing, and styling of single events based on their time spans.
 */
@Composable
fun EventCompose(
    modifier: Modifier = Modifier,
    event: Event.Single,
    scalingFactor: Float,
    eventConfig: EventConfig,
    startTime: LocalTime,
    columnWidth: Dp,
    eventLayout: EventOverlapCalculator.EventLayout,
    locale: Locale = Locale.getDefault(),
    onEventClick: ((event: Event) -> Unit)? = null,
    onEventLongPress: ((event: Event) -> Unit)? = null,
) {
    val (topOffset, eventHeight) =
        EventPositionUtil.calculateVerticalOffsets(
            event = event,
            startTime = startTime,
            scalingFactor = scalingFactor,
        )

    val hasTimeLabel = eventConfig.showTimeStart || eventConfig.showTimeEnd
    val location = event.subTitle?.takeIf { eventConfig.showSubtitle && it.isNotBlank() }
    val teacher = event.upperText?.takeIf { eventConfig.showUpperText && it.isNotBlank() }
    val lowerText = event.lowerText?.takeIf { eventConfig.showLowerText && it.isNotBlank() }

    // Priority-based visibility for short entries: the name (title) is always shown;
    // time, location, and teacher are only added once there's enough room, in that
    // priority order (name=1 > time=2 > location=3 > teacher=4), so very short entries
    // surface the most useful info first instead of whatever happens to render first.
    val showTimeField = hasTimeLabel && EventPositionUtil.allowsTimeField(eventHeight)
    val showLocationField = location != null && EventPositionUtil.allowsLocationField(eventHeight)
    val showTeacherField = teacher != null && EventPositionUtil.allowsTeacherField(eventHeight)
    val showLowerTextField = lowerText != null && EventPositionUtil.allowsTeacherField(eventHeight)

    val allowTwoLineTitle = EventPositionUtil.allowsTwoLineTitle(eventHeight)
    // Splitting into two separate labels only makes sense when there's something to split
    // (both start and end enabled) and only when there's enough room to not feel cramped;
    // otherwise fall back to one combined "start - end" line.
    val splitTimeLabels =
        showTimeField &&
            eventConfig.showTimeStart &&
            eventConfig.showTimeEnd &&
            EventPositionUtil.allowsSplitTimeLabels(eventHeight)

    // Apply overlap layout calculations
    val eventWidth = columnWidth * eventLayout.widthFraction
    val horizontalOffset = columnWidth * eventLayout.offsetFraction

    // Event styling
    val backgroundColor = Color(event.backgroundColor)
    val textColor = Color(event.textColor)
    val cornerRadius = 4.dp

    // Determine which title to show based on config and orientation
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT
    val displayTitle =
        if (eventConfig.alwaysUseFullName) {
            event.title
        } else {
            if (isPortrait) {
                event.shortTitle.ifBlank { event.title }
            } else {
                event.title
            }
        }

    Box(
        modifier =
            modifier
                .testTag("EventView_${event.id}")
                .offset(x = horizontalOffset, y = topOffset)
                .size(width = eventWidth, height = eventHeight)
                .let { if (eventConfig.eventSpacingDp > 0) it.padding(eventConfig.eventSpacingDp.dp) else it }
                .clip(RoundedCornerShape(cornerRadius))
                .background(backgroundColor)
                .combinedClickable(
                    enabled = onEventClick != null || onEventLongPress != null,
                    role = Role.Button,
                    onClick = { onEventClick?.invoke(event) },
                    onLongClick = onEventLongPress?.let { { it(event) } },
                )
                .semantics {
                    contentDescription =
                        "${event.title}, ${event.timeSpan.start.toLocalString(locale)} - " +
                        event.timeSpan.endExclusive.toLocalString(locale)
                }
                .padding(start = 4.dp, top = 4.dp, end = 4.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .testTag("EventViewInner_${event.id}"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            // Start/end time. Shown first so the entry reads time -> name -> location
            // -> teacher, top to bottom. When there's enough room, start and end are
            // split into their own labels (end pinned to the bottom, below); otherwise
            // one combined "start - end" line here carries both. Dropped entirely on
            // very short entries so the name below isn't crowded out (see showTimeField).
            if (showTimeField) {
                val timeText =
                    if (splitTimeLabels) {
                        event.timeSpan.start.toLocalString(locale)
                    } else {
                        buildString {
                            if (eventConfig.showTimeStart) append(event.timeSpan.start.toLocalString(locale))
                            if (eventConfig.showTimeStart && eventConfig.showTimeEnd) append(" - ")
                            if (eventConfig.showTimeEnd) append(event.timeSpan.endExclusive.toLocalString(locale))
                        }
                    }

                Text(
                    text = timeText,
                    color = textColor.copy(alpha = 0.7f),
                    fontSize = 9.sp,
                    lineHeight = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("EventTime_${event.id}"),
                )
            }

            // Main title. Allowed to wrap onto a second line when the entry is tall
            // enough to spare the room, otherwise kept to one line so it doesn't
            // crowd out the other fields below.
            Text(
                text = displayTitle,
                color = textColor,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = if (allowTwoLineTitle || !(showLocationField || showTimeField)) 2 else 1,
                overflow = TextOverflow.Ellipsis,
            )

            // Location (if enabled, available, and there's enough room - priority 3)
            if (location != null && showLocationField) {
                Text(
                    text = location,
                    color = textColor.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Teacher (if enabled, available, and there's enough room - priority 4, lowest)
            if (teacher != null && showTeacherField) {
                Text(
                    text = teacher,
                    color = textColor.copy(alpha = 0.8f),
                    fontSize = 8.sp,
                    lineHeight = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Lower text (if enabled, available, and there's enough room - same tier as teacher)
            if (lowerText != null && showLowerTextField) {
                Text(
                    text = lowerText,
                    color = textColor.copy(alpha = 0.8f),
                    fontSize = 8.sp,
                    lineHeight = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        // End time, pinned to the bottom of the entry. Only rendered when there's
        // enough room to split start/end into separate labels (see splitTimeLabels above).
        if (splitTimeLabels) {
            Text(
                text = event.timeSpan.endExclusive.toLocalString(locale),
                color = textColor.copy(alpha = 0.7f),
                fontSize = 9.sp,
                lineHeight = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .testTag("EventTimeEnd_${event.id}"),
            )
        }
    }
}
