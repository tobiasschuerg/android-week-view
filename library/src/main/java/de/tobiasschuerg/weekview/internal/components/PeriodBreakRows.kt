package de.tobiasschuerg.weekview.internal.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import de.tobiasschuerg.weekview.internal.layout.PeriodGridLayout
import de.tobiasschuerg.weekview.style.WeekViewColors

private val breakLabelStyle = TextStyle(fontSize = 11.sp, lineHeight = 13.sp, fontWeight = FontWeight.Medium)

/** The break rows of a period grid: a tinted band across the week with the break's label in the middle. */
@Composable
internal fun PeriodBreakRows(
    layout: PeriodGridLayout,
    colors: WeekViewColors,
) {
    layout.periods.forEachIndexed { index, period ->
        if (period.isBreak) {
            Box(
                modifier =
                    Modifier
                        .offset(y = layout.tops[index])
                        .testTag("PeriodBreak_$index")
                        .fillMaxWidth()
                        .height(layout.heights[index])
                        .background(colors.breakBackground),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = period.label,
                    style = breakLabelStyle,
                    color = colors.timeLabelTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
