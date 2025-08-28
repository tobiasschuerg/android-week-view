package de.tobiasschuerg.weekview.util

import android.graphics.Paint
import android.graphics.Rect

internal object TextHelper {
    fun fitText(
        text: String,
        maxTextSize: Float,
        maxWidth: Int,
        maxHeight: Int,
    ): Float {
        var hi = maxTextSize
        var lo = 15f // min text size
        val threshold = 0.5f // How close we have to be

        val paint = Paint()
        val bounds = Rect()

        while (hi - lo > threshold) {
            val size = (hi + lo) / 2
            paint.textSize = size
            paint.getTextBounds(text, 0, text.length, bounds)

            if (bounds.width() >= maxWidth || bounds.height() >= maxHeight) {
                // too big
                hi = size
            } else {
                // too small
                lo = size
            }
        }
        // Use lo so that we undershoot rather than overshoot
        return lo
    }
}
