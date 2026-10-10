package de.tobiasschuerg.weekview.sample

import androidx.compose.runtime.Composable
import de.tobiasschuerg.weekview.content.EventContentScope
import de.tobiasschuerg.weekview.content.FilledEventContent
import de.tobiasschuerg.weekview.content.OutlinedEventContent
import de.tobiasschuerg.weekview.content.TintedEventContent

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
