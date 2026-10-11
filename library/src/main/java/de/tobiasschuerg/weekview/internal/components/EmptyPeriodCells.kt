package de.tobiasschuerg.weekview.internal.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import de.tobiasschuerg.weekview.content.EventCornerRadius
import de.tobiasschuerg.weekview.internal.layout.PeriodGridLayout
import de.tobiasschuerg.weekview.internal.time.toLocalString
import de.tobiasschuerg.weekview.internal.time.toShortDateStringWithoutYear
import de.tobiasschuerg.weekview.model.Event
import de.tobiasschuerg.weekview.model.Period
import java.time.LocalDate
import java.util.Locale

private val PLACEHOLDER_STROKE = 1.dp
private val PLACEHOLDER_DASH = 4.dp
private val PLACEHOLDER_GAP = 3.dp

/**
 * A dashed placeholder in every lesson row that has no entry on a day, the same size as an entry
 * would be there. With [onEmptyPeriodClick] the whole cell takes clicks. Break rows get none.
 */
@Composable
internal fun EmptyPeriodCells(
    layout: PeriodGridLayout,
    days: List<LocalDate>,
    events: List<Event.Single>,
    columnWidth: Dp,
    spacing: Dp,
    color: Color,
    locale: Locale,
    onEmptyPeriodClick: ((date: LocalDate, period: Period) -> Unit)?,
) {
    val occupied =
        remember(events, layout.periods) {
            events.flatMap { event -> layout.rowsOf(event.timeSpan).map { row -> event.date to row } }.toSet()
        }

    days.forEachIndexed { dayIndex, date ->
        layout.periods.forEachIndexed { row, period ->
            if (!period.isBreak && (date to row) !in occupied) {
                key(date, row) {
                    Box(
                        modifier =
                            Modifier
                                .offset(x = dayIndex * columnWidth, y = layout.tops[row])
                                .testTag("EmptyPeriod_${date}_$row")
                                .size(columnWidth, layout.heights[row])
                                .then(
                                    if (onEmptyPeriodClick == null) {
                                        Modifier
                                    } else {
                                        Modifier.clickable(role = Role.Button) { onEmptyPeriodClick(date, period) }.semantics {
                                            contentDescription = describe(date, period, locale)
                                        }
                                    },
                                ).padding(spacing)
                                .dashedOutline(color),
                    )
                }
            }
        }
    }
}

private fun describe(
    date: LocalDate,
    period: Period,
    locale: Locale,
): String =
    "${date.toShortDateStringWithoutYear(locale)}, ${period.label}, " +
        "${period.timeSpan.start.toLocalString(locale)} - ${period.timeSpan.endExclusive.toLocalString(locale)}"

/** A dashed rounded outline just inside the bounds, with the corners of an entry. */
private fun Modifier.dashedOutline(color: Color): Modifier =
    drawBehind {
        val stroke = PLACEHOLDER_STROKE.toPx()
        drawRoundRect(
            color = color,
            topLeft = Offset(stroke / 2, stroke / 2),
            size = Size(size.width - stroke, size.height - stroke),
            cornerRadius = CornerRadius(EventCornerRadius.toPx()),
            style =
                Stroke(
                    width = stroke,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(PLACEHOLDER_DASH.toPx(), PLACEHOLDER_GAP.toPx())),
                ),
        )
    }
