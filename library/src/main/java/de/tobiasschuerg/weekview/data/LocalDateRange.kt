package de.tobiasschuerg.weekview.data

import java.time.LocalDate

/**
 * A range of [LocalDate] values.
 */
class LocalDateRange(
    override val start: LocalDate,
    override val endInclusive: LocalDate,
) : ClosedRange<LocalDate>, Iterable<LocalDate> {
    override fun contains(value: LocalDate): Boolean = value >= start && value <= endInclusive

    override fun iterator(): Iterator<LocalDate> =
        object : Iterator<LocalDate> {
            private var current = start

            override fun hasNext() = current <= endInclusive

            override fun next(): LocalDate {
                if (!hasNext()) throw NoSuchElementException()
                val result = current
                current = current.plusDays(1)
                return result
            }
        }
}
