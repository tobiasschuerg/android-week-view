package de.tobiasschuerg.weekview.content

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * Light entry style: the theme's surface color with a border in the event's background color,
 * text in the theme's `onSurface` color. The surface keeps grid lines from showing through.
 */
@Composable
public fun OutlinedEventContent(
    scope: EventContentScope,
    modifier: Modifier = Modifier,
) {
    EventFields(
        scope = scope,
        textColor = MaterialTheme.colorScheme.onSurface,
        height = scope.height - BORDER_WIDTH * 2,
        modifier =
            modifier
                .fillMaxSize()
                .clip(EventCornerShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(BORDER_WIDTH, scope.event.backgroundColor, EventCornerShape)
                .padding(BORDER_WIDTH),
    )
}

private val BORDER_WIDTH = 1.5.dp
