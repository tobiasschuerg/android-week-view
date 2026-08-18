package de.tobiasschuerg.weekview.compose

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.LocalDateRange
import de.tobiasschuerg.weekview.data.WeekViewConfig
import de.tobiasschuerg.weekview.util.TimeSpan
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime

/**
 * Verifies that events are only exposed as enabled/actionable to accessibility services when a
 * click or long-press handler is actually supplied, matching the DayHeaderRow behavior. Without a
 * handler, `combinedClickable(enabled = false)` still registers an OnClick semantics action but
 * marks the node disabled, which is what changes how screen readers announce it.
 */
@RunWith(AndroidJUnit4::class)
class EventClickabilityAccessibilityTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val testDate: LocalDate = LocalDate.of(2025, 9, 2)
    private val dateRange = LocalDateRange(testDate, testDate.plusDays(2))

    @Test
    fun singleEventWithoutHandlersIsDisabled() {
        val event =
            Event.Single(
                id = 1L,
                date = testDate,
                title = "Test Event",
                shortTitle = "Test",
                timeSpan = TimeSpan(LocalTime.of(9, 0), LocalTime.of(10, 0)),
                textColor = 0xFF000000.toInt(),
                backgroundColor = 0xFF00FF00.toInt(),
            )

        composeTestRule.setContent {
            MaterialTheme {
                WeekBackgroundCompose(
                    dateRange = dateRange,
                    events = listOf(event),
                    timeRange = event.timeSpan,
                    weekViewConfig = WeekViewConfig(),
                )
            }
        }

        composeTestRule.onNodeWithTag("EventView_1").assertIsNotEnabled()
    }

    @Test
    fun singleEventWithClickHandlerIsEnabledAndClickable() {
        val event =
            Event.Single(
                id = 2L,
                date = testDate,
                title = "Test Event",
                shortTitle = "Test",
                timeSpan = TimeSpan(LocalTime.of(9, 0), LocalTime.of(10, 0)),
                textColor = 0xFF000000.toInt(),
                backgroundColor = 0xFF00FF00.toInt(),
            )

        composeTestRule.setContent {
            MaterialTheme {
                WeekBackgroundCompose(
                    dateRange = dateRange,
                    events = listOf(event),
                    timeRange = event.timeSpan,
                    weekViewConfig = WeekViewConfig(),
                    onEventClick = {},
                )
            }
        }

        composeTestRule.onNodeWithTag("EventView_2").assertIsEnabled().assertHasClickAction()
    }

    @Test
    fun allDayEventWithoutHandlersIsDisabled() {
        val event =
            Event.AllDay(
                id = 3L,
                date = testDate,
                title = "Holiday",
                shortTitle = "Holiday",
                textColor = 0xFF000000.toInt(),
                backgroundColor = 0xFF00FF00.toInt(),
            )

        composeTestRule.setContent {
            MaterialTheme {
                WeekBackgroundCompose(
                    dateRange = dateRange,
                    allDayEvents = listOf(event),
                    timeRange = TimeSpan.of(LocalTime.of(9, 0), java.time.Duration.ofHours(1)),
                    weekViewConfig = WeekViewConfig(),
                )
            }
        }

        composeTestRule.onNodeWithTag("AllDayEventView_3").assertIsNotEnabled()
    }

    @Test
    fun multiDayEventWithoutHandlersIsDisabled() {
        val event =
            Event.MultiDay(
                id = 4L,
                date = testDate,
                title = "Conference",
                shortTitle = "Conference",
                lastDate = testDate.plusDays(1),
                textColor = 0xFF000000.toInt(),
                backgroundColor = 0xFF00FF00.toInt(),
            )

        composeTestRule.setContent {
            MaterialTheme {
                WeekBackgroundCompose(
                    dateRange = dateRange,
                    multiDayEvents = listOf(event),
                    timeRange = TimeSpan.of(LocalTime.of(9, 0), java.time.Duration.ofHours(1)),
                    weekViewConfig = WeekViewConfig(),
                )
            }
        }

        composeTestRule.onNodeWithTag("MultiDayEventView_4").assertIsNotEnabled()
    }
}
