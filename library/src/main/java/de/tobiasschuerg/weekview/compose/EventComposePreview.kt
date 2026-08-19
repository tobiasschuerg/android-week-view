package de.tobiasschuerg.weekview.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.util.EventOverlapCalculator
import de.tobiasschuerg.weekview.util.TimeSpan
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

/**
 * Previews for [EventCompose] covering the start/end time label combinations, the
 * height-aware split of start/end into separate labels, and the height-aware
 * two-line title wrapping.
 */

private val fullEventLayout =
    EventOverlapCalculator.EventLayout(
        widthFraction = 1f,
        offsetFraction = 0f,
        overlapGroup = 0,
    )

private fun sampleEvent(
    id: Long,
    title: String = "Linear Algebra",
    shortTitle: String = "LinAlg",
    subTitle: String? = "Room A101",
    startTime: LocalTime = LocalTime.of(8, 15),
    duration: Duration = Duration.ofMinutes(90),
    upperText: String? = "Prof. Schmidt",
    lowerText: String? = null,
): Event.Single =
    Event.Single(
        id = id,
        date = LocalDate.now(),
        title = title,
        shortTitle = shortTitle,
        subTitle = subTitle,
        timeSpan = TimeSpan.of(startTime, duration),
        backgroundColor = "#90323D".toColorInt(),
        textColor = "#dddddd".toColorInt(),
        upperText = upperText,
        lowerText = lowerText,
    )

@Composable
private fun PreviewRow(
    label: String,
    event: Event.Single,
    eventConfig: EventConfig,
) {
    Column {
        Text(label)
        // eventHeight is derived from the event's duration * scalingFactor (1f here),
        // so varying `duration` on the sample event is what actually controls entry height below.
        EventCompose(
            event = event,
            scalingFactor = 1f,
            eventConfig = eventConfig,
            startTime = event.timeSpan.start,
            columnWidth = 120.dp,
            eventLayout = fullEventLayout,
        )
    }
}

/** Combined "start - end" label: entry too short (60 min < 90.dp threshold) to split. */
@Preview(name = "Both times combined (60 min)", showBackground = true)
@Composable
private fun PreviewEventComposeBothTimesCombined() {
    PreviewRow(
        label = "Both times, 60 min (combined)",
        event = sampleEvent(id = 1L, duration = Duration.ofMinutes(60)),
        eventConfig = EventConfig(showTimeStart = true, showTimeEnd = true),
    )
}

/** Split labels: entry tall enough (120 min >= 90.dp threshold) for start-top / end-bottom. */
@Preview(name = "Both times split (120 min)", showBackground = true)
@Composable
private fun PreviewEventComposeBothTimesSplit() {
    PreviewRow(
        label = "Both times, 120 min (split: start top, end bottom)",
        event = sampleEvent(id = 10L, duration = Duration.ofMinutes(120)),
        eventConfig = EventConfig(showTimeStart = true, showTimeEnd = true),
    )
}

/** Only the start time shown. Never splits - nothing to split when only one flag is enabled. */
@Preview(name = "Start time only", showBackground = true)
@Composable
private fun PreviewEventComposeStartTimeOnly() {
    PreviewRow(
        label = "Start time only, 120 min",
        event = sampleEvent(id = 2L, duration = Duration.ofMinutes(120)),
        eventConfig = EventConfig(showTimeStart = true, showTimeEnd = false),
    )
}

/** Only the end time shown. Never splits - nothing to split when only one flag is enabled. */
@Preview(name = "End time only", showBackground = true)
@Composable
private fun PreviewEventComposeEndTimeOnly() {
    PreviewRow(
        label = "End time only, 120 min",
        event = sampleEvent(id = 3L, duration = Duration.ofMinutes(120)),
        eventConfig = EventConfig(showTimeStart = false, showTimeEnd = true),
    )
}

/** Neither time shown. */
@Preview(name = "No time label", showBackground = true)
@Composable
private fun PreviewEventComposeNoTimes() {
    PreviewRow(
        label = "No time label, 90 min",
        event = sampleEvent(id = 4L),
        eventConfig = EventConfig(showTimeStart = false, showTimeEnd = false),
    )
}

/** Short lesson (25 min): combined time label still renders on the one available line. */
@Preview(name = "Both times - short lesson (25 min)", showBackground = true)
@Composable
private fun PreviewEventComposeShortLesson() {
    PreviewRow(
        label = "Both times, 25 min",
        event = sampleEvent(id = 5L, duration = Duration.ofMinutes(25), subTitle = null, upperText = null),
        eventConfig = EventConfig(showTimeStart = true, showTimeEnd = true),
    )
}

/**
 * Long title on a tall entry: enough room for the title to wrap to a second line
 * instead of being ellipsized.
 */
@Preview(name = "Long title - tall entry (2-line title)", showBackground = true)
@Composable
private fun PreviewEventComposeLongTitleTallEntry() {
    PreviewRow(
        label = "Long title, 120 min (2-line title + split time)",
        event =
            sampleEvent(
                id = 6L,
                title = "Introduction to Machine Learning and Neural Networks",
                shortTitle = "Intro ML & Neural Networks",
                duration = Duration.ofMinutes(120),
            ),
        eventConfig = EventConfig(showTimeStart = true, showTimeEnd = true, alwaysUseFullName = true),
    )
}

/**
 * Same long title on a short entry: not enough room for a second line, so the
 * title is ellipsized on one line instead.
 */
@Preview(name = "Long title - short entry (1-line, ellipsized)", showBackground = true)
@Composable
private fun PreviewEventComposeLongTitleShortEntry() {
    PreviewRow(
        label = "Long title, 30 min (1-line, ellipsized)",
        event =
            sampleEvent(
                id = 7L,
                title = "Introduction to Machine Learning and Neural Networks",
                shortTitle = "Intro ML & Neural Networks",
                duration = Duration.ofMinutes(30),
            ),
        eventConfig = EventConfig(showTimeStart = true, showTimeEnd = true, alwaysUseFullName = true),
    )
}

/** All optional fields enabled together with the combined time label, to check for overlap/clipping. */
@Preview(name = "All fields + combined time (60 min)", showBackground = true)
@Composable
private fun PreviewEventComposeAllFieldsCombined() {
    PreviewRow(
        label = "All fields, 60 min (combined)",
        event =
            sampleEvent(
                id = 8L,
                duration = Duration.ofMinutes(60),
                subTitle = "Subtitle",
                upperText = "Upper Text",
                lowerText = "Lower Text",
            ),
        eventConfig =
            EventConfig(
                showTimeStart = true,
                showTimeEnd = true,
                showSubtitle = true,
                showUpperText = true,
                showLowerText = true,
            ),
    )
}

/** All optional fields enabled together with split time labels, to check the bottom-pinned end time doesn't collide with lower text. */
@Preview(name = "All fields + split times (150 min)", showBackground = true)
@Composable
private fun PreviewEventComposeAllFieldsSplit() {
    PreviewRow(
        label = "All fields, 150 min (split)",
        event =
            sampleEvent(
                id = 11L,
                duration = Duration.ofMinutes(150),
                subTitle = "Subtitle",
                upperText = "Upper Text",
                lowerText = "Lower Text",
            ),
        eventConfig =
            EventConfig(
                showTimeStart = true,
                showTimeEnd = true,
                showSubtitle = true,
                showUpperText = true,
                showLowerText = true,
            ),
    )
}

/** Minimal fields (title + combined time only), no subtitle/upper/lower text. */
@Preview(name = "Minimal fields + both times", showBackground = true)
@Composable
private fun PreviewEventComposeMinimalFields() {
    PreviewRow(
        label = "Minimal fields, 45 min",
        event = sampleEvent(id = 9L, duration = Duration.ofMinutes(45), subTitle = null, upperText = null),
        eventConfig = EventConfig(showTimeStart = true, showTimeEnd = true, showSubtitle = false, showUpperText = false),
    )
}
