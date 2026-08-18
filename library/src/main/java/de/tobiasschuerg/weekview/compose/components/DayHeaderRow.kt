package de.tobiasschuerg.weekview.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.tobiasschuerg.weekview.compose.style.WeekViewStyle
import de.tobiasschuerg.weekview.compose.style.defaultWeekViewStyle
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.util.toShortDateStringWithoutYear
import java.time.LocalDate
import java.time.format.TextStyle.FULL
import java.time.format.TextStyle.SHORT
import java.util.Locale

@Composable
internal fun DayHeaderRow(
    days: List<LocalDate>,
    today: LocalDate,
    leftOffsetDp: Dp,
    topOffsetDp: Dp,
    columnWidth: Dp,
    style: WeekViewStyle = defaultWeekViewStyle(),
    highlightCurrentDay: Boolean = true,
    eventConfig: EventConfig = EventConfig(),
    locale: Locale = LocalLocale.current.platformLocale,
    onDayClick: ((date: LocalDate) -> Unit)? = null,
) {
    Row {
        Box(modifier = Modifier.size(leftOffsetDp, topOffsetDp))
        days.forEach { date ->
            val isToday = date == today
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
            val dayName =
                if (eventConfig.alwaysUseFullName) {
                    date.dayOfWeek.getDisplayName(FULL, locale)
                } else {
                    date.dayOfWeek.getDisplayName(SHORT, locale)
                }
            val shortDate = date.toShortDateStringWithoutYear(locale)
            Column(
                modifier =
                    boxModifier
                        .testTag("DayHeader_$date")
                        .semantics {
                            contentDescription = "$dayName, $shortDate"
                        }
                        .combinedClickable(
                            enabled = onDayClick != null,
                            role = Role.Button,
                            onClick = { onDayClick?.invoke(date) },
                        ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = dayName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = textStyle,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = shortDate,
                    maxLines = 1,
                    style = textStyle,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
