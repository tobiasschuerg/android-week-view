package de.tobiasschuerg.weekview.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
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
    locale: Locale,
    onDayClick: ((date: LocalDate) -> Unit)? = null,
) {
    val useFullNames = eventConfig.alwaysUseFullName && fullDayNamesFit(days, columnWidth, locale)

    // The row grows with its two text lines (e.g. under a large system font scale) and
    // never shrinks below topOffsetDp, so the day name and date are never cut off.
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Box(modifier = Modifier.width(leftOffsetDp).fillMaxHeight())
        days.forEach { date ->
            val isToday = date == today
            val boxModifier =
                if (highlightCurrentDay && isToday) {
                    Modifier
                        .width(columnWidth)
                        .heightIn(min = topOffsetDp)
                        .fillMaxHeight()
                        .background(style.colors.currentDayBackground)
                } else {
                    Modifier
                        .width(columnWidth)
                        .heightIn(min = topOffsetDp)
                        .fillMaxHeight()
                }
            val textStyle =
                if (highlightCurrentDay && isToday) {
                    HEADER_TODAY_STYLE.copy(color = style.colors.currentDayText)
                } else {
                    HEADER_STYLE.copy(color = style.colors.dayHeaderText)
                }
            val dayName = date.dayOfWeek.getDisplayName(if (useFullNames) FULL else SHORT, locale)
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
                        ).padding(vertical = 2.dp),
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
                    overflow = TextOverflow.Ellipsis,
                    style = textStyle,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

/**
 * Whether every full weekday name fits into one column without being ellipsized, measured
 * in the (widest) bold today style. Under a large system font scale or on a narrow screen
 * that is often not the case, and a row of "Wedne..." / "Thurs..." reads worse than
 * consistently short names, so the caller falls back to the short form for all days.
 */
@Composable
private fun fullDayNamesFit(
    days: List<LocalDate>,
    columnWidth: Dp,
    locale: Locale,
): Boolean {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    return remember(days, columnWidth, locale, density) {
        val maxWidthPx = with(density) { (columnWidth - HEADER_HORIZONTAL_PADDING * 2).roundToPx() }
        days.all { date ->
            val name = date.dayOfWeek.getDisplayName(FULL, locale)
            textMeasurer.measure(name, style = HEADER_TODAY_STYLE, softWrap = false, maxLines = 1).size.width <= maxWidthPx
        }
    }
}

private val HEADER_STYLE =
    TextStyle(
        fontSize = 13.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
    )
private val HEADER_TODAY_STYLE = HEADER_STYLE.copy(fontWeight = FontWeight.Bold)

/** Breathing room kept on each side of the day name when deciding whether the full name fits. */
private val HEADER_HORIZONTAL_PADDING = 2.dp
