package de.tobiasschuerg.weekview.compose

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.getUnclippedBoundsInRoot
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
import org.junit.Assert.assertTrue
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
 * Covers the start/end time labels: each independently controlled by its own config
 * flag and never combined into one line. Stacked with the title when the entry is tall
 * enough, moved into the corners as smaller labels when it isn't, and dropped entirely
 * on very low entries (see EventFieldLayout). Thresholds below assume font scale 1.0:
 * stacked needs 14 + 2 * 11 = 36.dp, corners need 2 * 9 = 18.dp, plus 1.dp spacing and
 * 2.dp padding on each side.
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
    fun bothTimesStackedAsSeparateLabelsWhenTallEnough() {
        // 60 min at scalingFactor=1f -> 60.dp, well above the stacked threshold.
        val start = LocalTime.of(9, 0)
        val duration = Duration.ofMinutes(60)
        setEventContent(id = 1L, start = start, duration = duration, eventConfig = EventConfig())

        composeTestRule.waitUntilTagsAreDisplayed("EventTime_1", "EventTimeEnd_1")

        composeTestRule.onNodeWithTag("EventTime_1", useUnmergedTree = true).assertIsDisplayed().assertTextEquals(expectedTimeText(start))
        composeTestRule
            .onNodeWithTag("EventTimeEnd_1", useUnmergedTree = true)
            .assertIsDisplayed()
            .assertTextEquals(expectedTimeText(start.plus(duration)))
    }

    @Test
    fun bothTimesShownInCornersWhenTooShortToStack() {
        // 30 min -> 30.dp, 24.dp available: below stacked (36) but above corners (18).
        val start = LocalTime.of(9, 0)
        val duration = Duration.ofMinutes(30)
        setEventContent(id = 7L, start = start, duration = duration, eventConfig = EventConfig())

        composeTestRule.waitUntilTagsAreDisplayed("EventTime_7", "EventTimeEnd_7")

        composeTestRule.onNodeWithTag("EventTime_7", useUnmergedTree = true).assertIsDisplayed().assertTextEquals(expectedTimeText(start))
        composeTestRule
            .onNodeWithTag("EventTimeEnd_7", useUnmergedTree = true)
            .assertIsDisplayed()
            .assertTextEquals(expectedTimeText(start.plus(duration)))
    }

    @Test
    fun noTimeLabelButTitleShownWhenTooShortForCorners() {
        // 12 min -> 12.dp, 10.dp available: below the corner threshold (18).
        setEventContent(id = 8L, duration = Duration.ofMinutes(12), title = "Mentor", eventConfig = EventConfig())

        composeTestRule.waitUntilTagsAreDisplayed("EventTitle_8")

        composeTestRule.onNodeWithTag("EventTitle_8", useUnmergedTree = true).assertIsDisplayed().assertTextEquals("Mentor")
        composeTestRule.onNodeWithTag("EventTime_8", useUnmergedTree = true).assertDoesNotExist()
        composeTestRule.onNodeWithTag("EventTimeEnd_8", useUnmergedTree = true).assertDoesNotExist()
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
        composeTestRule.onNodeWithTag("EventTimeEnd_2", useUnmergedTree = true).assertDoesNotExist()
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

        composeTestRule.waitUntilTagsAreDisplayed("EventTimeEnd_3")
        composeTestRule
            .onNodeWithTag("EventTimeEnd_3", useUnmergedTree = true)
            .assertIsDisplayed()
            .assertTextEquals(expectedTimeText(start.plus(duration)))
        composeTestRule.onNodeWithTag("EventTime_3", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun noTimeLabelShownWhenBothDisabled() {
        setEventContent(
            id = 4L,
            eventConfig = EventConfig(showTimeStart = false, showTimeEnd = false),
        )

        composeTestRule.waitUntilTagsAreDisplayed("EventView_4")
        composeTestRule.onNodeWithTag("EventTime_4", useUnmergedTree = true).assertDoesNotExist()
        composeTestRule.onNodeWithTag("EventTimeEnd_4", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun endTimeStaysBelowLocationAndTeacherWhenStacked() {
        // 90 min -> 84.dp available: 36 stacked + 12 location + 10 teacher = 58 fits.
        val start = LocalTime.of(9, 0)
        val duration = Duration.ofMinutes(90)
        composeTestRule.setContent {
            MaterialTheme {
                Box(Modifier.fillMaxSize()) {
                    EventCompose(
                        event = event(9L, start, duration).copy(subTitle = "Room 1", upperText = "Ms. Smith"),
                        scalingFactor = 1f,
                        eventConfig = EventConfig(),
                        startTime = LocalTime.of(8, 0),
                        columnWidth = 120.dp,
                        eventLayout = fullEventLayout,
                        locale = locale,
                    )
                }
            }
        }

        composeTestRule.waitUntilTagsAreDisplayed("EventTeacher_9", "EventTimeEnd_9")

        val teacherBottom = composeTestRule.onNodeWithTag("EventTeacher_9", useUnmergedTree = true).getUnclippedBoundsInRoot().bottom
        val endTop = composeTestRule.onNodeWithTag("EventTimeEnd_9", useUnmergedTree = true).getUnclippedBoundsInRoot().top
        assertTrue("end time label must not overlap the teacher line", endTop >= teacherBottom)
    }
}
