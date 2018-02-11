package de.tobiasschuerg.timetableview.util

import android.content.Context
import android.util.TypedValue

internal fun Context.dipToPixeel(dip: Float): Float {
    val metrics = resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, metrics)
}