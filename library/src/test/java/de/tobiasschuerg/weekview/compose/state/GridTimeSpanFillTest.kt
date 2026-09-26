package de.tobiasschuerg.weekview.compose.state

import androidx.compose.ui.unit.dp
import de.tobiasschuerg.weekview.util.TimeSpan
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import java.time.LocalTime

class GridTimeSpanFillTest {
    private val rowHeight = 60.dp
    private val span = TimeSpan(LocalTime.of(8, 0), LocalTime.of(12, 0))

    @Test
    fun `should return the same span when the grid already fills the minimum height`() {
        assertSame(span, span.extendToFillHeight(rowHeight, 240.dp))
        assertSame(span, span.extendToFillHeight(rowHeight, 100.dp))
    }

    @Test
    fun `should return the same span when the minimum height is zero`() {
        assertSame(span, span.extendToFillHeight(rowHeight, 0.dp))
    }

    @Test
    fun `should append whole hours after the end when more height is needed`() {
        val result = span.extendToFillHeight(rowHeight, 400.dp)

        assertEquals(TimeSpan(LocalTime.of(8, 0), LocalTime.of(15, 0)), result)
    }

    @Test
    fun `should round a partial last hour up before appending further hours`() {
        val result = TimeSpan(LocalTime.of(8, 0), LocalTime.of(11, 30)).extendToFillHeight(rowHeight, 250.dp)

        assertEquals(TimeSpan(LocalTime.of(8, 0), LocalTime.of(13, 0)), result)
    }

    @Test
    fun `should prepend hours before the start once the end has reached midnight`() {
        val result = TimeSpan(LocalTime.of(20, 0), LocalTime.of(22, 0)).extendToFillHeight(rowHeight, 350.dp)

        assertEquals(TimeSpan(LocalTime.of(18, 0), LocalTime.MAX), result)
    }

    @Test
    fun `should stop at a full day when the minimum height cannot be reached`() {
        val result = span.extendToFillHeight(rowHeight, 10_000.dp)

        assertEquals(TimeSpan(LocalTime.MIDNIGHT, LocalTime.MAX), result)
    }

    @Test
    fun `should scale the number of added hours with the row height`() {
        val result = span.extendToFillHeight(120.dp, 720.dp)

        assertEquals(TimeSpan(LocalTime.of(8, 0), LocalTime.of(14, 0)), result)
    }

    @Test
    fun `should return the same span when the row height is not positive`() {
        assertSame(span, span.extendToFillHeight(0.dp, 400.dp))
    }

    @Test
    fun `should treat the midnight end as just under 24 hours in grid height`() {
        assertEquals(60f * 23 + 59, gridHeightDp(LocalTime.MIDNIGHT, LocalTime.MAX, rowHeight).value, 0.01f)
        assertEquals(240f, gridHeightDp(LocalTime.of(8, 0), LocalTime.of(12, 0), rowHeight).value, 0.01f)
    }
}
