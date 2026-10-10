package de.tobiasschuerg.weekview.internal.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import de.tobiasschuerg.weekview.internal.layout.WeekViewMetrics
import de.tobiasschuerg.weekview.internal.time.toAxisLabel
import java.time.LocalTime
import java.util.Locale
import kotlin.math.ceil

/** Typography and spacing shared by the time axis labels, the current-time pill and their measurements. */
internal object TimeAxisDefaults {
    /** The clock time, the first line of a label. */
    val timeTextStyle: TextStyle = TextStyle(fontSize = 12.sp, lineHeight = 14.sp)

    /** The AM/PM marker stacked below the clock time. */
    val dayPeriodTextStyle: TextStyle = TextStyle(fontSize = 10.sp, lineHeight = 11.sp, fontWeight = FontWeight.Medium)

    /** Padding between the gutter edges and a label. */
    val horizontalPadding: Dp = 6.dp

    /** Padding the current-time pill draws around its text. */
    val pillHorizontalPadding: Dp = 8.dp
    val pillVerticalPadding: Dp = 4.dp

    /** Every hour the axis can show, plus a wide minute for the current-time pill. */
    private val sampleTimes: List<LocalTime> =
        (0..23).flatMap { hour -> listOf(LocalTime.of(hour, 0), LocalTime.of(hour, 58)) }

    /**
     * Measures the clock times [locale] can produce. 12-hour locales stack their AM/PM marker below
     * the time instead of next to it, which keeps the gutter as narrow as a 24-hour one.
     */
    @Composable
    fun rememberLabelMetrics(locale: Locale): TimeAxisLabelMetrics {
        val textMeasurer = rememberTextMeasurer()
        val density = LocalDensity.current
        return remember(locale, density, textMeasurer) {
            // The pill text is bold, so measure everything in the wider of the two weights.
            val timeSize = textMeasurer.widestSize(sampleTimes.map { it.toAxisLabel(locale).time }, timeTextStyle)
            with(density) {
                TimeAxisLabelMetrics(
                    axisWidth =
                        max(
                            WeekViewMetrics.LEFT_OFFSET,
                            timeSize.width.toDp() + (pillHorizontalPadding + horizontalPadding) * 2,
                        ),
                    // The pill shows the clock time only, so it stays a single line in every locale.
                    pillHeight = timeSize.height.toDp() + pillVerticalPadding * 2,
                )
            }
        }
    }

    /**
     * Size of the widest of [texts] and the height of one line, or zero when there is nothing to measure.
     *
     * All texts are laid out together, one per line, because a text layout costs far more to set up
     * than to extend: one layout of 48 lines is much cheaper than 48 layouts of one line.
     */
    private fun TextMeasurer.widestSize(
        texts: List<String>,
        style: TextStyle,
    ): IntSize {
        if (texts.isEmpty()) return IntSize.Zero
        val layout = measure(texts.joinToString("\n"), style.copy(fontWeight = FontWeight.Bold), softWrap = false)
        val lines = 0 until layout.lineCount
        return IntSize(
            width = ceil(lines.maxOf { layout.getLineRight(it) - layout.getLineLeft(it) }).toInt(),
            height = ceil(lines.maxOf { layout.getLineBottom(it) - layout.getLineTop(it) }).toInt(),
        )
    }
}
