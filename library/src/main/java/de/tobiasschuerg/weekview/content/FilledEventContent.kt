package de.tobiasschuerg.weekview.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

/**
 * Default entry style: a rounded box filled with the event's background color, text in its text color.
 * Shows as many fields as fit the entry height.
 */
@Composable
fun FilledEventContent(
    scope: EventContentScope,
    modifier: Modifier = Modifier,
) {
    EventFields(
        scope = scope,
        textColor = scope.event.textColor,
        modifier =
            modifier
                .fillMaxSize()
                .clip(EventCornerShape)
                .background(scope.event.backgroundColor),
    )
}
