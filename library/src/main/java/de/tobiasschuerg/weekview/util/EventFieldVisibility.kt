package de.tobiasschuerg.weekview.util

import androidx.compose.ui.unit.Dp

/** Which optional fields of an event entry fit into its box, see [EventFieldLayout.resolve]. */
internal data class EventFieldVisibility(
    val timeLabelMode: TimeLabelMode,
    val showStartTime: Boolean,
    val showEndTime: Boolean,
    val showLocation: Boolean,
    val showTeacher: Boolean,
    val showLowerText: Boolean,
    val twoLineTitle: Boolean,
    /** Height the title may use; smaller than its regular line height when the entry is too low, so the font is shrunk to it. */
    val titleHeight: Dp,
)
