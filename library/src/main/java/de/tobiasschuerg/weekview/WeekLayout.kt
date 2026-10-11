package de.tobiasschuerg.weekview

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.tobiasschuerg.weekview.model.Period

/** How the week view arranges the day: on a continuous time axis, or as a grid of fixed periods. */
public sealed interface WeekLayout {
    /** A calendar: rows are hours and every entry is as tall as it lasts. */
    public data object Timeline : WeekLayout

    /**
     * A school timetable: one row per [Period], all lessons equally tall whatever they last.
     *
     * A timed event fills every row its time span overlaps, so a double lesson covers two rows and a
     * lesson starting a few minutes late still fills its row. An event that overlaps no row (e.g. one
     * in a gap between periods) goes to the nearest row instead of disappearing.
     */
    @Immutable
    public data class Periods(
        /** The rows from top to bottom: sorted by time and not overlapping. Gaps between them are allowed. */
        val periods: List<Period>,
        /** Height of a lesson row at scaling factor 1; pinch-zoom scales it, and rows grow to fill the viewport. */
        val periodHeight: Dp = 56.dp,
        /** Height of a break row; it doesn't zoom. */
        val breakHeight: Dp = 24.dp,
        /** Show each lesson's start and end time below its label in the axis. */
        val showTimes: Boolean = true,
    ) : WeekLayout {
        init {
            require(periods.any { !it.isBreak }) { "Periods need at least one lesson, but got $periods" }
            require(periodHeight > 0.dp) { "periodHeight must be positive, but was $periodHeight" }
            require(breakHeight > 0.dp) { "breakHeight must be positive, but was $breakHeight" }
            periods.zipWithNext().forEach { (previous, next) ->
                require(previous.timeSpan.endExclusive <= next.timeSpan.start) {
                    "Periods must be sorted and must not overlap, but ${previous.label} " +
                        "(${previous.timeSpan.start}-${previous.timeSpan.endExclusive}) is followed by " +
                        "${next.label} (${next.timeSpan.start}-${next.timeSpan.endExclusive})"
                }
            }
        }
    }
}
