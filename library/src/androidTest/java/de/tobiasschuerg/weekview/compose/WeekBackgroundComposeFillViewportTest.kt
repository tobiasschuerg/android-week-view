package de.tobiasschuerg.weekview.compose

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tobiasschuerg.weekview.data.LocalDateRange
import de.tobiasschuerg.weekview.data.WeekViewConfig
import de.tobiasschuerg.weekview.util.TimeSpan
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class WeekBackgroundComposeFillViewportTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    // A 600dp tall view leaves room for well over 8 rows of 60dp below the header, so a filled
    // grid starting at 08:00 reaches at least 16:00; an unfilled one stops at 10:00.
    private val filledHour = "13:00"

    private fun setWeekView(config: WeekViewConfig) {
        val firstDay = LocalDate.of(2025, 9, 1)
        composeTestRule.setContent {
            MaterialTheme {
                Box(modifier = Modifier.height(600.dp)) {
                    WeekBackgroundCompose(
                        dateRange = LocalDateRange(firstDay, firstDay.plusDays(4)),
                        timeRange = TimeSpan(LocalTime.of(8, 0), LocalTime.of(10, 0)),
                        weekViewConfig = config,
                    )
                }
            }
        }
        composeTestRule.waitForIdle()
    }

    private fun hourLabelCount(text: String): Int = composeTestRule.onAllNodesWithText(text).fetchSemanticsNodes().size

    @Test
    fun shouldPadAShortScheduleWithHourRowsWhenFillingTheViewportByDefault() {
        setWeekView(WeekViewConfig(locale = Locale.GERMANY, showCurrentTimeIndicator = false))

        assertEquals(1, hourLabelCount("08:00"))
        assertEquals(1, hourLabelCount(filledHour))
    }

    @Test
    fun shouldShowOnlyTheRequestedHoursWhenFillViewportIsDisabled() {
        setWeekView(WeekViewConfig(locale = Locale.GERMANY, showCurrentTimeIndicator = false, fillViewport = false))

        assertEquals(1, hourLabelCount("08:00"))
        assertEquals(0, hourLabelCount(filledHour))
    }
}
