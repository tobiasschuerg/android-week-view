package de.tobiasschuerg.weekview.util

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Decides which fields of an event entry are shown based on the vertical room it has.
 *
 * The title is always shown (shrunk to fit if needed). Start and end time come next:
 * stacked with the title when the entry is tall enough, otherwise as smaller labels in
 * the corners so they never need to share a line with each other (a combined
 * "start - end" line does not fit a five-day phone column). Location, teacher and lower
 * text are then added in that order as long as they still fit. Working with the actual
 * line heights rather than fixed dp thresholds keeps the decision correct under any
 * system font scale.
 */
object EventFieldLayout {
    fun resolve(
        availableHeight: Dp,
        lines: EventLineHeights,
        hasStartTime: Boolean,
        hasEndTime: Boolean,
        hasLocation: Boolean,
        hasTeacher: Boolean,
        hasLowerText: Boolean,
    ): EventFieldVisibility {
        val wantedTimeLabels = listOf(hasStartTime, hasEndTime).count { it }
        val stackedHeight = lines.title + lines.time * wantedTimeLabels
        val mode =
            when {
                wantedTimeLabels == 0 || availableHeight >= stackedHeight -> TimeLabelMode.STACKED
                availableHeight >= lines.compactTime * 2 -> TimeLabelMode.CORNERS
                else -> TimeLabelMode.NONE
            }
        // Corner labels always block both bands so the centred title stays clear of them.
        val timeHeight =
            when (mode) {
                TimeLabelMode.STACKED -> lines.time * wantedTimeLabels
                TimeLabelMode.CORNERS -> lines.compactTime * 2
                TimeLabelMode.NONE -> 0.dp
            }

        val titleHeight = (availableHeight - timeHeight).coerceIn(0.dp, lines.title)
        var remaining = availableHeight - titleHeight - timeHeight

        fun claim(
            wanted: Boolean,
            height: Dp,
        ): Boolean {
            if (!wanted || remaining < height) return false
            remaining -= height
            return true
        }

        val showLocation = claim(hasLocation, lines.location)
        val showTeacher = claim(hasTeacher, lines.teacher)
        val showLowerText = claim(hasLowerText, lines.teacher)

        return EventFieldVisibility(
            timeLabelMode = mode,
            showStartTime = mode != TimeLabelMode.NONE && hasStartTime,
            showEndTime = mode != TimeLabelMode.NONE && hasEndTime,
            showLocation = showLocation,
            showTeacher = showTeacher,
            showLowerText = showLowerText,
            twoLineTitle = remaining >= lines.title,
            titleHeight = titleHeight,
        )
    }

    /**
     * Vertical padding inside an entry on each side. Dropped entirely once the entry is too
     * low for a full title line so every dp goes to the (shrunk) title.
     */
    fun verticalPadding(
        innerHeight: Dp,
        lines: EventLineHeights,
    ): Dp = if (innerHeight >= lines.title + DEFAULT_VERTICAL_PADDING * 2) DEFAULT_VERTICAL_PADDING else 0.dp

    private val DEFAULT_VERTICAL_PADDING: Dp = 2.dp
}
