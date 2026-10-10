package de.tobiasschuerg.weekview.compose

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.data.LocalDateRange
import de.tobiasschuerg.weekview.data.WeekData
import de.tobiasschuerg.weekview.data.WeekViewConfig
import de.tobiasschuerg.weekview.util.EventOverlapCalculator
import de.tobiasschuerg.weekview.util.TimeSpan
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

/** Covers the [EventContent] slot: custom content replaces the default look while the week view keeps placement and clicks. */
@RunWith(AndroidJUnit4::class)
class EventContentSlotTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val testDate: LocalDate = LocalDate.of(2025, 9, 2)
    private val event =
        Event.Single(
            id = "1",
            date = testDate,
            title = "Math",
            shortTitle = "M",
            timeSpan = TimeSpan(LocalTime.of(9, 0), LocalTime.of(10, 0)),
            textColor = 0xFF000000.toInt(),
            backgroundColor = 0xFF00FF00.toInt(),
        )

    private fun weekData(): WeekData =
        WeekData(LocalDateRange(testDate, testDate.plusDays(2)), LocalTime.of(8, 0), LocalTime.of(12, 0)).apply { add(event) }

    @Test
    fun shouldRenderCustomContentInsteadOfDefaultWhenEventContentIsGiven() {
        composeTestRule.setContent {
            MaterialTheme {
                WeekViewCompose(
                    weekData = weekData(),
                    weekViewConfig = WeekViewConfig(),
                    eventContent = { scope -> Text(scope.event.title, Modifier.testTag("Custom_${scope.event.id}")) },
                )
            }
        }

        composeTestRule.onNodeWithTag("Custom_1", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("EventTitle_1", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun shouldRenderDefaultContentWhenNoEventContentIsGiven() {
        composeTestRule.setContent {
            MaterialTheme {
                WeekViewCompose(weekData = weekData(), weekViewConfig = WeekViewConfig())
            }
        }

        composeTestRule.onNodeWithTag("EventTitle_1", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun shouldKeepClickHandlingWhenContentIsCustom() {
        var clicked: Event? = null
        composeTestRule.setContent {
            MaterialTheme {
                WeekViewCompose(
                    weekData = weekData(),
                    weekViewConfig = WeekViewConfig(),
                    actions = WeekViewActions(onEventClick = { clicked = it }),
                    eventContent = { scope -> Text(scope.event.title) },
                )
            }
        }

        composeTestRule.onNodeWithTag("EventView_1").performClick()

        assertEquals(event, clicked)
    }

    @Test
    fun shouldPassEntrySizeMinusSpacingWhenRenderingContent() {
        // 60 min at scalingFactor 1f -> 60.dp high, full 120.dp column; 2.dp spacing on each side.
        var scope: EventContentScope? = null
        composeTestRule.setContent {
            MaterialTheme {
                Box(Modifier.fillMaxSize()) {
                    EventCompose(
                        event = event,
                        scalingFactor = 1f,
                        eventConfig = EventConfig(eventSpacingDp = 2),
                        startTime = LocalTime.of(8, 0),
                        columnWidth = 120.dp,
                        eventLayout = EventOverlapCalculator.EventLayout(widthFraction = 1f, offsetFraction = 0f, overlapGroup = 0),
                        locale = Locale.GERMANY,
                        eventContent = { scope = it },
                    )
                }
            }
        }
        composeTestRule.waitForIdle()

        assertEquals(116.dp, scope?.width)
        assertEquals(56.dp, scope?.height)
        assertEquals(event, scope?.event)
        assertEquals(Locale.GERMANY, scope?.locale)
    }
}
