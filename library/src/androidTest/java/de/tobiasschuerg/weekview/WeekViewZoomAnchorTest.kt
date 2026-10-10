package de.tobiasschuerg.weekview

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tobiasschuerg.weekview.model.Event
import de.tobiasschuerg.weekview.model.LocalDateRange
import de.tobiasschuerg.weekview.model.TimeSpan
import de.tobiasschuerg.weekview.model.WeekData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

/** Pinch-zooming keeps the time under the fingers in place instead of zooming from the top of the grid. */
@RunWith(AndroidJUnit4::class)
class WeekViewZoomAnchorTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val day = LocalDate.of(2025, 9, 2)

    @Test
    fun shouldKeepTheTimeUnderTheFingersInPlaceWhenZoomingIn() {
        val lesson =
            Event.Single(
                id = "noon",
                date = day,
                title = "Noon",
                shortTitle = "N",
                timeSpan = TimeSpan.of(LocalTime.of(12, 0), Duration.ofHours(1)),
                textColor = Color.White,
                backgroundColor = Color.Blue,
            )
        val weekData = WeekData(LocalDateRange(day, day), LocalTime.of(8, 0), LocalTime.of(18, 0), listOf(lesson))
        composeTestRule.setContent {
            MaterialTheme {
                WeekView(
                    weekData = weekData,
                    weekViewConfig = WeekViewConfig(),
                    modifier = Modifier.testTag("WeekView").size(300.dp, 600.dp),
                )
            }
        }
        val view = composeTestRule.onNodeWithTag("WeekView").getUnclippedBoundsInRoot()
        val eventTopBefore = composeTestRule.onNodeWithTag("EventView_noon").getUnclippedBoundsInRoot().top

        // Pinch out around the top edge of the event, in small steps like a real gesture.
        composeTestRule.onNodeWithTag("WeekView").performTouchInput {
            val focusY = (eventTopBefore - view.top).toPx()
            down(1, Offset(centerX - 60f, focusY))
            down(2, Offset(centerX + 60f, focusY))
            for (step in 1..10) {
                updatePointerTo(1, Offset(centerX - 60f - step * 6f, focusY))
                updatePointerTo(2, Offset(centerX + 60f + step * 6f, focusY))
                move()
            }
            up(1)
            up(2)
        }
        composeTestRule.waitForIdle()

        val eventBoundsAfter = composeTestRule.onNodeWithTag("EventView_noon").getUnclippedBoundsInRoot()
        assertTrue("event did not grow, so the view did not zoom", eventBoundsAfter.bottom - eventBoundsAfter.top > 60.dp * 1.5f)
        assertEquals(eventTopBefore.value, eventBoundsAfter.top.value, 2f)
    }
}
