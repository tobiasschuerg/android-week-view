package de.tobiasschuerg.weekview.util

import android.view.View.MeasureSpec

internal object ViewHelper {

    fun debugMeasureSpec(spec: Int): String {
        val mode = MeasureSpec.getMode(spec)
        val size = MeasureSpec.getSize(spec)
        return when (mode) {
            MeasureSpec.EXACTLY -> "Exactly $size px"
            MeasureSpec.AT_MOST -> "At most $size px"
            MeasureSpec.UNSPECIFIED -> "Unspecified ($size px)"
            else -> "? $size px"
        }
    }
}
