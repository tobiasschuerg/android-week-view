package de.tobiasschuerg.weekview

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tobiasschuerg.weekview.model.Event
import de.tobiasschuerg.weekview.model.LocalDateRange
import de.tobiasschuerg.weekview.model.TimeSpan
import de.tobiasschuerg.weekview.model.WeekData
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class WeekViewDataUpdateTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val day = LocalDate.of(2025, 9, 2)

    private fun lesson(id: String) =
        Event.Single(
            id = id,
            date = day,
            title = "Lesson $id",
            shortTitle = id,
            timeSpan = TimeSpan.of(LocalTime.of(10, 0), Duration.ofHours(1)),
            textColor = Color.White,
            backgroundColor = Color.Blue,
        )

    @Test
    fun shouldShowTheNewEventsWhenANewWeekDataIsPassed() {
        val range = LocalDateRange(day, day)
        var weekData by mutableStateOf(WeekData(range, LocalTime.of(8, 0), LocalTime.of(18, 0), listOf(lesson("old"))))

        composeTestRule.setContent {
            MaterialTheme {
                WeekView(weekData = weekData, weekViewConfig = WeekViewConfig(), modifier = Modifier.size(300.dp, 600.dp))
            }
        }
        composeTestRule.onNodeWithTag("EventView_old").assertIsDisplayed()

        weekData = weekData.copy(events = listOf(lesson("new")))

        composeTestRule.onNodeWithTag("EventView_new").assertIsDisplayed()
        composeTestRule.onNodeWithTag("EventView_old").assertDoesNotExist()
    }
}
