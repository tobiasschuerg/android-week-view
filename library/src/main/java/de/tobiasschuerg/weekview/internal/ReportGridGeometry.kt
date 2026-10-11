package de.tobiasschuerg.weekview.internal

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Density
import de.tobiasschuerg.weekview.WeekViewState
import de.tobiasschuerg.weekview.internal.layout.GridGeometry

/**
 * Hands the grid's geometry to [state] while the grid is placed. Placement runs after the scroll
 * container measured the new content height and before it scrolls the content into position,
 * so a scroll set from here (e.g. to keep a pinch anchored) shows in the same frame.
 */
internal fun Modifier.reportGridGeometry(
    state: WeekViewState,
    geometry: Density.() -> GridGeometry,
): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            state.onGridLaidOut(geometry())
            placeable.place(0, 0)
        }
    }
