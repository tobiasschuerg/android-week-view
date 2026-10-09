package de.tobiasschuerg.weekview.sample

import androidx.compose.runtime.Composable
import de.tobiasschuerg.weekview.compose.EventContentScope
import de.tobiasschuerg.weekview.compose.FilledEventContent
import de.tobiasschuerg.weekview.compose.OutlinedEventContent
import de.tobiasschuerg.weekview.compose.TintedEventContent

/** Entry styles selectable in the sample: the three built into the library plus a custom one. */
enum class SampleEventStyle(
    val label: String,
) {
    FILLED("Filled"),
    TINTED("Tinted"),
    OUTLINED("Outlined"),
    CUSTOM("Custom"),
    ;

    @Composable
    fun Content(scope: EventContentScope) {
        when (this) {
            FILLED -> FilledEventContent(scope)
            TINTED -> TintedEventContent(scope)
            OUTLINED -> OutlinedEventContent(scope)
            CUSTOM -> CustomEventContent(scope)
        }
    }
}
