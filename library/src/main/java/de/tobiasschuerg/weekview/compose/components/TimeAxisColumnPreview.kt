package de.tobiasschuerg.weekview.compose.components

import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalTime

/**
 * Previews for [TimeAxisColumn] showcasing German (24h) and US (12h) time formats.
 */

private fun sampleTimeLabels(): List<LocalTime> = (10..19).map { hour -> LocalTime.of(hour, 0) }

private val sampleNow: LocalTime = LocalTime.of(13, 30)
private val sampleStart: LocalTime = sampleTimeLabels().first()
private val sampleEnd: LocalTime = sampleTimeLabels().last()

@Preview(name = "Time Axis DE", showBackground = true, locale = "de")
@Composable
private fun TimeAxisColumnPreviewDE() {
    val timeLabels = sampleTimeLabels()
    val rowHeight = 60.dp
    val gridHeight = rowHeight * timeLabels.size
    val leftOffset = 64.dp
    TimeAxisColumn(
        timeLabels = timeLabels,
        now = sampleNow,
        gridStartTime = sampleStart,
        gridEndTime = sampleEnd,
        rowHeightDp = rowHeight,
        gridHeightDp = gridHeight,
        leftOffsetDp = leftOffset,
        scrollState = rememberScrollState(0),
        showNowIndicator = true,
    )
}

@Preview(name = "Time Axis US", showBackground = true, locale = "US")
@Composable
private fun TimeAxisColumnPreviewUS() {
    val timeLabels = sampleTimeLabels()
    val rowHeight = 60.dp
    val gridHeight = rowHeight * timeLabels.size
    val leftOffset = 64.dp
    TimeAxisColumn(
        timeLabels = timeLabels,
        now = sampleNow,
        gridStartTime = sampleStart,
        gridEndTime = sampleEnd,
        rowHeightDp = rowHeight,
        gridHeightDp = gridHeight,
        leftOffsetDp = leftOffset,
        scrollState = rememberScrollState(0),
        showNowIndicator = true,
    )
}
