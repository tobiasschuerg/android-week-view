package de.tobiasschuerg.weekview.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.tobiasschuerg.weekview.EventConfig
import de.tobiasschuerg.weekview.internal.layout.EventOverlapCalculator
import de.tobiasschuerg.weekview.model.Event
import de.tobiasschuerg.weekview.model.TimeSpan
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

/**
 * Previews for [EventCompose] covering the start/end time label combinations, the three
 * height-aware time label modes (stacked, corners, none - see EventFieldLayout), the
 * shrunk title on very low entries and the height-aware two-line title wrapping.
 *
 * Entry height is duration * scalingFactor (1f here), so at font scale 1.0 both times
 * are stacked from 42 min on, sit in the corners between 24 and 41 min and are dropped
 * below that.
 */

private val fullEventLayout =
    EventOverlapCalculator.EventLayout(
        widthFraction = 1f,
        offsetFraction = 0f,
        overlapGroup = 0,
    )

private fun sampleEvent(
    id: String,
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
        backgroundColor = 0xFF90323D.toInt(),
        textColor = 0xFFDDDDDD.toInt(),
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

/** Stacked labels: start above the title, end pinned to the bottom right. */
@Preview(name = "Both times stacked (60 min)", showBackground = true)
@Composable
private fun PreviewEventComposeBothTimesStacked() {
    PreviewRow(
        label = "Both times, 60 min (stacked)",
        event = sampleEvent(id = "1", duration = Duration.ofMinutes(60)),
        eventConfig = EventConfig(showTimeStart = true, showTimeEnd = true),
    )
}

/** Stacked labels on a tall entry, with room left over for location and teacher. */
@Preview(name = "Both times stacked (120 min)", showBackground = true)
@Composable
private fun PreviewEventComposeBothTimesStackedTall() {
    PreviewRow(
        label = "Both times, 120 min (stacked + location + teacher)",
        event = sampleEvent(id = "10", duration = Duration.ofMinutes(120)),
        eventConfig = EventConfig(showTimeStart = true, showTimeEnd = true),
    )
}

/** Corner labels: too low to stack, so start sits top-left and end bottom-right as small labels. */
@Preview(name = "Both times in corners (30 min)", showBackground = true)
@Composable
private fun PreviewEventComposeBothTimesCorners() {
    PreviewRow(
        label = "Both times, 30 min (corners)",
        event = sampleEvent(id = "12", duration = Duration.ofMinutes(30), subTitle = null, upperText = null),
        eventConfig = EventConfig(showTimeStart = true, showTimeEnd = true),
    )
}

/** Very low entry: no room for any time label, the title is shrunk to fit. */
@Preview(name = "Title only, shrunk (12 min)", showBackground = true)
@Composable
private fun PreviewEventComposeShrunkTitle() {
    PreviewRow(
        label = "Both times enabled, 12 min (title only, shrunk)",
        event = sampleEvent(id = "13", title = "Mentor", shortTitle = "Mentor", duration = Duration.ofMinutes(12)),
        eventConfig = EventConfig(showTimeStart = true, showTimeEnd = true),
    )
}

/** Only the start time shown, stacked above the title. */
@Preview(name = "Start time only", showBackground = true)
@Composable
private fun PreviewEventComposeStartTimeOnly() {
    PreviewRow(
        label = "Start time only, 120 min",
        event = sampleEvent(id = "2", duration = Duration.ofMinutes(120)),
        eventConfig = EventConfig(showTimeStart = true, showTimeEnd = false),
    )
}

/** Only the end time shown, pinned to the bottom right. */
@Preview(name = "End time only", showBackground = true)
@Composable
private fun PreviewEventComposeEndTimeOnly() {
    PreviewRow(
        label = "End time only, 120 min",
        event = sampleEvent(id = "3", duration = Duration.ofMinutes(120)),
        eventConfig = EventConfig(showTimeStart = false, showTimeEnd = true),
    )
}

/** Neither time shown. */
@Preview(name = "No time label", showBackground = true)
@Composable
private fun PreviewEventComposeNoTimes() {
    PreviewRow(
        label = "No time label, 90 min",
        event = sampleEvent(id = "4"),
        eventConfig = EventConfig(showTimeStart = false, showTimeEnd = false),
    )
}

/** Short lesson (25 min): still enough room for corner labels around a full-height title. */
@Preview(name = "Both times - short lesson (25 min)", showBackground = true)
@Composable
private fun PreviewEventComposeShortLesson() {
    PreviewRow(
        label = "Both times, 25 min",
        event = sampleEvent(id = "5", duration = Duration.ofMinutes(25), subTitle = null, upperText = null),
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
        label = "Long title, 120 min (2-line title + stacked time)",
        event =
            sampleEvent(
                id = "6",
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
                id = "7",
                title = "Introduction to Machine Learning and Neural Networks",
                shortTitle = "Intro ML & Neural Networks",
                duration = Duration.ofMinutes(30),
            ),
        eventConfig = EventConfig(showTimeStart = true, showTimeEnd = true, alwaysUseFullName = true),
    )
}

/** All optional fields enabled on a 60 min entry: only those that fit below the stacked labels are shown. */
@Preview(name = "All fields (60 min)", showBackground = true)
@Composable
private fun PreviewEventComposeAllFieldsMedium() {
    PreviewRow(
        label = "All fields, 60 min (dropped by priority)",
        event =
            sampleEvent(
                id = "8",
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

/** All optional fields on a tall entry, to check the bottom-pinned end time doesn't collide with lower text. */
@Preview(name = "All fields (150 min)", showBackground = true)
@Composable
private fun PreviewEventComposeAllFieldsTall() {
    PreviewRow(
        label = "All fields, 150 min",
        event =
            sampleEvent(
                id = "11",
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

/** Minimal fields (title + stacked times only), no subtitle/upper/lower text. */
@Preview(name = "Minimal fields + both times", showBackground = true)
@Composable
private fun PreviewEventComposeMinimalFields() {
    PreviewRow(
        label = "Minimal fields, 45 min",
        event = sampleEvent(id = "9", duration = Duration.ofMinutes(45), subTitle = null, upperText = null),
        eventConfig = EventConfig(showTimeStart = true, showTimeEnd = true, showSubtitle = false, showUpperText = false),
    )
}
