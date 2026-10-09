package de.tobiasschuerg.weekview.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

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
        textColor = Color(scope.event.textColor),
        modifier =
            modifier
                .fillMaxSize()
                .clip(EventCornerShape)
                .background(Color(scope.event.backgroundColor)),
    )
}
