package de.tobiasschuerg.weekview.compose

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tobiasschuerg.weekview.compose.components.DayHeaderRow
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.util.toShortDateStringWithoutYear
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.util.Locale

/**
 * The day header used to be a fixed 36.dp box, which cut off the date line as soon as
 * the system font scale was raised. It now grows with its content and only uses the
 * given height as a minimum, and falls back to short weekday names for the whole row
 * once a full name would no longer fit its column.
 */
@RunWith(AndroidJUnit4::class)
class DayHeaderRowTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val monday = LocalDate.of(2026, 9, 14)

    private fun setHeader(
        fontScale: Float,
        columnWidth: Dp = 80.dp,
        eventConfig: EventConfig = EventConfig(),
    ) {
        composeTestRule.setContent {
            val base = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(base.density, fontScale)) {
                MaterialTheme {
                    DayHeaderRow(
                        days = listOf(monday, monday.plusDays(1), monday.plusDays(2)),
                        today = monday,
                        leftOffsetDp = 48.dp,
                        topOffsetDp = 36.dp,
                        columnWidth = columnWidth,
                        eventConfig = eventConfig,
                        locale = Locale.GERMANY,
                    )
                }
            }
        }
    }

    @Test
    fun headerKeepsMinimumHeightAtDefaultFontScale() {
        setHeader(fontScale = 1f)

        val bounds = composeTestRule.onNodeWithTag("DayHeader_$monday").getUnclippedBoundsInRoot()
        assertTrue("expected at least 36.dp, was ${(bounds.bottom - bounds.top)}", (bounds.bottom - bounds.top) >= 36.dp)
    }

    @Test
    fun headerGrowsSoBothLinesStayVisibleAtLargeFontScale() {
        setHeader(fontScale = 1.5f)

        val header = composeTestRule.onNodeWithTag("DayHeader_$monday").getUnclippedBoundsInRoot()
        val dateLine =
            composeTestRule.onNodeWithText(
                monday.toShortDateStringWithoutYear(Locale.GERMANY),
                useUnmergedTree = true,
            ).assertIsDisplayed().getUnclippedBoundsInRoot()
        assertTrue("header must grow beyond 36.dp, was ${(header.bottom - header.top)}", (header.bottom - header.top) > 36.dp)
        assertTrue("date line must end inside the header", dateLine.bottom <= header.bottom)
    }

    @Test
    fun fullDayNamesShownWhenTheyFitTheColumn() {
        setHeader(fontScale = 1f, columnWidth = 120.dp, eventConfig = EventConfig(alwaysUseFullName = true))

        composeTestRule.onNodeWithText("Montag", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Mittwoch", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun shortDayNamesUsedForAllDaysWhenOneFullNameWouldNotFit() {
        // "Mittwoch" no longer fits 60.dp at 1.5x, so even the short "Montag" switches to "Mo.".
        setHeader(fontScale = 1.5f, columnWidth = 60.dp, eventConfig = EventConfig(alwaysUseFullName = true))

        composeTestRule.onNodeWithText("Mo.", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Mi.", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Montag", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun shortDayNamesAlwaysUsedWhenFullNamesAreNotConfigured() {
        setHeader(fontScale = 1f, columnWidth = 120.dp, eventConfig = EventConfig(alwaysUseFullName = false))

        composeTestRule.onNodeWithText("Mo.", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Montag", useUnmergedTree = true).assertDoesNotExist()
    }
}
