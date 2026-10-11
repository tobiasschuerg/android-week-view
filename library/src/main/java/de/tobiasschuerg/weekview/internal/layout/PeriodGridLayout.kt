package de.tobiasschuerg.weekview.internal.layout

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.tobiasschuerg.weekview.model.Period
import de.tobiasschuerg.weekview.model.TimeSpan
import java.time.LocalTime

/**
 * Where the rows of a period grid go: lessons all equally tall, breaks thinner, stacked without gaps
 * whatever time lies between them. Maps times to offsets row by row, so a time inside a row lands
 * proportionally within it and a time between two rows lands on the line between them.
 */
@Immutable
internal data class PeriodGridLayout(
    val periods: List<Period>,
    val tops: List<Dp>,
    val heights: List<Dp>,
) {
    val height: Dp = if (periods.isEmpty()) 0.dp else tops.last() + heights.last()

    /**
     * The rows [span] fills: every row it overlaps, or the nearest row when it overlaps none, so that
     * an event never disappears from the grid.
     */
    fun rowsOf(span: TimeSpan): IntRange {
        val overlapped = periods.indices.filter { periods[it].timeSpan.overlaps(span) }
        if (overlapped.isNotEmpty()) return overlapped.first()..overlapped.last()
        val nearest = periods.indices.minBy { periods[it].timeSpan.distanceTo(span) }
        return nearest..nearest
    }

    /** Offset of [minute] (minutes after midnight) from the top of the grid. */
    fun yOf(minute: Float): Dp {
        periods.forEachIndexed { index, period ->
            val start = period.timeSpan.start.minuteOfDay()
            val end = period.timeSpan.endExclusive.minuteOfDay()
            if (minute < start) return tops[index]
            if (minute < end) return tops[index] + heights[index] * ((minute - start) / (end - start))
        }
        return height
    }

    /** Time at [y] from the top of the grid, in minutes after midnight; the inverse of [yOf] within the rows. */
    fun minuteAt(y: Dp): Float {
        if (periods.isEmpty()) return 0f
        if (y <= 0.dp) return periods.first().timeSpan.start.minuteOfDay()
        periods.forEachIndexed { index, period ->
            if (y < tops[index] + heights[index]) {
                val start = period.timeSpan.start.minuteOfDay()
                val end = period.timeSpan.endExclusive.minuteOfDay()
                return start + (end - start) * ((y - tops[index]) / heights[index])
            }
        }
        return periods.last().timeSpan.endExclusive.minuteOfDay()
    }

    companion object {
        /**
         * Lessons are [periodHeight] tall, breaks [breakHeight]. Lessons grow equally until the grid is at
         * least [minHeight] tall, so a short day fills the viewport instead of leaving space below it.
         */
        fun of(
            periods: List<Period>,
            periodHeight: Dp,
            breakHeight: Dp,
            minHeight: Dp = 0.dp,
        ): PeriodGridLayout {
            val lessonCount = periods.count { !it.isBreak }
            val breaksHeight = breakHeight * (periods.size - lessonCount)
            val fillingHeight = if (lessonCount > 0) (minHeight - breaksHeight) / lessonCount else 0.dp
            val lessonHeight = maxOf(periodHeight, fillingHeight)
            val heights = periods.map { if (it.isBreak) breakHeight else lessonHeight }
            val tops = heights.runningFold(0.dp) { top, height -> top + height }.dropLast(1)
            return PeriodGridLayout(periods, tops, heights)
        }
    }
}

private fun LocalTime.minuteOfDay(): Float = toSecondOfDay() / SECONDS_PER_MINUTE

private fun TimeSpan.overlaps(other: TimeSpan): Boolean = start < other.endExclusive && other.start < endExclusive

/** Minutes between two spans that don't overlap. */
private fun TimeSpan.distanceTo(other: TimeSpan): Float =
    if (endExclusive <= other.start) {
        other.start.minuteOfDay() - endExclusive.minuteOfDay()
    } else {
        start.minuteOfDay() - other.endExclusive.minuteOfDay()
    }

private const val SECONDS_PER_MINUTE = 60f
