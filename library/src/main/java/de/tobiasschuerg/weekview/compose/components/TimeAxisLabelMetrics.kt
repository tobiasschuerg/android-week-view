package de.tobiasschuerg.weekview.compose.components

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp

/**
 * Measured sizes of the time axis labels, so the gutter and the current-time pill are built from the
 * text sizes actually rendered.
 */
@Immutable
internal data class TimeAxisLabelMetrics(
    /** Width of the gutter: the widest clock time plus the padding the pill draws around it. */
    val axisWidth: Dp,
    /** Height of the current-time pill, which is centered on the indicator line. */
    val pillHeight: Dp,
)
