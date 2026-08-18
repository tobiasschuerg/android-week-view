package de.tobiasschuerg.weekview.data

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class WeekViewConfigTest {
    @Test
    fun `scaling factor below minimum is rejected`() {
        assertThrows<IllegalArgumentException> {
            WeekViewConfig(
                scalingFactor = 0.25f,
                minScalingFactor = 0.5f,
            )
        }
    }

    @Test
    fun `scaling factor above maximum is rejected`() {
        assertThrows<IllegalArgumentException> {
            WeekViewConfig(
                scalingFactor = 2.5f,
                maxScalingFactor = 2f,
            )
        }
    }
}
