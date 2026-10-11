package de.tobiasschuerg.weekview.content

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tobiasschuerg.weekview.EventConfig
import de.tobiasschuerg.weekview.internal.EventCompose
import de.tobiasschuerg.weekview.internal.layout.EventOverlapCalculator
import de.tobiasschuerg.weekview.model.Event
import de.tobiasschuerg.weekview.model.TimeSpan
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

/** Where the fields of a tall entry sit: below a start time at the top, otherwise centred. */
@RunWith(AndroidJUnit4::class)
class EventFieldsAlignmentTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    // Two hours at scaling factor 1 -> a 120 dp entry, far more than its fields need.
    private val event =
        Event.Single(
            id = "1",
            date = LocalDate.of(2025, 9, 2),
            title = "Math",
            shortTitle = "Math",
            timeSpan = TimeSpan(LocalTime.of(9, 0), LocalTime.of(11, 0)),
            textColor = Color.White,
            backgroundColor = Color.Blue,
        )

    private fun setEvent(eventConfig: EventConfig) {
        composeTestRule.setContent {
            MaterialTheme {
                Box(Modifier.fillMaxSize()) {
                    EventCompose(
                        event = event,
                        scalingFactor = 1f,
                        eventConfig = eventConfig,
                        startTime = LocalTime.of(9, 0),
                        columnWidth = 120.dp,
                        eventLayout = EventOverlapCalculator.EventLayout(widthFraction = 1f, offsetFraction = 0f, overlapGroup = 0),
                        locale = Locale.GERMANY,
                    )
                }
            }
        }
    }

    @Test
    fun shouldCenterTheFieldsVerticallyWhenNoTimesAreShown() {
        setEvent(EventConfig(showTimeStart = false, showTimeEnd = false))

        val entry = composeTestRule.onNodeWithTag("EventView_1").getUnclippedBoundsInRoot()
        val title = composeTestRule.onNodeWithTag("EventTitle_1", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val entryCenter = (entry.top + entry.bottom) / 2
        val titleCenter = (title.top + title.bottom) / 2
        assertEquals(entryCenter.value, titleCenter.value, 1f)
    }

    @Test
    fun shouldKeepTheFieldsAtTheTopWhenTheStartTimeIsShownAboveThem() {
        setEvent(EventConfig())

        val entry = composeTestRule.onNodeWithTag("EventView_1").getUnclippedBoundsInRoot()
        val title = composeTestRule.onNodeWithTag("EventTitle_1", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertTrue("title starts ${title.top - entry.top} below the entry top", title.top - entry.top < 25.dp)
    }
}
