package de.tobiasschuerg.weekview.internal.layout

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalTime

class GridEndTest {
    @Test
    fun `should close the grid on the end itself when the last event ends on the hour`() {
        assertEquals(LocalTime.of(15, 0), gridEndFor(LocalTime.of(15, 0)))
    }

    @Test
    fun `should close the grid on the next hour when the last event ends within an hour`() {
        assertEquals(LocalTime.of(16, 0), gridEndFor(LocalTime.of(15, 10)))
        assertEquals(LocalTime.of(16, 0), gridEndFor(LocalTime.of(15, 59, 59)))
    }

    @Test
    fun `should close the grid at midnight when the last event ends after 23`() {
        assertEquals(LocalTime.MAX, gridEndFor(LocalTime.of(23, 30)))
        assertEquals(LocalTime.MAX, gridEndFor(LocalTime.MAX))
    }

    @Test
    fun `should keep a full hour when rounding a time already on the hour`() {
        assertEquals(LocalTime.of(8, 0), LocalTime.of(8, 0).ceilToHour())
        assertEquals(LocalTime.of(9, 0), LocalTime.of(8, 1).ceilToHour())
    }
}
