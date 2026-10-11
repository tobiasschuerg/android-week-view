package de.tobiasschuerg.weekview.model

import androidx.compose.runtime.Immutable

/**
 * One row of a [de.tobiasschuerg.weekview.WeekLayout.Periods] grid: a numbered lesson ("1", "2", ...)
 * or, with [isBreak], a break between lessons.
 *
 * Lessons all get the same height, whatever their duration. Breaks are thin rows that show their
 * [label] across the week and can't be clicked when empty.
 */
@Immutable
public data class Period(
    /** Shown in the axis for a lesson ("1", "1."), across the grid for a break ("Lunch"). */
    val label: String,
    val timeSpan: TimeSpan,
    val isBreak: Boolean = false,
)
