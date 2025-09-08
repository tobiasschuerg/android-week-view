package de.tobiasschuerg.weekview.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.tobiasschuerg.weekview.compose.style.WeekViewStyle
import de.tobiasschuerg.weekview.compose.style.defaultWeekViewStyle
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
internal fun DayHeaderRow(
    days: List<LocalDate>,
    leftOffsetDp: Dp,
    topOffsetDp: Dp,
    columnWidth: Dp,
    style: WeekViewStyle = defaultWeekViewStyle(),
    highlightCurrentDay: Boolean = true,
) {
    Row {
        Box(modifier = Modifier.size(leftOffsetDp, topOffsetDp))
        days.forEach { date ->
            val isToday = date == LocalDate.now()
            val boxModifier =
                if (highlightCurrentDay && isToday) {
                    Modifier
                        .size(columnWidth, topOffsetDp)
                        .background(style.colors.currentDayBackground)
                        .padding(vertical = 2.dp)
                } else {
                    Modifier
                        .size(columnWidth, topOffsetDp)
                        .padding(vertical = 2.dp)
                }
            val textStyle =
                if (highlightCurrentDay && isToday) {
                    TextStyle(
                        fontSize = 13.sp,
                        color = style.colors.currentDayText,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                } else {
                    TextStyle(
                        fontSize = 13.sp,
                        color = style.colors.dayHeaderText,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    )
                }
            Box(
                modifier = boxModifier,
                contentAlignment = Alignment.Center,
            ) {
                val shortName = date.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault())
                val shortDate = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)).replace(Regex("[^0-9]*[0-9]+$"), "")
                Text(
                    text = "$shortName\n$shortDate",
                    style = textStyle,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
