package de.tobiasschuerg.weekview.internal.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tobiasschuerg.weekview.internal.layout.totalHours
import de.tobiasschuerg.weekview.style.WeekViewColors
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

/** Grid lines are sized in dp, so they keep their thickness across screen densities. */
@RunWith(AndroidJUnit4::class)
class GridCanvasLineWidthTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun shouldScaleGridLinesWithDensityWhenDrawnOnADenseScreen() {
        val density = 4f
        composeTestRule.setContent {
            CompositionLocalProvider(LocalDensity provides Density(density)) {
                Box(Modifier.testTag(TAG).size(100.dp).background(Color.White)) {
                    GridCanvas(
                        modifier = Modifier.size(100.dp),
                        columnCount = 1,
                        rowHeightDp = 50.dp,
                        totalHours = 2f,
                        days = listOf(LocalDate.of(2025, 9, 1)),
                        today = LocalDate.of(2025, 9, 8),
                        highlightCurrentDay = false,
                        colors = blackGrid,
                    )
                }
            }
        }

        val pixels = composeTestRule.onNodeWithTag(TAG).captureToImage().toPixelMap()
        // Count the rows of the middle hour line (at 50.dp), away from the vertical lines and the clipped edges.
        val x = pixels.width / 2
        val middle = pixels.height / 2
        // The line is anti-aliased across rows, so sum its coverage (black = 1, white = 0) instead of counting rows.
        val coveredPx = (middle - 10..middle + 10).sumOf { y -> 1.0 - pixels[x, y].red }

        assertEquals(GRID_LINE_WIDTH.value * density.toDouble(), coveredPx, 0.5)
    }

    private companion object {
        const val TAG = "grid"
        val blackGrid =
            WeekViewColors(
                todayHighlight = Color.Transparent,
                nowIndicator = Color.Red,
                dayHeaderText = Color.Black,
                timeLabelTextColor = Color.Black,
                gridLineColor = Color.Black,
                currentDayBackground = Color.Transparent,
                currentDayText = Color.Black,
            )
    }
}
