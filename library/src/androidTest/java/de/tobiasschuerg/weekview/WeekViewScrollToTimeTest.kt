package de.tobiasschuerg.weekview

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tobiasschuerg.weekview.model.Event
import de.tobiasschuerg.weekview.model.LocalDateRange
import de.tobiasschuerg.weekview.model.TimeSpan
import de.tobiasschuerg.weekview.model.WeekData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

/** The week view can open at a given time and scroll there later. */
@RunWith(AndroidJUnit4::class)
class WeekViewScrollToTimeTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val day = LocalDate.of(2025, 9, 2)

    private fun lesson(
        id: String,
        start: LocalTime,
    ) = Event.Single(
        id = id,
        date = day,
        title = id,
        shortTitle = id,
        timeSpan = TimeSpan.of(start, Duration.ofHours(1)),
        textColor = Color.White,
        backgroundColor = Color.Blue,
    )

    private val weekData =
        WeekData(
            LocalDateRange(day, day),
            LocalTime.of(6, 0),
            LocalTime.of(22, 0),
            listOf(lesson("nine", LocalTime.of(9, 0)), lesson("eleven", LocalTime.of(11, 0))),
        )

    private lateinit var state: WeekViewState
    private lateinit var scope: CoroutineScope

    private fun setWeekView(initialTime: LocalTime?) {
        composeTestRule.setContent {
            state = rememberWeekViewState(initialTime = initialTime)
            scope = rememberCoroutineScope()
            MaterialTheme {
                WeekView(weekData = weekData, weekViewConfig = WeekViewConfig(), modifier = Modifier.size(300.dp, 600.dp), state = state)
            }
        }
    }

    /** Scroll offset that puts [hoursAfterGridStart] at the top: the grid starts at 06:00 with 60 dp per hour. */
    private fun offsetOf(hoursAfterGridStart: Int) = with(composeTestRule.density) { (60.dp * hoursAfterGridStart).roundToPx() }

    @Test
    fun shouldShowTheInitialTimeAtTheTopWhenFirstShown() {
        setWeekView(initialTime = LocalTime.of(9, 0))

        assertEquals(offsetOf(3), state.scrollState.value)
    }

    @Test
    fun shouldStartAtTheTopOfTheGridWhenNoInitialTimeIsGiven() {
        setWeekView(initialTime = null)

        assertEquals(0, state.scrollState.value)
    }

    @Test
    fun shouldScrollTheTimeToTheTopWhenAnimatingThere() {
        setWeekView(initialTime = null)

        composeTestRule.runOnIdle { scope.launch { state.animateScrollToTime(LocalTime.of(11, 0)) } }
        composeTestRule.waitForIdle()

        assertEquals(offsetOf(5), state.scrollState.value)
    }
}
