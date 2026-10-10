package de.tobiasschuerg.weekview

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Stable
public class WeekViewState internal constructor(
    initialScalingFactor: Float,
    public val scrollState: ScrollState,
) {
    public var scalingFactor: Float by mutableFloatStateOf(initialScalingFactor)
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

    internal companion object {
        /** Keeps the zoom level across configuration changes and process death; the scroll state saves itself. */
        fun saver(scrollState: ScrollState): Saver<WeekViewState, *> =
            Saver(
                save = { floatArrayOf(it.scalingFactor, it.lastConfiguredScalingFactor) },
                restore = { saved ->
                    WeekViewState(initialScalingFactor = saved[1], scrollState = scrollState).also {
                        it.scalingFactor = saved[0]
                    }
                },
            )
    }
}

@Composable
public fun rememberWeekViewState(initialScalingFactor: Float = 1f): WeekViewState {
    val scrollState = rememberScrollState()
    return rememberSaveable(saver = WeekViewState.saver(scrollState)) {
        WeekViewState(
            initialScalingFactor = initialScalingFactor,
            scrollState = scrollState,
        )
    }
}
