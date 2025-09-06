package de.tobiasschuerg.weekview.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
internal fun DayHeaderRow(
    days: List<LocalDate>,
    leftOffsetDp: Dp,
    topOffsetDp: Dp,
    columnWidth: Dp,
) {
    Row {
        Box(modifier = Modifier.size(leftOffsetDp, topOffsetDp)) // Spacer for time column
        days.forEach { date ->
            Box(
                modifier =
                    Modifier
                        .size(columnWidth, topOffsetDp)
                        .padding(vertical = 2.dp),
                contentAlignment = Alignment.Center,
            ) {
                val shortName = date.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault())
                val shortDate = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)).replace(Regex("[^0-9]*[0-9]+$"), "")
                Text(
                    text = "$shortName\n$shortDate",
                    style =
                        androidx.compose.ui.text.TextStyle(
                            fontSize = 13.sp,
                            color = Color.LightGray,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                        ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
