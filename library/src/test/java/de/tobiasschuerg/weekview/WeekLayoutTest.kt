package de.tobiasschuerg.weekview

import androidx.compose.ui.unit.dp
import de.tobiasschuerg.weekview.model.Period
import de.tobiasschuerg.weekview.model.TimeSpan
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.time.LocalTime

class WeekLayoutTest {
    private val first = Period("1", TimeSpan(LocalTime.of(8, 0), LocalTime.of(8, 45)))
    private val second = Period("2", TimeSpan(LocalTime.of(8, 50), LocalTime.of(9, 35)))

    @Test
    fun `should accept sorted periods when they have gaps or touch`() {
        val touching = Period("3", TimeSpan(LocalTime.of(9, 35), LocalTime.of(10, 20)))

        assertDoesNotThrow { WeekLayout.Periods(listOf(first, second, touching)) }
    }

    @Test
    fun `should reject periods when they are not sorted`() {
        assertThrows<IllegalArgumentException> { WeekLayout.Periods(listOf(second, first)) }
    }

    @Test
    fun `should reject periods when two of them overlap`() {
        val overlapping = Period("2", TimeSpan(LocalTime.of(8, 30), LocalTime.of(9, 15)))

        assertThrows<IllegalArgumentException> { WeekLayout.Periods(listOf(first, overlapping)) }
    }

    @Test
    fun `should reject periods when there are no lessons`() {
        val lunch = Period("Lunch", TimeSpan(LocalTime.of(12, 0), LocalTime.of(13, 0)), isBreak = true)

        assertThrows<IllegalArgumentException> { WeekLayout.Periods(emptyList()) }
        assertThrows<IllegalArgumentException> { WeekLayout.Periods(listOf(lunch)) }
    }

    @Test
    fun `should reject a row height when it is not positive`() {
        assertThrows<IllegalArgumentException> { WeekLayout.Periods(listOf(first), periodHeight = 0.dp) }
        assertThrows<IllegalArgumentException> { WeekLayout.Periods(listOf(first), breakHeight = (-1).dp) }
    }

    @Test
    fun `should name the offending periods when they overlap`() {
        val overlapping = Period("2b", TimeSpan(LocalTime.of(8, 30), LocalTime.of(9, 15)))

        val error = assertThrows<IllegalArgumentException> { WeekLayout.Periods(listOf(first, overlapping)) }

        assertEquals(true, error.message?.contains("1 (08:00-08:45) is followed by 2b (08:30-09:15)"))
    }
}
