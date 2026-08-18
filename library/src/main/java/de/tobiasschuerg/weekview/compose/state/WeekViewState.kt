package de.tobiasschuerg.weekview.compose.state

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Stable
class WeekViewState internal constructor(
    initialScalingFactor: Float,
    val scrollState: ScrollState,
) {
    var scalingFactor by mutableFloatStateOf(initialScalingFactor)
        private set

    private var lastConfiguredScalingFactor by mutableFloatStateOf(initialScalingFactor)

    internal fun syncConfiguredScalingFactor(configuredScalingFactor: Float) {
        if (configuredScalingFactor != lastConfiguredScalingFactor) {
            scalingFactor = configuredScalingFactor
            lastConfiguredScalingFactor = configuredScalingFactor
        }
    }

    internal fun applyZoom(
        zoom: Float,
        minScalingFactor: Float,
        maxScalingFactor: Float,
    ): Float? {
        val newScalingFactor = (scalingFactor * zoom).coerceIn(minScalingFactor, maxScalingFactor)
        if (newScalingFactor == scalingFactor) return null

        scalingFactor = newScalingFactor
        return newScalingFactor
    }
}

@Composable
fun rememberWeekViewState(initialScalingFactor: Float = 1f): WeekViewState {
    val scrollState = rememberScrollState()
    return remember {
        WeekViewState(
            initialScalingFactor = initialScalingFactor,
            scrollState = scrollState,
        )
    }
}
