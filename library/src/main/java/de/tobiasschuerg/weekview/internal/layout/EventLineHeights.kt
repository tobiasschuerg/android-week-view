package de.tobiasschuerg.weekview.internal.layout

import androidx.compose.ui.unit.Dp

/** Line heights (already scaled by the user's font scale) of the text fields inside an event entry. */
internal data class EventLineHeights(
    /** Regular time label, used when start and end can be stacked with the title. */
    val time: Dp,
    /** Smaller time label, used when the labels have to sit in the corners beside the title. */
    val compactTime: Dp,
    val title: Dp,
    /** Title at its minimum font size; corner labels must leave at least this much room for it. */
    val minTitle: Dp,
    val location: Dp,
    val teacher: Dp,
)
