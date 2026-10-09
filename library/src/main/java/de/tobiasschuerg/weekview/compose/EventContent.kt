package de.tobiasschuerg.weekview.compose

import androidx.compose.runtime.Composable

/**
 * Slot that draws a timed entry, including its background and shape.
 * Use [DefaultEventContent] for the built-in look.
 */
typealias EventContent = @Composable (scope: EventContentScope) -> Unit
