package de.tobiasschuerg.weekview.util

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EventFieldLayoutTest {
    // Line heights at font scale 1.0 (11/9/14/12/10 sp).
    private val lines = EventLineHeights(time = 11.dp, compactTime = 9.dp, title = 14.dp, location = 12.dp, teacher = 10.dp)

    private fun resolve(
        availableHeight: Dp,
        hasStartTime: Boolean = true,
        hasEndTime: Boolean = true,
        hasLocation: Boolean = true,
        hasTeacher: Boolean = true,
        hasLowerText: Boolean = false,
    ) = EventFieldLayout.resolve(availableHeight, lines, hasStartTime, hasEndTime, hasLocation, hasTeacher, hasLowerText)

    @Test
    fun `should stack start and end time when title plus both labels fit`() {
        // 14 + 2 * 11 = 36
        val fields = resolve(36.dp)

        assertEquals(TimeLabelMode.STACKED, fields.timeLabelMode)
        assertTrue(fields.showStartTime)
        assertTrue(fields.showEndTime)
        assertFalse(fields.showLocation)
        assertEquals(14.dp, fields.titleHeight)
    }

    @Test
    fun `should use corner labels when stacking does not fit but two compact bands do`() {
        val fields = resolve(35.dp)

        assertEquals(TimeLabelMode.CORNERS, fields.timeLabelMode)
        assertTrue(fields.showStartTime)
        assertTrue(fields.showEndTime)
        assertFalse(fields.showLocation)
        // 35 - 2 * 9 = 17 >= 14, so the title keeps its full height
        assertEquals(14.dp, fields.titleHeight)
    }

    @Test
    fun `should shrink title in corner mode when it does not fit between the bands`() {
        // 24 - 2 * 9 = 6 left for the title
        val fields = resolve(24.dp)

        assertEquals(TimeLabelMode.CORNERS, fields.timeLabelMode)
        assertEquals(6.dp, fields.titleHeight)
    }

    @Test
    fun `should drop time labels when the entry is too low for two compact bands`() {
        val fields = resolve(17.dp)

        assertEquals(TimeLabelMode.NONE, fields.timeLabelMode)
        assertFalse(fields.showStartTime)
        assertFalse(fields.showEndTime)
        assertFalse(fields.showLocation)
        assertEquals(14.dp, fields.titleHeight)
    }

    @Test
    fun `should shrink title to the whole entry when even one title line does not fit`() {
        val fields = resolve(8.dp)

        assertEquals(TimeLabelMode.NONE, fields.timeLabelMode)
        assertEquals(8.dp, fields.titleHeight)
    }

    @Test
    fun `should stack a single time label when only one of start or end is enabled`() {
        // 14 + 11 = 25 fits, 14 + 22 would not
        val fields = resolve(25.dp, hasEndTime = false)

        assertEquals(TimeLabelMode.STACKED, fields.timeLabelMode)
        assertTrue(fields.showStartTime)
        assertFalse(fields.showEndTime)
    }

    @Test
    fun `should never show a disabled time label in corner mode`() {
        val fields = resolve(20.dp, hasStartTime = false)

        assertEquals(TimeLabelMode.CORNERS, fields.timeLabelMode)
        assertFalse(fields.showStartTime)
        assertTrue(fields.showEndTime)
    }

    @Test
    fun `should report stacked mode without labels when no time label is configured`() {
        val fields = resolve(20.dp, hasStartTime = false, hasEndTime = false)

        assertEquals(TimeLabelMode.STACKED, fields.timeLabelMode)
        assertFalse(fields.showStartTime)
        assertFalse(fields.showEndTime)
        assertFalse(fields.showLocation)
    }

    @Test
    fun `should add location then teacher then lower text as room grows`() {
        // stacked base 36, +12 location = 48, +10 teacher = 58, +10 lower text = 68
        val withLocation = resolve(48.dp, hasLowerText = true)
        assertTrue(withLocation.showLocation)
        assertFalse(withLocation.showTeacher)
        assertFalse(withLocation.showLowerText)

        val withTeacher = resolve(58.dp, hasLowerText = true)
        assertTrue(withTeacher.showTeacher)
        assertFalse(withTeacher.showLowerText)

        val withLowerText = resolve(68.dp, hasLowerText = true)
        assertTrue(withLowerText.showLowerText)
    }

    @Test
    fun `should skip a missing field without blocking lower-priority ones`() {
        val fields = resolve(46.dp, hasLocation = false)

        assertFalse(fields.showLocation)
        assertTrue(fields.showTeacher)
    }

    @Test
    fun `should allow a two-line title only once a whole extra title line is left over`() {
        // 36 stacked + 12 location + 10 teacher = 58; +14 = 72
        assertFalse(resolve(71.dp).twoLineTitle)
        assertTrue(resolve(72.dp).twoLineTitle)
    }

    @Test
    fun `should keep the vertical padding only while a full title line still fits`() {
        assertEquals(2.dp, EventFieldLayout.verticalPadding(18.dp, lines))
        assertEquals(0.dp, EventFieldLayout.verticalPadding(17.dp, lines))
    }
}
