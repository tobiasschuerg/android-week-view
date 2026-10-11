package de.tobiasschuerg.weekview

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTopPositionInRootIsEqualTo
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tobiasschuerg.weekview.model.Event
import de.tobiasschuerg.weekview.model.LocalDateRange
import de.tobiasschuerg.weekview.model.Period
import de.tobiasschuerg.weekview.model.TimeSpan
import de.tobiasschuerg.weekview.model.WeekData
import de.tobiasschuerg.weekview.style.WeekViewDefaults
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

/** The week view as a grid of fixed periods: entries snap to rows, empty rows take clicks, breaks show. */
@RunWith(AndroidJUnit4::class)
class WeekViewPeriodsTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val monday = LocalDate.of(2025, 9, 1)
    private val tuesday = monday.plusDays(1)

    private val periods =
        listOf(
            Period("1", TimeSpan(LocalTime.of(8, 0), LocalTime.of(8, 45))),
            Period("2", TimeSpan(LocalTime.of(8, 50), LocalTime.of(9, 35))),
            Period("Recess", TimeSpan(LocalTime.of(9, 35), LocalTime.of(9, 55)), isBreak = true),
            Period("3", TimeSpan(LocalTime.of(9, 55), LocalTime.of(10, 40))),
        )

    // Fixed heights and no viewport filling, so rows are exactly 60 dp (lessons) and 20 dp (break).
    private val layout = WeekLayout.Periods(periods, periodHeight = 60.dp, breakHeight = 20.dp)
    private val config =
        WeekViewConfig(fillViewport = false, showCurrentTimeIndicator = false, highlightCurrentDay = false, locale = Locale.GERMANY)

    private lateinit var state: WeekViewState

    private fun event(
        id: String,
        date: LocalDate,
        start: LocalTime,
        end: LocalTime,
    ) = Event.Single(
        id = id,
        date = date,
        title = id,
        shortTitle = id,
        timeSpan = TimeSpan(start, end),
        textColor = Color.White,
        backgroundColor = Color.Blue,
    )

    private fun setWeekView(
        events: List<Event>,
        actions: WeekViewActions = WeekViewActions(),
        height: Dp = 600.dp,
        initialTime: LocalTime? = null,
    ) {
        val weekData = WeekData(LocalDateRange(monday, tuesday), LocalTime.of(8, 0), LocalTime.of(11, 0), events)
        composeTestRule.setContent {
            state = rememberWeekViewState(initialTime = initialTime)
            MaterialTheme {
                WeekView(
                    weekData = weekData,
                    weekViewConfig = config,
                    modifier = Modifier.size(400.dp, height),
                    state = state,
                    eventConfig = EventConfig(eventSpacingDp = 0),
                    actions = actions,
                    layout = layout,
                )
            }
        }
    }

    @Test
    fun shouldFillBothRowsWhenAnEventIsADoubleLesson() {
        setWeekView(listOf(event("double", monday, LocalTime.of(8, 0), LocalTime.of(9, 35))))

        composeTestRule.onNodeWithTag("EventView_double").assertHeightIsEqualTo(120.dp)
    }

    @Test
    fun shouldFillTheWholeRowWhenAnEventIsShorterThanItsPeriod() {
        setWeekView(listOf(event("short", monday, LocalTime.of(9, 0), LocalTime.of(9, 10))))

        val event = composeTestRule.onNodeWithTag("EventView_short")
        val firstRowTop = composeTestRule.onNodeWithTag("PeriodLabel_0").getUnclippedBoundsInRoot().top
        event.assertHeightIsEqualTo(60.dp)
        event.assertTopPositionInRootIsEqualTo(firstRowTop + 60.dp)
    }

    @Test
    fun shouldCallBackWithDateAndPeriodWhenAnEmptyPeriodIsClicked() {
        var clicked: Pair<LocalDate, Period>? = null
        setWeekView(emptyList(), WeekViewActions(onEmptyPeriodClick = { date, period -> clicked = date to period }))

        composeTestRule.onNodeWithTag("EmptyPeriod_${tuesday}_3").performClick()

        assertEquals(tuesday to periods[3], clicked)
    }

    @Test
    fun shouldOfferNoEmptyCellWhenAnEventFillsThePeriod() {
        setWeekView(
            listOf(event("first", monday, LocalTime.of(8, 0), LocalTime.of(8, 45))),
            WeekViewActions(onEmptyPeriodClick = { _, _ -> }),
        )

        composeTestRule.onNodeWithTag("EmptyPeriod_${monday}_0").assertDoesNotExist()
        composeTestRule.onNodeWithTag("EmptyPeriod_${tuesday}_0").assertExists()
    }

    @Test
    fun shouldOfferNoEmptyCellWhenThePeriodIsABreak() {
        setWeekView(emptyList(), WeekViewActions(onEmptyPeriodClick = { _, _ -> }))

        composeTestRule.onNodeWithTag("EmptyPeriod_${monday}_2").assertDoesNotExist()
    }

    @Test
    fun shouldShowAPlaceholderThatTakesNoClicksWhenNoCallbackIsGiven() {
        setWeekView(emptyList())

        composeTestRule.onNodeWithTag("EmptyPeriod_${monday}_0").assertIsDisplayed().assertHasNoClickAction()
    }

    @Test
    fun shouldShowAPlaceholderThatTakesClicksWhenACallbackIsGiven() {
        setWeekView(emptyList(), WeekViewActions(onEmptyPeriodClick = { _, _ -> }))

        composeTestRule.onNodeWithTag("EmptyPeriod_${monday}_0").assertIsDisplayed().assertHasClickAction()
    }

    @Test
    fun shouldShowTheBreakLabelAcrossTheGridWhenPeriodsHaveABreak() {
        setWeekView(emptyList())

        composeTestRule.onNodeWithText("Recess").assertIsDisplayed()
        composeTestRule.onNodeWithTag("PeriodBreak_2").assertHeightIsEqualTo(20.dp)
    }

    @Test
    fun shouldShowEachLessonLabelWithItsTimesWhenTimesAreShown() {
        setWeekView(emptyList())

        composeTestRule.onNodeWithText("3").assertIsDisplayed()
        composeTestRule.onNodeWithText("09:55").assertIsDisplayed()
        composeTestRule.onNodeWithText("10:40").assertIsDisplayed()
    }

    @Test
    fun shouldStillClickTheEventWhenEmptyCellsAreClickable() {
        var clicked: Event? = null
        val lesson = event("first", monday, LocalTime.of(8, 0), LocalTime.of(8, 45))
        setWeekView(listOf(lesson), WeekViewActions(onEventClick = { clicked = it }, onEmptyPeriodClick = { _, _ -> }))

        composeTestRule.onNodeWithTag("EventView_first").performClick()

        assertEquals(lesson, clicked)
    }

    @Test
    fun shouldShowThePeriodAtTheTopWhenOpenedAtItsStartTime() {
        setWeekView(emptyList(), height = 150.dp, initialTime = LocalTime.of(8, 50))

        assertEquals(with(composeTestRule.density) { 60.dp.roundToPx() }, state.scrollState.value)
    }

    @Test
    fun shouldDrawNoGridLinesWhenTheRowIsABreak() {
        val breakColor = Color.Green
        val silentBreak = WeekLayout.Periods(periods.map { if (it.isBreak) it.copy(label = "") else it }, 60.dp, 20.dp)
        val weekData = WeekData(LocalDateRange(monday, tuesday), LocalTime.of(8, 0), LocalTime.of(11, 0))
        composeTestRule.setContent {
            MaterialTheme {
                WeekView(
                    weekData = weekData,
                    weekViewConfig = config,
                    modifier = Modifier.size(400.dp, 600.dp),
                    colors = WeekViewDefaults.colors(gridLineColor = Color.Red, breakBackground = breakColor),
                    layout = silentBreak,
                )
            }
        }

        // The divider between Monday and Tuesday runs through the middle of the break band.
        val band = composeTestRule.onNodeWithTag("PeriodBreak_2").fetchSemanticsNode().boundsInRoot
        val pixels = composeTestRule.onRoot().captureToImage().toPixelMap()
        assertEquals(breakColor, pixels[band.center.x.toInt(), band.center.y.toInt()])
    }

    @Test
    fun shouldShowOnlyTheLabelWhenTheRowIsTooShortForTheTimes() {
        val weekData = WeekData(LocalDateRange(monday, tuesday), LocalTime.of(8, 0), LocalTime.of(11, 0))
        composeTestRule.setContent {
            MaterialTheme {
                WeekView(
                    weekData = weekData,
                    weekViewConfig = config,
                    modifier = Modifier.size(400.dp, 600.dp),
                    layout = layout.copy(periodHeight = 30.dp),
                )
            }
        }

        composeTestRule.onNodeWithText("3").assertIsDisplayed()
        composeTestRule.onNodeWithText("09:55").assertDoesNotExist()
    }
}
