package de.tobiasschuerg.weekview.util

import org.junit.Assert.assertEquals
import org.junit.Test
import org.threeten.bp.Duration
import org.threeten.bp.LocalTime

/**
 * Created by Tobias Schrg on 11.02.2022.
 */
class TimeSpanTest {

    @Test
    fun testDuration() {
        val timeSpan = TimeSpan(
            start = LocalTime.of(10, 15),
            endExclusive = LocalTime.of(10, 45)
        )
        assertEquals(Duration.ofMinutes(30), timeSpan.duration)
    }

    @Test
    fun testDuration2() {
        val timeSpan = TimeSpan.of(
            start = LocalTime.of(10, 15),
            duration = Duration.ofMinutes(30)
        )
        assertEquals(Duration.ofMinutes(30), timeSpan.duration)
    }
}
