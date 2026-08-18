package de.tobiasschuerg.weekview.compose

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tobiasschuerg.weekview.data.LocalDateRange
import de.tobiasschuerg.weekview.data.WeekData
import de.tobiasschuerg.weekview.data.WeekViewConfig
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class WeekViewComposeScalingFactorTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    /**
     * Reproduces the crash where a persisted (unsynced) zoom level falls outside a
     * subsequently tightened [WeekViewConfig.minScalingFactor]/[WeekViewConfig.maxScalingFactor],
     * causing [WeekViewConfig]'s own `require` check to throw when `WeekViewCompose` rebuilds its
     * active config from the raw persisted scaling factor.
     */
    @Test
    fun doesNotCrashWhenScalingBoundsTightenAfterZoom() {
        val today = LocalDate.of(2025, 9, 2)
        val weekData = WeekData(LocalDateRange(today, today), LocalTime.of(8, 0), LocalTime.of(18, 0))
        var config by mutableStateOf(
            WeekViewConfig(scalingFactor = 1f, minScalingFactor = 0.5f, maxScalingFactor = 2f),
        )

        composeTestRule.setContent {
            MaterialTheme {
                WeekViewCompose(
                    weekData = weekData,
                    weekViewConfig = config,
                    modifier = Modifier.testTag("WeekView").size(300.dp, 600.dp),
                )
            }
        }

        // Pinch in to drive the persisted zoom state down to the current minimum (0.5f).
        composeTestRule.onNodeWithTag("WeekView").performTouchInput {
            val p1Start = Offset(center.x - 100f, center.y)
            val p2Start = Offset(center.x + 100f, center.y)
            down(1, p1Start)
            down(2, p2Start)
            updatePointerTo(1, Offset(center.x - 10f, center.y))
            updatePointerTo(2, Offset(center.x + 10f, center.y))
            move()
            up(1)
            up(2)
        }
        composeTestRule.waitForIdle()

        // Tighten minScalingFactor without changing weekViewConfig.scalingFactor, so the
        // stale persisted zoom state (0.5f) is never resynced before the next recomposition.
        config = config.copy(minScalingFactor = 0.9f)
        composeTestRule.waitForIdle()

        // Should recompose without WeekViewConfig's init `require` throwing.
        composeTestRule.onNodeWithTag("WeekView").assertIsDisplayed()
    }
}
