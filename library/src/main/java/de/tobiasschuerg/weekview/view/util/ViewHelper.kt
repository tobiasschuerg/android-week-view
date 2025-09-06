package de.tobiasschuerg.weekview.view.util

import android.view.View

@Deprecated("Not needed with Compose")
internal object ViewHelper {
    fun debugMeasureSpec(spec: Int): String {
        val mode = View.MeasureSpec.getMode(spec)
        val size = View.MeasureSpec.getSize(spec)
        return when (mode) {
            View.MeasureSpec.EXACTLY -> "Exactly $size px"
            View.MeasureSpec.AT_MOST -> "At most $size px"
            View.MeasureSpec.UNSPECIFIED -> "Unspecified ($size px)"
            else -> "? $size px"
        }
    }
}
