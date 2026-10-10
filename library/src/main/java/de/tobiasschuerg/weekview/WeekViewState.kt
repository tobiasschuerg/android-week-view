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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import de.tobiasschuerg.weekview.internal.layout.GridGeometry
import de.tobiasschuerg.weekview.internal.layout.ZoomAnchor
import java.time.LocalTime
import kotlin.math.roundToInt

@Stable
public class WeekViewState internal constructor(
    initialScalingFactor: Float,
    public val scrollState: ScrollState,
    initialTime: LocalTime? = null,
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

    /** Layout of the scrollable grid, as last placed; null until the grid was laid out once. */
    private var gridGeometry: GridGeometry? = null

    /** Time to scroll to once the grid is laid out. */
    private var pendingScrollTime: LocalTime? = initialTime

    /** Time kept under the fingers during an ongoing pinch. */
    private var zoomAnchor: ZoomAnchor? = null

    /** Whether a zoom step still waits for its grid layout. */
    private var zoomAwaitsLayout = false

    /** Whether the pinch ended; the anchor is dropped once the last zoom step is laid out. */
    private var zoomEnded = false

    /** Coordinates the zoom gesture reports its pointer positions in. */
    internal var gestureCoordinates: LayoutCoordinates? = null

    /** Coordinates of the scrollable grid's viewport. */
    internal var viewportCoordinates: LayoutCoordinates? = null

    /**
     * Scales by [zoom] within the given bounds. With a [focus] (in [gestureCoordinates]), the time
     * under it stays in place: the next grid layout scrolls it back there.
     */
    internal fun applyZoom(
        zoom: Float,
        minScalingFactor: Float,
        maxScalingFactor: Float,
        focus: Offset? = null,
    ): Float? {
        val newScalingFactor = (scalingFactor * zoom).coerceIn(minScalingFactor, maxScalingFactor)
        if (newScalingFactor == scalingFactor) return null

        focus?.let(::anchorZoomAt)
        zoomAwaitsLayout = true
        zoomEnded = false
        scalingFactor = newScalingFactor
        return newScalingFactor
    }

    /**
     * Scrolls so that [time] is at the top of the grid, or as close as the grid allows.
     * Before the grid is laid out, the scroll is applied with the first layout.
     */
    public suspend fun scrollToTime(time: LocalTime) {
        val geometry = gridGeometry ?: return run { pendingScrollTime = time }
        scrollState.scrollTo(geometry.yOf(time.minuteOfDay()).roundToInt())
    }

    /** Like [scrollToTime], but animated. */
    public suspend fun animateScrollToTime(time: LocalTime) {
        val geometry = gridGeometry ?: return run { pendingScrollTime = time }
        scrollState.animateScrollTo(geometry.yOf(time.minuteOfDay()).roundToInt())
    }

    /** Ends the pinch, so later layouts no longer pull the anchored time under the fingers. */
    internal fun endZoom() {
        if (zoomAwaitsLayout) zoomEnded = true else zoomAnchor = null
    }

    /** Called while the grid is placed, before its content is scrolled into position. */
    internal fun onGridLaidOut(geometry: GridGeometry) {
        gridGeometry = geometry
        pendingScrollTime?.let {
            pendingScrollTime = null
            scrollTo(geometry.yOf(it.minuteOfDay()))
        }
        zoomAnchor?.let { scrollTo(geometry.yOf(it.minute) - it.viewportY) }
        zoomAwaitsLayout = false
        if (zoomEnded) {
            zoomAnchor = null
            zoomEnded = false
        }
    }

    private fun anchorZoomAt(focus: Offset) {
        val geometry = gridGeometry ?: return
        val viewportY = viewportY(focus) ?: return
        // Keep the time picked when the pinch started; only follow the fingers as they move.
        zoomAnchor = zoomAnchor?.copy(viewportY = viewportY)
            ?: ZoomAnchor(minute = geometry.minuteAt(scrollState.value + viewportY), viewportY = viewportY)
    }

    private fun viewportY(focus: Offset): Float? {
        val gesture = gestureCoordinates?.takeIf { it.isAttached } ?: return null
        val viewport = viewportCoordinates?.takeIf { it.isAttached } ?: return null
        return viewport.localPositionOf(gesture, focus).y
    }

    private fun LocalTime.minuteOfDay(): Float = toSecondOfDay() / SECONDS_PER_MINUTE

    private fun scrollTo(y: Float) {
        scrollState.dispatchRawDelta(y - scrollState.value)
    }

    internal companion object {
        private const val SECONDS_PER_MINUTE = 60f

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

/**
 * Creates a [WeekViewState] that survives configuration changes and process death.
 *
 * @param initialScalingFactor zoom level to start with.
 * @param initialTime time to show at the top when the week view first appears, e.g. the current
 *   time or the first lesson; null starts at the top of the grid. Ignored when the state is restored,
 *   which keeps the restored scroll position instead.
 */
@Composable
public fun rememberWeekViewState(
    initialScalingFactor: Float = 1f,
    initialTime: LocalTime? = null,
): WeekViewState {
    val scrollState = rememberScrollState()
    return rememberSaveable(saver = WeekViewState.saver(scrollState)) {
        WeekViewState(
            initialScalingFactor = initialScalingFactor,
            scrollState = scrollState,
            initialTime = initialTime,
        )
    }
}
