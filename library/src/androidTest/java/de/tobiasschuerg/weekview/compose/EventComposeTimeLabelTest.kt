package de.tobiasschuerg.weekview.compose

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.util.EventOverlapCalculator
import de.tobiasschuerg.weekview.util.TimeSpan
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

/**
 * Covers the start/end time label: each half independently controlled by its own
 * config flag, rendered as one combined "start - end" line at the top when the entry
 * is too short to split (or when only one of the two flags is enabled), or as two
 * separate labels - start at the top, end pinned to the bottom - once the entry is
 * tall enough (EventPositionUtil.allowsSplitTimeLabels, 90.dp threshold).
 */
@RunWith(AndroidJUnit4::class)
class EventComposeTimeLabelTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val testDate = LocalDate.of(2025, 9, 2)
    private val locale = Locale.GERMANY
    private val fullEventLayout =
        EventOverlapCalculator.EventLayout(
            widthFraction = 1f,
            offsetFraction = 0f,
            overlapGroup = 0,
        )

    private fun expectedTimeText(time: LocalTime): String =
        DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale).format(time)

    private fun event(
        id: Long,
        start: LocalTime,
        duration: Duration,
        title: String = "Test Event",
    ): Event.Single =
        Event.Single(
            id = id,
            date = testDate,
            title = title,
            shortTitle = title,
            subTitle = null,
            timeSpan = TimeSpan.of(start, duration),
            textColor = 0xFF000000.toInt(),
            backgroundColor = "#00FF00".toColorInt(),
        )

    private fun setEventContent(
        id: Long,
        start: LocalTime = LocalTime.of(9, 0),
        duration: Duration = Duration.ofMinutes(90),
        title: String = "Test Event",
        eventConfig: EventConfig,
    ) {
        composeTestRule.setContent {
            MaterialTheme {
                Box(Modifier.fillMaxSize()) {
                    EventCompose(
                        event = event(id, start, duration, title),
                        scalingFactor = 1f,
                        eventConfig = eventConfig,
                        startTime = LocalTime.of(8, 0),
                        columnWidth = 120.dp,
                        eventLayout = fullEventLayout,
                        locale = locale,
                    )
                }
            }
        }
    }

    // The outer entry Box is `combinedClickable` with role = Button, which merges all
    // descendant semantics (including the time label's testTag) into the Box's own node
    // for accessibility. Reaching the label directly requires the unmerged tree.
    private fun ComposeTestRule.waitUntilTagsAreDisplayed(vararg tags: String) {
        waitUntil(timeoutMillis = 1_000) {
            tags.all { tag -> onAllNodesWithTag(tag, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty() }
        }
    }

    @Test
    fun bothTimesShownAsSingleCombinedLabelWhenTooShortToSplit() {
        // 60 min at scalingFactor=1f -> eventHeight = 60.dp, below the 90.dp split threshold.
        val start = LocalTime.of(9, 0)
        val duration = Duration.ofMinutes(60)
        setEventContent(id = 1L, start = start, duration = duration, eventConfig = EventConfig())

        composeTestRule.waitUntilTagsAreDisplayed("EventTime_1")

        val expected = "${expectedTimeText(start)} - ${expectedTimeText(start.plus(duration))}"
        composeTestRule.onNodeWithTag("EventTime_1", useUnmergedTree = true).assertIsDisplayed().assertTextEquals(expected)
        composeTestRule.onNodeWithTag("EventTimeEnd_1", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun bothTimesSplitIntoSeparateLabelsWhenTallEnough() {
        // 120 min at scalingFactor=1f -> eventHeight = 120.dp, above the 90.dp split threshold.
        val start = LocalTime.of(9, 0)
        val duration = Duration.ofMinutes(120)
        setEventContent(id = 7L, start = start, duration = duration, eventConfig = EventConfig())

        composeTestRule.waitUntilTagsAreDisplayed("EventTime_7", "EventTimeEnd_7")

        composeTestRule.onNodeWithTag("EventTime_7", useUnmergedTree = true).assertIsDisplayed().assertTextEquals(expectedTimeText(start))
        composeTestRule
            .onNodeWithTag("EventTimeEnd_7", useUnmergedTree = true)
            .assertIsDisplayed()
            .assertTextEquals(expectedTimeText(start.plus(duration)))
    }

    @Test
    fun onlyStartTimeShownWhenEndTimeDisabled() {
        val start = LocalTime.of(9, 0)
        setEventContent(
            id = 2L,
            start = start,
            eventConfig = EventConfig(showTimeStart = true, showTimeEnd = false),
        )

        composeTestRule.waitUntilTagsAreDisplayed("EventTime_2")
        composeTestRule.onNodeWithTag("EventTime_2", useUnmergedTree = true).assertIsDisplayed().assertTextEquals(expectedTimeText(start))
    }

    @Test
    fun onlyEndTimeShownWhenStartTimeDisabled() {
        val start = LocalTime.of(9, 0)
        val duration = Duration.ofMinutes(90)
        setEventContent(
            id = 3L,
            start = start,
            duration = duration,
            eventConfig = EventConfig(showTimeStart = false, showTimeEnd = true),
        )

        composeTestRule.waitUntilTagsAreDisplayed("EventTime_3")
        composeTestRule
            .onNodeWithTag("EventTime_3", useUnmergedTree = true)
            .assertIsDisplayed()
            .assertTextEquals(expectedTimeText(start.plus(duration)))
    }

    @Test
    fun noTimeLabelShownWhenBothDisabled() {
        setEventContent(
            id = 4L,
            eventConfig = EventConfig(showTimeStart = false, showTimeEnd = false),
        )

        composeTestRule.waitUntilTagsAreDisplayed("EventView_4")
        composeTestRule.onNodeWithTag("EventTime_4", useUnmergedTree = true).assertDoesNotExist()
    }
}
