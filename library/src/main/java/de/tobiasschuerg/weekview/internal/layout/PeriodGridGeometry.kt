package de.tobiasschuerg.weekview.internal.layout

import androidx.compose.ui.unit.dp

/** Geometry of a period grid, in px for [density] (px per dp). */
internal data class PeriodGridGeometry(
    val layout: PeriodGridLayout,
    val density: Float,
) : GridGeometry {
    override fun minuteAt(contentY: Float): Float = layout.minuteAt((contentY / density).dp)

    override fun yOf(minute: Float): Float = layout.yOf(minute).value * density
}
