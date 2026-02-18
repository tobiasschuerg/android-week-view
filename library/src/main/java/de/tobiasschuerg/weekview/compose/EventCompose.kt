package de.tobiasschuerg.weekview.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.util.EventOverlapCalculator
import de.tobiasschuerg.weekview.util.EventPositionUtil
import de.tobiasschuerg.weekview.util.TimeSpan
import de.tobiasschuerg.weekview.util.toLocalString
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

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
    onEventClick: ((event: Event) -> Unit)? = null,
    onEventLongPress: ((event: Event) -> Unit)? = null,
) {
    val (topOffset, eventHeight) =
        EventPositionUtil.calculateVerticalOffsets(
            event = event,
            startTime = startTime,
            scalingFactor = scalingFactor,
        )

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
                .clip(RoundedCornerShape(cornerRadius))
                .background(backgroundColor)
                .pointerInput(event.id) {
                    detectTapGestures(
                        onTap = { onEventClick?.invoke(event) },
                        onLongPress = { onEventLongPress?.invoke(event) },
                    )
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
            // Main title
            Text(
                text = displayTitle,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = if (eventConfig.showSubtitle || eventConfig.showTimeEnd) 1 else 2,
                overflow = TextOverflow.Ellipsis,
            )

            // Subtitle (if enabled and available)
            if (eventConfig.showSubtitle && event.subTitle?.isNotBlank() == true) {
                Text(
                    text = event.subTitle,
                    color = textColor.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Time information (if enabled)
            if (eventConfig.showTimeEnd) {
                val timeText = "${event.timeSpan.start.toLocalString()} - ${event.timeSpan.endExclusive.toLocalString()}"

                Text(
                    text = timeText,
                    color = textColor.copy(alpha = 0.7f),
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Upper text (if enabled and available)
            if (eventConfig.showUpperText && event.upperText?.isNotBlank() == true) {
                Text(
                    text = event.upperText,
                    color = textColor.copy(alpha = 0.8f),
                    fontSize = 8.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Lower text (if enabled and available)
            if (eventConfig.showLowerText && event.lowerText?.isNotBlank() == true) {
                Text(
                    text = event.lowerText,
                    color = textColor.copy(alpha = 0.8f),
                    fontSize = 8.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEventCompose() {
    Column(
        Modifier
            .height(400.dp)
            .width(400.dp),
    ) {
        Text("Preview")

        val event =
            Event.Single(
                id = 1L,
                date = LocalDate.now(),
                title = "Full Title",
                shortTitle = "Short Title",
                subTitle = "Subtitle",
                timeSpan = TimeSpan.of(LocalTime.of(8, 15), Duration.ofMinutes(45)),
                backgroundColor = "#90323D".toColorInt(),
                textColor = "#dddddd".toColorInt(),
                upperText = "Upper Text",
                lowerText = "Lower Text",
            )
        val eventConfig =
            EventConfig(
                showSubtitle = true,
                showTimeEnd = true,
                alwaysUseFullName = false,
                showTimeStart = true,
                showUpperText = true,
                showLowerText = true,
            )
        val eventLayout =
            EventOverlapCalculator.EventLayout(
                widthFraction = 1f,
                offsetFraction = 0f,
                overlapGroup = 0,
            )
        EventCompose(
            modifier =
                Modifier
                    .width(150.dp)
                    .height(50.dp),
            event = event,
            scalingFactor = 1f,
            eventConfig = eventConfig,
            startTime = LocalTime.of(8, 0),
            columnWidth = 120.dp,
            eventLayout = eventLayout,
        )
    }
}
