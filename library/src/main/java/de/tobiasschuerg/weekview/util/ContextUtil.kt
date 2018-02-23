package de.tobiasschuerg.weekview.util

import android.content.Context
import android.util.TypedValue

internal fun Context.dipToPixelF(dip: Float): Float {
    val metrics = resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, metrics)
}

internal fun Context.dipToPixelI(dip: Float): Int {
    return dipToPixelF(dip).toInt()
}