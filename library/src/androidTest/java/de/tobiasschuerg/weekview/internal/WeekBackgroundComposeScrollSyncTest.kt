package de.tobiasschuerg.weekview.internal

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tobiasschuerg.weekview.EventConfig
import de.tobiasschuerg.weekview.WeekViewConfig
import de.tobiasschuerg.weekview.model.Event
import de.tobiasschuerg.weekview.model.LocalDateRange
import de.tobiasschuerg.weekview.model.TimeSpan
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

/**
 * The time axis and the grid scroll with one shared state, so an hour label has to stay level with
 * the grid row of its hour however far the view is scrolled.
 */
@RunWith(AndroidJUnit4::class)
class WeekBackgroundComposeScrollSyncTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val firstDay = LocalDate.of(2025, 9, 1)

    private fun setWeekView(
        timeRange: TimeSpan,
        entryStart: LocalTime,
        fillViewport: Boolean,
    ) {
        val entry =
            Event.Single(
                id = "1",
                date = firstDay,
                title = "Entry",
                shortTitle = "E",
                timeSpan = TimeSpan(entryStart, entryStart.plusHours(1)),
                textColor = Color(0xFF000000),
                backgroundColor = Color(0xFF00FF00),
            )
        composeTestRule.setContent {
            MaterialTheme {
                Box(modifier = Modifier.height(600.dp)) {
                    WeekBackgroundCompose(
                        dateRange = LocalDateRange(firstDay, firstDay.plusDays(4)),
                        timeRange = timeRange,
                        events = listOf(entry),
                        // Without its times, the entry cannot be mistaken for the hour label it is compared with.
                        eventConfig = EventConfig(showTimeStart = false, showTimeEnd = false),
                        weekViewConfig =
                            WeekViewConfig(
                                locale = Locale.GERMANY,
                                showCurrentTimeIndicator = false,
                                fillViewport = fillViewport,
                            ),
                    )
                }
            }
        }
        composeTestRule.waitForIdle()
    }

    /** Vertical distance between the hour label and the entry starting at that hour. */
    private fun labelToEntryOffset(label: String): Dp {
        val labelTop = composeTestRule.onNodeWithText(label).getUnclippedBoundsInRoot().top
        val entryTop = composeTestRule.onNodeWithTag("EventView_1").getUnclippedBoundsInRoot().top
        return entryTop - labelTop
    }

    /** Swipes up on the time axis, often enough to reach the end of any grid in these tests. */
    private fun hasVerticalScrollRange() = SemanticsMatcher.keyIsDefined(SemanticsProperties.VerticalScrollAxisRange)

    private fun swipeUpOnAxis() {
        repeat(6) {
            composeTestRule.onRoot().performTouchInput {
                val x = 16.dp.toPx()
                swipe(start = Offset(x, 480.dp.toPx()), end = Offset(x, 60.dp.toPx()))
            }
            composeTestRule.waitForIdle()
        }
    }

    /**
     * Two scroll containers sharing one state drift apart: each writes its own maximum into the state,
     * and whichever is measured last decides how far both can scroll. The axis rows can be taller than
     * the grid, so the axis then scrolled past the grid. A single container rules that out.
     */
    @Test
    fun shouldScrollTheTimeAxisAndTheGridAsOneContainer() {
        setWeekView(TimeSpan(LocalTime.of(6, 0), LocalTime.of(22, 0)), LocalTime.of(20, 0), fillViewport = true)

        val verticalScrollers = composeTestRule.onAllNodes(hasVerticalScrollRange()).fetchSemanticsNodes()

        assertEquals(1, verticalScrollers.size)
    }

    @Test
    fun shouldKeepTheAxisLevelWithTheGridWhenTheGridIsShorterThanTheViewport() {
        // 07:30–15:30 like the School sample: the grid ends mid-hour and fits the view without scrolling.
        setWeekView(TimeSpan(LocalTime.of(7, 30), LocalTime.of(15, 30)), LocalTime.of(9, 0), fillViewport = false)
        val before = labelToEntryOffset("09:00")

        swipeUpOnAxis()

        assertEquals(before.value, labelToEntryOffset("09:00").value, 1f)
    }

    @Test
    fun shouldKeepTheAxisLevelWithTheGridWhenScrolledToTheBottom() {
        // A long day on a whole hour: the grid scrolls, and the axis must not scroll further than it.
        setWeekView(TimeSpan(LocalTime.of(6, 0), LocalTime.of(22, 0)), LocalTime.of(20, 0), fillViewport = true)
        val before = labelToEntryOffset("20:00")

        swipeUpOnAxis()

        assertEquals(before.value, labelToEntryOffset("20:00").value, 1f)
    }
}
