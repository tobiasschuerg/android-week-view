package de.tobiasschuerg.weekview.internal.components

import androidx.compose.ui.graphics.Color
import java.time.LocalDate

/** The [date] whose column is highlighted with [color], matching the grid's today column. */
internal data class TodayHighlight(
    val date: LocalDate,
    val color: Color,
)
