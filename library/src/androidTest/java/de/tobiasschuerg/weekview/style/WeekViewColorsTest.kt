package de.tobiasschuerg.weekview.style

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tobiasschuerg.weekview.WeekViewCompose
import de.tobiasschuerg.weekview.WeekViewConfig
import de.tobiasschuerg.weekview.model.LocalDateRange
import de.tobiasschuerg.weekview.model.WeekData
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class WeekViewColorsTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun shouldTakeTextAndGridColorsFromTheThemeWhenUsingTheDefaults() {
        val scheme =
            darkColorScheme(
                onSurface = Color(0xFFEEEEEE),
                onSurfaceVariant = Color(0xFFCCCCCC),
                outlineVariant = Color(0xFF444444),
            )
        lateinit var colors: WeekViewColors

        composeTestRule.setContent {
            MaterialTheme(colorScheme = scheme) {
                colors = defaultWeekViewColors()
            }
        }
        composeTestRule.waitForIdle()

        assertEquals(scheme.onSurfaceVariant, colors.dayHeaderText)
        assertEquals(scheme.onSurfaceVariant, colors.timeLabelTextColor)
        assertEquals(scheme.outlineVariant, colors.gridLineColor)
        assertEquals(scheme.onSurface, colors.currentDayText)
    }

    @Test
    fun shouldDrawWithTheGivenStyleWhenOnePassedToTheWeekView() {
        val today = LocalDate.now()
        val weekData = WeekData(LocalDateRange(today, today), LocalTime.of(8, 0), LocalTime.of(18, 0))
        val todayHeader = Color.Magenta

        composeTestRule.setContent {
            MaterialTheme {
                WeekViewCompose(
                    weekData = weekData,
                    weekViewConfig = WeekViewConfig(),
                    modifier = Modifier.size(300.dp, 600.dp),
                    style = WeekViewStyle(colors = defaultWeekViewColors().copy(currentDayBackground = todayHeader)),
                )
            }
        }

        val pixels = composeTestRule.onNodeWithTag("DayHeader_$today").captureToImage().toPixelMap()
        // Top-left corner: background only, clear of the centered day name.
        assertEquals(todayHeader, pixels[1, 1])
    }
}
