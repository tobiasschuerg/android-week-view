package de.tobiasschuerg.weekview.util

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import de.tobiasschuerg.weekview.data.Event
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

class EventPositionUtilTest {
    @Test
    fun `event at column start should have zero offset`() {
        val event =
            Event.Single(
                id = 1L,
                date = LocalDate.of(2025, 9, 2),
                title = "Test Event",
                shortTitle = "Test",
                timeSpan = TimeSpan.of(LocalTime.of(8, 0), Duration.ofMinutes(60)),
                backgroundColor = 0,
                textColor = 0,
            )
        val startTime = LocalTime.of(8, 0)
        val scalingFactor = 1f
        val density = Density(1f)

        val (topOffset, eventHeight) =
            EventPositionUtil.calculateVerticalOffsets(
                event = event,
                startTime = startTime,
                scalingFactor = scalingFactor,
                density = density,
            )

        assertEquals(0.dp, topOffset)
        assertEquals(60.dp, eventHeight)
    }
}
