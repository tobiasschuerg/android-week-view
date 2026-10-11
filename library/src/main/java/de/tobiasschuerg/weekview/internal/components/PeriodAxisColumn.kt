package de.tobiasschuerg.weekview.internal.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.tobiasschuerg.weekview.internal.layout.PeriodGridLayout
import de.tobiasschuerg.weekview.internal.time.toAxisLabel
import de.tobiasschuerg.weekview.model.Period
import de.tobiasschuerg.weekview.style.WeekViewColors
import java.util.Locale

/** The lesson's label, e.g. "1". */
private val periodLabelStyle = TextStyle(fontSize = 14.sp, lineHeight = 18.sp, fontWeight = FontWeight.Bold)

/** Start and end time below the label. */
private val periodTimeStyle = TextStyle(fontSize = 10.sp, lineHeight = 12.sp)

/** Room kept free above and below the label and times. */
private val VERTICAL_PADDING = 4.dp

/**
 * The left column of a period grid: each lesson's label, with its start and end time when [showTimes]
 * and the row is tall enough. Break rows stay empty here; their label runs across the grid.
 *
 * Like the time axis, it sits in the grid's scroll container so the labels stay level with their rows.
 */
@Composable
internal fun PeriodAxisColumn(
    layout: PeriodGridLayout,
    width: Dp,
    showTimes: Boolean,
    locale: Locale,
    colors: WeekViewColors,
) {
    // Below this row height the times are left out, so the label alone stays readable. Measured in
    // line heights, so it follows the font scale.
    val minHeightForTimes =
        with(LocalDensity.current) { periodLabelStyle.lineHeight.toDp() + periodTimeStyle.lineHeight.toDp() * 2 } + VERTICAL_PADDING * 2
    Box(modifier = Modifier.width(width).height(layout.height)) {
        layout.periods.forEachIndexed { index, period ->
            if (!period.isBreak) {
                PeriodLabel(
                    period = period,
                    showTimes = showTimes && layout.heights[index] >= minHeightForTimes,
                    locale = locale,
                    colors = colors,
                    modifier =
                        Modifier
                            .offset(y = layout.tops[index])
                            .testTag("PeriodLabel_$index")
                            .size(width, layout.heights[index])
                            .padding(horizontal = TimeAxisDefaults.horizontalPadding),
                )
            }
        }
    }
}

@Composable
private fun PeriodLabel(
    period: Period,
    showTimes: Boolean,
    locale: Locale,
    colors: WeekViewColors,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = period.label,
            style = periodLabelStyle,
            color = colors.timeLabelTextColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (showTimes) {
            listOf(period.timeSpan.start, period.timeSpan.endExclusive).forEach { time ->
                Text(text = time.toAxisLabel(locale).time, style = periodTimeStyle, color = colors.timeLabelTextColor, maxLines = 1)
            }
        }
    }
}
