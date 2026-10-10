package de.tobiasschuerg.weekview.internal.layout

import androidx.compose.ui.unit.dp
import de.tobiasschuerg.weekview.model.Event
import de.tobiasschuerg.weekview.model.TimeSpan
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

class EventPositionUtilTest {
    @Test
    fun `event at column start should have zero offset`() {
        val event =
            Event.Single(
                id = "1",
                date = LocalDate.of(2025, 9, 2),
                title = "Test Event",
                shortTitle = "Test",
                timeSpan = TimeSpan.of(LocalTime.of(8, 0), Duration.ofMinutes(60)),
                backgroundColor = 0,
                textColor = 0,
            )
        val startTime = LocalTime.of(8, 0)
        val scalingFactor = 1f

        val (topOffset, eventHeight) =
            EventPositionUtil.calculateVerticalOffsets(
                event = event,
                startTime = startTime,
                scalingFactor = scalingFactor,
            )

        assertEquals(0.dp, topOffset)
        assertEquals(60.dp, eventHeight)
    }
}
