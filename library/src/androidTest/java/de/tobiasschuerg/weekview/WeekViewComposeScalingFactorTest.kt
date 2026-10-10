package de.tobiasschuerg.weekview

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
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tobiasschuerg.weekview.model.LocalDateRange
import de.tobiasschuerg.weekview.model.WeekData
import org.junit.Assert.assertTrue
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
        pinchIn()

        // Tighten minScalingFactor without changing weekViewConfig.scalingFactor, so the
        // stale persisted zoom state (0.5f) is never resynced before the next recomposition.
        config = config.copy(minScalingFactor = 0.9f)
        composeTestRule.waitForIdle()

        // Should recompose without WeekViewConfig's init `require` throwing.
        composeTestRule.onNodeWithTag("WeekView").assertIsDisplayed()
    }

    @Test
    fun shouldReportZoomToTheLatestCallbackWhenActionsChangeAfterTheFirstComposition() {
        val today = LocalDate.of(2025, 9, 2)
        val weekData = WeekData(LocalDateRange(today, today), LocalTime.of(8, 0), LocalTime.of(18, 0))
        val firstCalls = mutableListOf<Float>()
        val latestCalls = mutableListOf<Float>()
        var actions by mutableStateOf(WeekViewActions(onScalingFactorChange = { firstCalls += it }))

        composeTestRule.setContent {
            MaterialTheme {
                WeekViewCompose(
                    weekData = weekData,
                    weekViewConfig = WeekViewConfig(),
                    modifier = Modifier.testTag("WeekView").size(300.dp, 600.dp),
                    actions = actions,
                )
            }
        }
        // The gesture handler only starts on the first touch, so pinch once before swapping the callback.
        pinch(from = 100f, to = 70f)
        assertTrue("first callback was not called", firstCalls.isNotEmpty())
        firstCalls.clear()

        actions = WeekViewActions(onScalingFactorChange = { latestCalls += it })
        composeTestRule.waitForIdle()
        pinch(from = 70f, to = 40f)

        assertTrue("latest callback was not called", latestCalls.isNotEmpty())
        assertTrue("stale callback was called: $firstCalls", firstCalls.isEmpty())
    }

    private fun pinchIn() = pinch(from = 100f, to = 10f)

    /** Two-finger pinch around the center; [from] and [to] are each finger's distance from it. */
    private fun pinch(
        from: Float,
        to: Float,
    ) {
        composeTestRule.onNodeWithTag("WeekView").performTouchInput {
            down(1, Offset(center.x - from, center.y))
            down(2, Offset(center.x + from, center.y))
            updatePointerTo(1, Offset(center.x - to, center.y))
            updatePointerTo(2, Offset(center.x + to, center.y))
            move()
            up(1)
            up(2)
        }
        composeTestRule.waitForIdle()
    }
}
