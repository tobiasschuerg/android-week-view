package de.tobiasschuerg.weekview.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import de.tobiasschuerg.weekview.data.WeekViewConfig
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle.SHORT
import java.util.Locale

@Composable
fun WeekBackgroundCompose(
    weekViewConfig: WeekViewConfig,
    modifier: Modifier = Modifier,
    days: List<DayOfWeek> =
        listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
        ),
    startTime: LocalTime = LocalTime.of(8, 0),
    endTime: LocalTime = LocalTime.of(18, 0),
    showNowIndicator: Boolean = true,
) {
    val todayHighlightColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    val nowIndicatorColor = MaterialTheme.colorScheme.error

    val columnCount = days.size
    val today = LocalDate.now().dayOfWeek
    val timeLabels = (startTime.hour..endTime.hour).map { LocalTime.of(it, 0) }

    val density = LocalDensity.current
    var boxSize by remember { mutableStateOf(IntSize.Zero) }
    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                boxSize = coordinates.size
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val columnWidth = width / columnCount
            val hourCount = endTime.hour - startTime.hour
            val hourHeightPx = height / hourCount
            // Draw vertical lines (columns)
            for (i in 0..columnCount) {
                val x = i * columnWidth
                drawLine(
                    color = Color.LightGray,
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 2f,
                )
            }
            // Draw horizontal lines (hours)
            for (i in 0..hourCount) {
                val y = i * hourHeightPx
                drawLine(
                    color = Color.LightGray,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 2f,
                )
            }
            // Highlight today column
            val todayIndex = days.indexOf(today)
            if (todayIndex >= 0) {
                val left = todayIndex * columnWidth
                drawRect(
                    color = todayHighlightColor,
                    topLeft = Offset(left, 0f),
                    size = androidx.compose.ui.geometry.Size(columnWidth, height),
                )
            }
            // Draw now indicator
            if (showNowIndicator && todayIndex >= 0) {
                val now = LocalTime.now()
                if (now.isAfter(startTime) && now.isBefore(endTime)) {
                    val y = ((now.hour + now.minute / 60f) - startTime.hour) * hourHeightPx
                    drawLine(
                        color = nowIndicatorColor,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 4f,
                    )
                }
            }
        }
        // Nur Labels anzeigen, wenn Größe bekannt
        if (boxSize.width > 0 && boxSize.height > 0) {
            val columnWidthPx = boxSize.width / columnCount
            val hourCount = endTime.hour - startTime.hour
            val hourHeightPx = boxSize.height / hourCount
            val dayLabelPaddingDp = 8.dp
            val timeLabelPaddingDp = 4.dp
            // Tagesnamen korrekt positionieren (zentriert über Spalte)
            for ((i, day) in days.withIndex()) {
                val shortName = day.getDisplayName(SHORT, Locale.getDefault())
                val xPx = (i * columnWidthPx + columnWidthPx / 2).toFloat()
                val xDp = with(density) { xPx.toDp() }
                val yDp = dayLabelPaddingDp
                androidx.compose.material3.Text(
                    text = shortName,
                    style = TextStyle(fontSize = 14.sp, color = Color.Gray),
                    modifier = Modifier.absoluteOffset(x = xDp, y = yDp)
                )
            }
            // Zeitlabels korrekt positionieren (links, vertikal mittig)
            for ((i, time) in timeLabels.withIndex()) {
                val yPx = (i * hourHeightPx + hourHeightPx / 2).toFloat()
                val yDp = with(density) { yPx.toDp() }
                val xDp = timeLabelPaddingDp
                androidx.compose.material3.Text(
                    text = time.toString(),
                    style = TextStyle(fontSize = 12.sp, color = Color.Gray),
                    modifier = Modifier.absoluteOffset(x = xDp, y = yDp)
                )
            }
        }
    }
}
