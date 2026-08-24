package de.tobiasschuerg.weekview.compose

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.util.EventOverlapCalculator
import de.tobiasschuerg.weekview.util.TimeSpan
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

/**
 * Covers the three styles shipped with the library: each keeps the auto-fitting fields and draws
 * its own container. Pixels are sampled on the left edge (mid height) and in the empty lower part
 * of a tall entry that only shows its title.
 */
@RunWith(AndroidJUnit4::class)
class BuiltInEventStylesTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val eventColor = Color.Blue
    private val surface = Color(0xFFFAFAFA)
    private val backdrop = Color.Red

    private fun event(subTitle: String? = null): Event.Single =
        Event.Single(
            id = "1",
            date = LocalDate.of(2025, 9, 2),
            title = "Math",
            shortTitle = "M",
            subTitle = subTitle,
            timeSpan = TimeSpan(LocalTime.of(9, 0), LocalTime.of(11, 0)),
            textColor = 0xFFFFFFFF.toInt(),
            backgroundColor = 0xFF0000FF.toInt(),
        )

    private fun setEntry(
        eventContent: EventContent,
        event: Event.Single = event(),
        eventConfig: EventConfig = EventConfig(showTimeStart = false, showTimeEnd = false, eventSpacingDp = 0),
    ) {
        composeTestRule.setContent {
            MaterialTheme(colorScheme = lightColorScheme(surface = surface)) {
                // Red backdrop standing in for the grid: no style may let it show through.
                Box(Modifier.fillMaxSize().background(backdrop)) {
                    EventCompose(
                        event = event,
                        scalingFactor = 1f,
                        eventConfig = eventConfig,
                        startTime = LocalTime.of(8, 0),
                        columnWidth = 120.dp,
                        eventLayout = EventOverlapCalculator.EventLayout(widthFraction = 1f, offsetFraction = 0f, overlapGroup = 0),
                        locale = Locale.GERMANY,
                        eventContent = eventContent,
                    )
                }
            }
        }
    }

    private fun entryPixels(): PixelMap = composeTestRule.onNodeWithTag("EventView_1").captureToImage().toPixelMap()

    private fun PixelMap.leftEdge(): Color = this[2, height / 2]

    private fun PixelMap.emptyInterior(): Color = this[width / 2, height * 4 / 5]

    private fun assertShowsFields(eventContent: EventContent) {
        setEntry(eventContent, event(subTitle = "Room 1"), EventConfig())
        listOf("EventTitle_1", "EventTime_1", "EventTimeEnd_1", "EventLocation_1").forEach { tag ->
            composeTestRule.onNodeWithTag(tag, useUnmergedTree = true).assertIsDisplayed()
        }
    }

    @Test
    fun shouldShowAllFittingFieldsWhenStyleIsFilled() = assertShowsFields { FilledEventContent(it) }

    @Test
    fun shouldShowAllFittingFieldsWhenStyleIsTinted() = assertShowsFields { TintedEventContent(it) }

    @Test
    fun shouldShowAllFittingFieldsWhenStyleIsOutlined() = assertShowsFields { OutlinedEventContent(it) }

    @Test
    fun shouldFillWithEventColorWhenStyleIsFilled() {
        setEntry({ FilledEventContent(it) })

        val pixels = entryPixels()
        assertEquals(eventColor, pixels.leftEdge())
        assertEquals(eventColor, pixels.emptyInterior())
    }

    @Test
    fun shouldDrawAccentBarOnTintWhenStyleIsTinted() {
        setEntry({ TintedEventContent(it) })

        val pixels = entryPixels()
        assertEquals(eventColor, pixels.leftEdge())
        val interior = pixels.emptyInterior()
        assertNotEquals(eventColor, interior)
        assertNotEquals(surface, interior)
        // Blue tint over the opaque surface: red and green reduced equally, so the backdrop doesn't shine through.
        assertEquals(1f, interior.blue, 0.03f)
        assertEquals(0.8f, interior.red, 0.05f)
        assertEquals(interior.green, interior.red, 0.01f)
    }

    @Test
    fun shouldDrawBorderOnSurfaceWhenStyleIsOutlined() {
        setEntry({ OutlinedEventContent(it) })

        val pixels = entryPixels()
        assertEquals(eventColor, pixels.leftEdge())
        assertEquals(surface, pixels.emptyInterior())
    }
}
