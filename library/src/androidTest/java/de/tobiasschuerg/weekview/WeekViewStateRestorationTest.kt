package de.tobiasschuerg.weekview

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tobiasschuerg.weekview.model.LocalDateRange
import de.tobiasschuerg.weekview.model.WeekData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class WeekViewStateRestorationTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun shouldKeepTheZoomLevelWhenTheStateIsSavedAndRestored() {
        val day = LocalDate.of(2025, 9, 2)
        val weekData = WeekData(LocalDateRange(day, day), LocalTime.of(8, 0), LocalTime.of(18, 0))
        val restorationTester = StateRestorationTester(composeTestRule)
        lateinit var state: WeekViewState

        restorationTester.setContent {
            state = rememberWeekViewState()
            MaterialTheme {
                WeekView(
                    weekData = weekData,
                    weekViewConfig = WeekViewConfig(),
                    modifier = Modifier.testTag("WeekView").size(300.dp, 600.dp),
                    state = state,
                )
            }
        }
        composeTestRule.onNodeWithTag("WeekView").performTouchInput {
            down(1, Offset(center.x - 100f, center.y))
            down(2, Offset(center.x + 100f, center.y))
            updatePointerTo(1, Offset(center.x - 60f, center.y))
            updatePointerTo(2, Offset(center.x + 60f, center.y))
            move()
            up(1)
            up(2)
        }
        composeTestRule.waitForIdle()
        val zoomed = state.scalingFactor
        assertNotEquals(1f, zoomed)

        restorationTester.emulateSavedInstanceStateRestore()

        assertEquals(zoomed, state.scalingFactor)
    }
}
