package de.tobiasschuerg.weekview.content

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.tobiasschuerg.weekview.EventConfig
import de.tobiasschuerg.weekview.internal.layout.EventFieldLayout
import de.tobiasschuerg.weekview.internal.layout.EventLineHeights
import de.tobiasschuerg.weekview.internal.layout.TimeLabelMode
import de.tobiasschuerg.weekview.internal.time.toLocalString
import de.tobiasschuerg.weekview.model.Event

/**
 * The text of a timed entry, shared by all built-in styles: title, time labels, location, teacher
 * and lower text, showing as many fields as fit [height]. Draws no background; the style does.
 *
 * @param height the height the fields may use, i.e. the entry height minus any border the style draws.
 */
@Composable
internal fun EventFields(
    scope: EventContentScope,
    textColor: Color,
    modifier: Modifier = Modifier,
    height: Dp = scope.height,
) {
    val event = scope.event
    val eventConfig = scope.eventConfig
    val locale = scope.locale

    val location = event.subTitle?.takeIf { eventConfig.showSubtitle && it.isNotBlank() }
    val teacher = event.upperText?.takeIf { eventConfig.showUpperText && it.isNotBlank() }
    val lowerText = event.lowerText?.takeIf { eventConfig.showLowerText && it.isNotBlank() }

    // Which optional fields fit, and how the time labels are laid out, is decided from the
    // real (font-scaled) line heights; the title is always shown. See EventFieldLayout.
    val lineHeights = LocalDensity.current.eventLineHeights()
    val verticalPadding = EventFieldLayout.verticalPadding(height, lineHeights)
    val availableHeight = height - verticalPadding * 2
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

    val displayTitle = displayTitle(event, eventConfig)

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp, vertical = verticalPadding),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    // Keep the stacked fields clear of the time labels pinned to the corners;
                    // the resolver guarantees at least a minimum-size title line stays free.
                    .padding(
                        top = if (cornerLabels && fields.showStartTime) lineHeights.compactTime else 0.dp,
                        bottom =
                            when {
                                !fields.showEndTime -> 0.dp
                                cornerLabels -> lineHeights.compactTime
                                else -> lineHeights.time
                            },
                    ).testTag("EventViewInner_${event.id}"),
            horizontalAlignment = Alignment.CenterHorizontally,
            // Corner mode and entries too low for a full title line centre the (shrunk) title.
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

/** Line heights of the entry's text fields in dp, i.e. scaled by the current font scale. */
private fun Density.eventLineHeights(): EventLineHeights =
    EventLineHeights(
        time = TIME_LINE_HEIGHT.toDp(),
        compactTime = COMPACT_TIME_LINE_HEIGHT.toDp(),
        title = TITLE_LINE_HEIGHT.toDp(),
        minTitle = (TITLE_LINE_HEIGHT * (MIN_TITLE_FONT_SIZE.value / TITLE_FONT_SIZE.value)).toDp(),
        location = LOCATION_LINE_HEIGHT.toDp(),
        teacher = TEACHER_LINE_HEIGHT.toDp(),
    )

/** Full title always when configured so, otherwise the short title in portrait and the full title in landscape. */
@Composable
private fun displayTitle(
    event: Event.Single,
    eventConfig: EventConfig,
): String {
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    return when {
        eventConfig.alwaysUseFullName || !isPortrait -> event.title
        else -> event.shortTitle.ifBlank { event.title }
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
    val fontSize = if (compact) COMPACT_TIME_FONT_SIZE else TIME_FONT_SIZE
    // Shrinks further in narrow overlap columns; a label that still doesn't fit at the
    // minimum size is hidden rather than clipped, since a truncated time reads as a wrong one.
    var overflows by remember { mutableStateOf(false) }
    BasicText(
        text = text,
        style =
            TextStyle(
                color = color.copy(alpha = 0.7f),
                fontSize = fontSize,
                lineHeight = if (compact) COMPACT_TIME_LINE_HEIGHT else TIME_LINE_HEIGHT,
                textAlign = textAlign,
            ),
        autoSize = TextAutoSize.StepBased(minFontSize = MIN_TIME_FONT_SIZE, maxFontSize = fontSize),
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Clip,
        onTextLayout = { overflows = it.hasVisualOverflow },
        modifier = modifier.alpha(if (overflows) 0f else 1f),
    )
}

private val TITLE_FONT_SIZE = 12.sp
private val MIN_TITLE_FONT_SIZE = 7.sp
private val TIME_FONT_SIZE = 9.sp
private val MIN_TIME_FONT_SIZE = 5.sp
private val TIME_LINE_HEIGHT = 11.sp
private val COMPACT_TIME_FONT_SIZE = 7.sp
private val COMPACT_TIME_LINE_HEIGHT = 9.sp
private val TITLE_LINE_HEIGHT = 14.sp
private val LOCATION_LINE_HEIGHT = 12.sp
private val TEACHER_LINE_HEIGHT = 10.sp

/** Corner radius shared by the built-in entry styles. */
internal val EventCornerShape = RoundedCornerShape(4.dp)
