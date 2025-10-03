package de.tobiasschuerg.weekview.compose

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.data.LocalDateRange
import de.tobiasschuerg.weekview.data.WeekViewConfig
import de.tobiasschuerg.weekview.util.TimeSpan
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class WeekBackgroundComposeEventDisplayTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun eventIsDisplayedCorrectly() {
        // Arrange
        val testDate = LocalDate.of(2025, 9, 2)
        val dateRange = LocalDateRange(testDate, testDate.plusDays(2)) // mind. 3 Tage
        val event =
            Event.Single(
                id = 1L,
                date = testDate,
                title = "Test Event",
                shortTitle = "Test",
                subTitle = "Subtitle",
                timeSpan = TimeSpan(LocalTime.of(9, 0), LocalTime.of(18, 0)),
                textColor = 0xFF000000.toInt(),
                backgroundColor = 0xFF00FF00.toInt(),
            )

        // Act
        composeTestRule.setContent {
            MaterialTheme {
                WeekBackgroundCompose(
                    dateRange = dateRange,
                    events = listOf(event),
                    eventConfig =
                        EventConfig(
                            showSubtitle = true,
                            showTimeEnd = false,
                        ),
                    timeRange = event.timeSpan,
                    weekViewConfig = WeekViewConfig(),
                )
            }
        }

        // Assert
        composeTestRule.waitForIdle()

        // Wait a bit more to ensure compose hierarchy is fully established
        Thread.sleep(100)

        // Verify event is displayed with correct test tag
        composeTestRule.onNodeWithTag("EventView_1").assertIsDisplayed()
    }

    @Test
    fun eventWithoutSubtitleIsDisplayedCorrectly() {
        // Arrange
        val testDate = LocalDate.of(2025, 9, 2)
        val dateRange = LocalDateRange(testDate, testDate.plusDays(2)) // mind. 3 Tage
        val event =
            Event.Single(
                id = 2L,
                date = testDate,
                title = "Simple Event",
                shortTitle = "Simple",
                subTitle = null,
                timeSpan = TimeSpan(LocalTime.of(10, 0), LocalTime.of(11, 0)),
                textColor = 0xFF000000.toInt(),
                backgroundColor = 0xFF0000FF.toInt(),
            )

        // Act
        composeTestRule.setContent {
            MaterialTheme {
                WeekBackgroundCompose(
                    dateRange = dateRange,
                    events = listOf(event),
                    eventConfig =
                        EventConfig(
                            showSubtitle = false,
                            showTimeEnd = false,
                        ),
                    timeRange = event.timeSpan,
                    weekViewConfig = WeekViewConfig(),
                )
            }
        }

        // Assert
        composeTestRule.waitForIdle()
        Thread.sleep(100)

        // Verify event is displayed
        composeTestRule.onNodeWithTag("EventView_2").assertIsDisplayed()
    }

    @Test
    fun multipleEventsAreDisplayed() {
        // Arrange
        val testDate = LocalDate.of(2025, 9, 2)
        val dateRange = LocalDateRange(testDate, testDate.plusDays(2)) // mind. 3 Tage
        val events =
            listOf(
                Event.Single(
                    id = 3L,
                    date = testDate,
                    title = "Morning Event",
                    shortTitle = "Morning",
                    subTitle = null,
                    timeSpan = TimeSpan(LocalTime.of(9, 0), LocalTime.of(10, 0)),
                    textColor = 0xFF000000.toInt(),
                    backgroundColor = 0xFF0000FF.toInt(),
                ),
                Event.Single(
                    id = 4L,
                    date = testDate,
                    title = "Afternoon Event",
                    shortTitle = "Afternoon",
                    subTitle = null,
                    timeSpan = TimeSpan(LocalTime.of(11, 0), LocalTime.of(12, 0)),
                    textColor = 0xFF000000.toInt(),
                    backgroundColor = 0xFFFF0000.toInt(),
                ),
            )

        // Act
        composeTestRule.setContent {
            MaterialTheme {
                WeekBackgroundCompose(
                    dateRange = dateRange,
                    events = events,
                    eventConfig =
                        EventConfig(
                            showSubtitle = false,
                            showTimeEnd = false,
                        ),
                    timeRange = TimeSpan.of(LocalTime.of(10, 0), Duration.ofHours(1)),
                    weekViewConfig = WeekViewConfig(),
                )
            }
        }

        // Assert
        composeTestRule.waitForIdle()
        Thread.sleep(100)

        // Verify both events are displayed by their tags
        composeTestRule.onNodeWithTag("EventView_3").assertIsDisplayed()
        composeTestRule.onNodeWithTag("EventView_4").assertIsDisplayed()
    }

    @Test
    fun eventWithShortTitleConfiguration() {
        // Arrange
        val testDate = LocalDate.of(2025, 9, 2)
        val dateRange = LocalDateRange(testDate, testDate.plusDays(2)) // mind. 3 Tage
        val event =
            Event.Single(
                id = 5L,
                date = testDate,
                title = "Very Long Event Title That Should Be Shortened",
                shortTitle = "Short",
                subTitle = null,
                timeSpan = TimeSpan(LocalTime.of(10, 0), LocalTime.of(12, 0)),
                textColor = 0xFF000000.toInt(),
                backgroundColor = 0xFFFFFF00.toInt(),
            )

        // Act
        composeTestRule.setContent {
            MaterialTheme {
                WeekBackgroundCompose(
                    dateRange = dateRange,
                    events = listOf(event),
                    eventConfig =
                        EventConfig(
                            showSubtitle = false,
                            showTimeEnd = false,
                        ),
                    timeRange = event.timeSpan,
                    weekViewConfig = WeekViewConfig(),
                )
            }
        }

        // Assert
        composeTestRule.waitForIdle()
        Thread.sleep(100)

        // Verify event is displayed with test tag
        composeTestRule.onNodeWithTag("EventView_5").assertIsDisplayed()
    }

    @Test
    fun eventWithLongerDurationIsVisible() {
        // Arrange
        val testDate = LocalDate.of(2025, 9, 2)
        val dateRange = LocalDateRange(testDate, testDate.plusDays(2)) // mind. 3 Tage
        val event =
            Event.Single(
                id = 6L,
                date = testDate,
                title = "Long Meeting",
                shortTitle = "Meeting",
                subTitle = null,
                timeSpan = TimeSpan(LocalTime.of(9, 0), LocalTime.of(12, 0)),
                textColor = 0xFFFFFFFF.toInt(),
                backgroundColor = 0xFF800080.toInt(),
            )

        // Act
        composeTestRule.setContent {
            MaterialTheme {
                WeekBackgroundCompose(
                    dateRange = dateRange,
                    events = listOf(event),
                    eventConfig = EventConfig(),
                    timeRange = event.timeSpan,
                    weekViewConfig = WeekViewConfig(),
                )
            }
        }

        // Assert
        composeTestRule.waitForIdle()
        Thread.sleep(100)

        // Verify event is displayed
        composeTestRule.onNodeWithTag("EventView_6").assertIsDisplayed()
    }

    @Test
    fun eventsWithOverlappingTimesAreVisuallySeparated() {
        // This test was moved to WeekBackgroundComposeEventOverlapTest.kt for better separation and clarity.
        // See WeekBackgroundComposeEventOverlapTest for the implementation.
    }
}
