package de.tobiasschuerg.weekview.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.util.EventFieldLayout
import de.tobiasschuerg.weekview.util.EventLineHeights
import de.tobiasschuerg.weekview.util.EventOverlapCalculator
import de.tobiasschuerg.weekview.util.EventPositionUtil
import de.tobiasschuerg.weekview.util.TimeLabelMode
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

    val location = event.subTitle?.takeIf { eventConfig.showSubtitle && it.isNotBlank() }
    val teacher = event.upperText?.takeIf { eventConfig.showUpperText && it.isNotBlank() }
    val lowerText = event.lowerText?.takeIf { eventConfig.showLowerText && it.isNotBlank() }

    // Which optional fields fit, and how the time labels are laid out, is decided from the
    // real (font-scaled) line heights; the title is always shown. See EventFieldLayout.
    val density = LocalDensity.current
    val lineHeights =
        with(density) {
            EventLineHeights(
                time = TIME_LINE_HEIGHT.toDp(),
                compactTime = COMPACT_TIME_LINE_HEIGHT.toDp(),
                title = TITLE_LINE_HEIGHT.toDp(),
                location = LOCATION_LINE_HEIGHT.toDp(),
                teacher = TEACHER_LINE_HEIGHT.toDp(),
            )
        }
    val innerHeight = eventHeight - eventConfig.eventSpacingDp.dp * 2
    val verticalPadding = EventFieldLayout.verticalPadding(innerHeight, lineHeights)
    val availableHeight = innerHeight - verticalPadding * 2
    val fields =
        EventFieldLayout.resolve(
            availableHeight = availableHeight,
            lines = lineHeights,
            hasStartTime = eventConfig.showTimeStart,
            hasEndTime = eventConfig.showTimeEnd,
            hasLocation = location != null,
            hasTeacher = teacher != null,
            hasLowerText = lowerText != null,
        )
    val cornerLabels = fields.timeLabelMode == TimeLabelMode.CORNERS
    // Shrink the title (font and line height together) to the band it was granted.
    val titleScale = (fields.titleHeight / lineHeights.title).coerceIn(0f, 1f)
    val titleFontSize = (TITLE_FONT_SIZE.value * titleScale).coerceAtLeast(MIN_TITLE_FONT_SIZE.value).sp
    val titleLineHeight = (TITLE_LINE_HEIGHT.value * titleFontSize.value / TITLE_FONT_SIZE.value).sp
    val fitsTitleLine = titleScale >= 1f

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
                .padding(horizontal = 4.dp, vertical = verticalPadding),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    // Stacked mode keeps the fields clear of the end time pinned to the bottom.
                    // Corner mode centres them instead: the resolver only grants fields that
                    // fit between the corner bands, and a title already at its minimum size
                    // may overlap the bands slightly rather than being clipped.
                    .padding(bottom = if (fields.showEndTime && !cornerLabels) lineHeights.time else 0.dp)
                    .testTag("EventViewInner_${event.id}"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = if (fitsTitleLine && !cornerLabels) Arrangement.Top else Arrangement.Center,
        ) {
            // Start time on its own line above the title (stacked mode).
            if (fields.showStartTime && !cornerLabels) {
                TimeLabel(
                    text = event.timeSpan.start.toLocalString(locale),
                    color = textColor,
                    textAlign = TextAlign.Start,
                    compact = false,
                    modifier = Modifier.fillMaxWidth().testTag("EventTime_${event.id}"),
                )
            }

            // Main title. Pre-shrunk to the height it was granted (see titleScale), shrunk
            // further by auto-size when it's too wide for the column, and allowed to wrap
            // onto a second line when there's room to spare.
            BasicText(
                text = displayTitle,
                style =
                    TextStyle(
                        color = textColor,
                        fontSize = titleFontSize,
                        lineHeight = titleLineHeight,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    ),
                autoSize = TextAutoSize.StepBased(minFontSize = MIN_TITLE_FONT_SIZE, maxFontSize = titleFontSize),
                maxLines = if (fields.twoLineTitle) 2 else 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth().testTag("EventTitle_${event.id}"),
            )

            if (location != null && fields.showLocation) {
                Text(
                    text = location,
                    color = textColor.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    lineHeight = LOCATION_LINE_HEIGHT,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("EventLocation_${event.id}"),
                )
            }

            if (teacher != null && fields.showTeacher) {
                Text(
                    text = teacher,
                    color = textColor.copy(alpha = 0.8f),
                    fontSize = 8.sp,
                    lineHeight = TEACHER_LINE_HEIGHT,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("EventTeacher_${event.id}"),
                )
            }

            if (lowerText != null && fields.showLowerText) {
                Text(
                    text = lowerText,
                    color = textColor.copy(alpha = 0.8f),
                    fontSize = 8.sp,
                    lineHeight = TEACHER_LINE_HEIGHT,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        // Start time in the top-left corner (corner mode); the column reserves its band.
        if (fields.showStartTime && cornerLabels) {
            TimeLabel(
                text = event.timeSpan.start.toLocalString(locale),
                color = textColor,
                textAlign = TextAlign.Start,
                compact = true,
                modifier =
                    Modifier
                        .align(Alignment.TopStart)
                        .testTag("EventTime_${event.id}"),
            )
        }

        // End time in the bottom-right corner in both modes; the column reserves its band.
        if (fields.showEndTime) {
            TimeLabel(
                text = event.timeSpan.endExclusive.toLocalString(locale),
                color = textColor,
                textAlign = TextAlign.End,
                compact = cornerLabels,
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .testTag("EventTimeEnd_${event.id}"),
            )
        }
    }
}

@Composable
private fun TimeLabel(
    text: String,
    color: Color,
    textAlign: TextAlign,
    compact: Boolean,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        color = color.copy(alpha = 0.7f),
        fontSize = if (compact) COMPACT_TIME_FONT_SIZE else TIME_FONT_SIZE,
        lineHeight = if (compact) COMPACT_TIME_LINE_HEIGHT else TIME_LINE_HEIGHT,
        textAlign = textAlign,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Clip,
        modifier = modifier,
    )
}

private val TITLE_FONT_SIZE = 12.sp
private val MIN_TITLE_FONT_SIZE = 7.sp
private val TIME_FONT_SIZE = 9.sp
private val TIME_LINE_HEIGHT = 11.sp
private val COMPACT_TIME_FONT_SIZE = 7.sp
private val COMPACT_TIME_LINE_HEIGHT = 9.sp
private val TITLE_LINE_HEIGHT = 14.sp
private val LOCATION_LINE_HEIGHT = 12.sp
private val TEACHER_LINE_HEIGHT = 10.sp
