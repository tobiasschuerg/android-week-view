package de.tobiasschuerg.weekview.internal.layout

import java.time.LocalTime

/**
 * The full hour the grid closes on for events ending at [latestEventEnd]: the end itself when it
 * falls on the hour, otherwise the next one, so a day ending at 15:00 shows no empty 15:00 row
 * while one ending at 15:10 still gets the row its last event runs into. Anything past 23:00
 * becomes midnight, represented as [LocalTime.MAX].
 */
internal fun gridEndFor(latestEventEnd: LocalTime): LocalTime = latestEventEnd.ceilToHour()

/** Rounds up to the next full hour; anything past 23:00 becomes midnight, represented as [LocalTime.MAX]. */
internal fun LocalTime.ceilToHour(): LocalTime =
    when {
        minute == 0 && second == 0 && nano == 0 -> this
        hour == 23 -> LocalTime.MAX
        else -> LocalTime.of(hour + 1, 0)
    }
