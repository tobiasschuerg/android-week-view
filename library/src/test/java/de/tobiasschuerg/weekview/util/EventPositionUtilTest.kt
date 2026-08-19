package de.tobiasschuerg.weekview.util

import androidx.compose.ui.unit.dp
import de.tobiasschuerg.weekview.data.Event
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

        val (topOffset, eventHeight) =
            EventPositionUtil.calculateVerticalOffsets(
                event = event,
                startTime = startTime,
                scalingFactor = scalingFactor,
            )

        assertEquals(0.dp, topOffset)
        assertEquals(60.dp, eventHeight)
    }

    @Test
    fun `allowsTwoLineTitle is false below the minimum height`() {
        assertEquals(false, EventPositionUtil.allowsTwoLineTitle(71.dp))
    }

    @Test
    fun `allowsTwoLineTitle is true at and above the minimum height`() {
        assertEquals(true, EventPositionUtil.allowsTwoLineTitle(72.dp))
        assertEquals(true, EventPositionUtil.allowsTwoLineTitle(120.dp))
    }

    @Test
    fun `allowsSplitTimeLabels is false below the minimum height`() {
        assertEquals(false, EventPositionUtil.allowsSplitTimeLabels(89.dp))
    }

    @Test
    fun `allowsSplitTimeLabels is true at and above the minimum height`() {
        assertEquals(true, EventPositionUtil.allowsSplitTimeLabels(90.dp))
        assertEquals(true, EventPositionUtil.allowsSplitTimeLabels(150.dp))
    }

    @Test
    fun `field priority thresholds are ordered time below location below teacher`() {
        // Below all thresholds: only the name (unconditional) would be shown.
        assertEquals(false, EventPositionUtil.allowsTimeField(23.dp))
        assertEquals(false, EventPositionUtil.allowsLocationField(23.dp))
        assertEquals(false, EventPositionUtil.allowsTeacherField(23.dp))

        // Enough room for time only.
        assertEquals(true, EventPositionUtil.allowsTimeField(24.dp))
        assertEquals(false, EventPositionUtil.allowsLocationField(24.dp))
        assertEquals(false, EventPositionUtil.allowsTeacherField(24.dp))

        // Enough room for time + location.
        assertEquals(true, EventPositionUtil.allowsTimeField(40.dp))
        assertEquals(true, EventPositionUtil.allowsLocationField(40.dp))
        assertEquals(false, EventPositionUtil.allowsTeacherField(40.dp))

        // Enough room for all three.
        assertEquals(true, EventPositionUtil.allowsTimeField(56.dp))
        assertEquals(true, EventPositionUtil.allowsLocationField(56.dp))
        assertEquals(true, EventPositionUtil.allowsTeacherField(56.dp))
    }
}
