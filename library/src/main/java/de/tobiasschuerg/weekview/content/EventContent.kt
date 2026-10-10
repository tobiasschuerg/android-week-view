package de.tobiasschuerg.weekview.content

import androidx.compose.runtime.Composable

/**
 * Slot that draws a timed entry, including its background and shape.
 * Use [FilledEventContent] for the built-in look.
 */
typealias EventContent = @Composable (scope: EventContentScope) -> Unit
