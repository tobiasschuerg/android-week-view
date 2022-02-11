package de.tobiasschuerg.weekview.data

/**
 * Configures the appearance of an [de.tobiasschuerg.weekview.view.EventView].
 *
 * Created by Tobias Schürg on 04.03.2018.
 */
data class EventConfig(
    val useShortNames: Boolean = true,
    val showTimeStart: Boolean = true,
    val showUpperText: Boolean = true,
    val showSubtitle: Boolean = true,
    val showLowerText: Boolean = true,
    val showTimeEnd: Boolean = true
)
