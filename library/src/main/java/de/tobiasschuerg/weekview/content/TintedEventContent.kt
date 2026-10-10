package de.tobiasschuerg.weekview.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * Entry style with a light tint of the event's background color and a bar in its full color on the
 * left, in the manner of calendar apps. The tint sits on the theme's surface color, so the grid
 * doesn't show through. Text uses the theme's `onSurface` color, since the event's
 * text color is meant for the full-strength background.
 */
@Composable
fun TintedEventContent(
    scope: EventContentScope,
    modifier: Modifier = Modifier,
) {
    val accent = scope.event.backgroundColor
    Row(
        modifier =
            modifier
                .fillMaxSize()
                .clip(EventCornerShape)
                // Opaque base, so grid lines don't show through the translucent tint.
                .background(MaterialTheme.colorScheme.surface)
                .background(accent.copy(alpha = TINT_ALPHA)),
    ) {
        Box(
            Modifier
                .width(ACCENT_BAR_WIDTH)
                .fillMaxHeight()
                .background(accent),
        )
        EventFields(scope = scope, textColor = MaterialTheme.colorScheme.onSurface)
    }
}

private const val TINT_ALPHA = 0.2f
private val ACCENT_BAR_WIDTH = 3.dp
