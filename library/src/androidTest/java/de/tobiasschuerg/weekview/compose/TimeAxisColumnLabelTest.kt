package de.tobiasschuerg.weekview.compose

import androidx.activity.ComponentActivity
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tobiasschuerg.weekview.compose.components.TimeAxisColumn
import de.tobiasschuerg.weekview.compose.components.TimeAxisDefaults
import de.tobiasschuerg.weekview.util.toAxisLabel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalTime
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class TimeAxisColumnLabelTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val hours = (0..23).map { LocalTime.of(it, 0) }

    /** A late minute of a two-digit hour, one of the widest times the current-time pill shows. */
    private val now = LocalTime.of(10, 58)

    private fun setAxis(locale: Locale) {
        composeTestRule.setContent {
            MaterialTheme {
                TimeAxisColumn(
                    timeLabels = hours,
                    now = now,
                    gridStartTime = LocalTime.MIDNIGHT,
                    gridEndTime = LocalTime.MAX,
                    rowHeightDp = 60.dp,
                    gridHeightDp = 60.dp * hours.size,
                    leftOffsetDp = TimeAxisDefaults.rememberLabelMetrics(locale).axisWidth,
                    scrollState = rememberScrollState(),
                    showNowIndicator = true,
                    locale = locale,
                )
            }
        }
        composeTestRule.waitForIdle()
    }

    /**
     * Compares the width the text needs with the width it was given. [TextLayoutResult.didOverflowWidth]
     * cannot be used: without soft wrap the paragraph is laid out at the full available width, so it
     * reports an overflow for any text narrower than its box.
     */
    private fun SemanticsNodeInteraction.assertTextFitsItsWidth(description: String) {
        val layouts = mutableListOf<TextLayoutResult>()
        performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        val layout = layouts.single()
        val neededWidth = layout.multiParagraph.intrinsics.maxIntrinsicWidth
        val availableWidth = layout.layoutInput.constraints.maxWidth
        assertTrue(
            "$description needs ${neededWidth}px but the time axis only leaves ${availableWidth}px",
            neededWidth <= availableWidth,
        )
    }

    private fun assertAllLabelsFit(locale: Locale) {
        setAxis(locale)

        hours.map { it.toAxisLabel(locale).time }.distinct().forEach { time ->
            val nodes = composeTestRule.onAllNodesWithText(time)
            repeat(nodes.fetchSemanticsNodes().size) { index ->
                nodes[index].assertTextFitsItsWidth("Hour label $time in $locale")
            }
        }
        composeTestRule
            .onNodeWithText(now.toAxisLabel(locale).time)
            .assertTextFitsItsWidth("Current-time pill in $locale")
    }

    private fun labelCount(text: String): Int = composeTestRule.onAllNodesWithText(text).fetchSemanticsNodes().size

    @Test
    fun shouldFitEveryLabelAndTheCurrentTimePillWhenTheLocaleUses12HourTime() {
        assertAllLabelsFit(Locale.US)
    }

    @Test
    fun shouldFitEveryLabelAndTheCurrentTimePillWhenTheLocaleUses24HourTime() {
        assertAllLabelsFit(Locale.GERMANY)
    }

    @Test
    fun shouldStackTheDayPeriodBelowEveryHourWhenTheLocaleUses12HourTime() {
        setAxis(Locale.US)

        assertEquals(12, labelCount("AM"))
        assertEquals(12, labelCount("PM"))
    }

    @Test
    fun shouldNotShowADayPeriodWhenTheLocaleUses24HourTime() {
        setAxis(Locale.GERMANY)

        assertEquals(0, labelCount("AM"))
        assertEquals(0, labelCount("PM"))
    }
}
