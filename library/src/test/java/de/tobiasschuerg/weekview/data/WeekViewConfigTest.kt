package de.tobiasschuerg.weekview.data

import org.junit.Test

class WeekViewConfigTest {
    @Test(expected = IllegalArgumentException::class)
    fun `scaling factor below minimum is rejected`() {
        WeekViewConfig(
            scalingFactor = 0.25f,
            minScalingFactor = 0.5f,
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `scaling factor above maximum is rejected`() {
        WeekViewConfig(
            scalingFactor = 2.5f,
            maxScalingFactor = 2f,
        )
    }
}
