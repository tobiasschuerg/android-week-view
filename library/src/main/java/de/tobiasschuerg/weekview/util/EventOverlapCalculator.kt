package de.tobiasschuerg.weekview.util

import de.tobiasschuerg.weekview.data.Event

/**
 * Utility class for calculating event overlaps and positioning in the week view.
 * Handles the logic for determining which events overlap and how to position them
 * side by side within the same day column.
 */
object EventOverlapCalculator {
    /**
     * Data class representing the position and sizing information for an event
     * when it has overlaps with other events.
     *
     * @param widthFraction The fraction of the column width this event should occupy (0.0 to 1.0)
     * @param offsetFraction The horizontal offset fraction from the left edge of the column (0.0 to 1.0)
     * @param overlapGroup The group index this event belongs to (for consistent positioning)
     */
    data class EventLayout(
        val widthFraction: Float,
        val offsetFraction: Float,
        val overlapGroup: Int,
    )

    /**
     * Calculates layout information for all events, handling overlaps by positioning
     * overlapping events side by side within their day columns.
     *
     * @param events List of events to calculate layouts for
     * @return Map from event ID to layout information
     */
    fun calculateEventLayouts(events: List<Event.Single>): Map<Long, EventLayout> {
        val layoutMap = mutableMapOf<Long, EventLayout>()

        // Group events by date for separate overlap calculation per day
        val eventsByDate = events.groupBy { it.date }

        eventsByDate.forEach { (_, dayEvents) ->
            calculateOverlapsForDay(dayEvents, layoutMap)
        }

        return layoutMap
    }

    /**
     * Calculates overlaps for events within a single day.
     */
    private fun calculateOverlapsForDay(
        dayEvents: List<Event.Single>,
        layoutMap: MutableMap<Long, EventLayout>,
    ) {
        // Sort events by start time for consistent processing
        val sortedEvents = dayEvents.sortedBy { it.timeSpan.start }
        val eventCount = sortedEvents.size
        val visited = BooleanArray(eventCount)
        var groupIndex = 0

        // Build overlap graph (adjacency list)
        val adjacency = Array(eventCount) { mutableListOf<Int>() }
        for (i in 0 until eventCount) {
            for (j in i + 1 until eventCount) {
                if (eventsOverlap(sortedEvents[i], sortedEvents[j])) {
                    adjacency[i].add(j)
                    adjacency[j].add(i)
                }
            }
        }

        // Find connected components (overlap groups)
        for (i in 0 until eventCount) {
            if (visited[i]) continue
            val group = mutableListOf<Int>()
            val queue = ArrayDeque<Int>()
            queue.add(i)
            visited[i] = true
            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                group.add(current)
                for (neighbor in adjacency[current]) {
                    if (!visited[neighbor]) {
                        visited[neighbor] = true
                        queue.add(neighbor)
                    }
                }
            }

            // Use greedy graph coloring to find the minimum number of slots.
            // For interval graphs (sorted by start time), greedy coloring is optimal
            // and uses only as many slots as the max number of simultaneously overlapping events.
            val colors = IntArray(group.size) { -1 }
            var maxColor = 0
            for (gi in group.indices) {
                val eventIdx = group[gi]
                val usedColors = mutableSetOf<Int>()
                for (neighbor in adjacency[eventIdx]) {
                    val neighborGroupIdx = group.indexOf(neighbor)
                    if (neighborGroupIdx >= 0 && colors[neighborGroupIdx] >= 0) {
                        usedColors.add(colors[neighborGroupIdx])
                    }
                }
                var color = 0
                while (color in usedColors) color++
                colors[gi] = color
                if (color > maxColor) maxColor = color
            }
            val slotCount = maxColor + 1
            val widthPerSlot = 1.0f / slotCount
            group.forEachIndexed { idx, eventIdx ->
                val event = sortedEvents[eventIdx]
                layoutMap[event.id] =
                    EventLayout(
                        widthFraction = widthPerSlot,
                        offsetFraction = colors[idx] * widthPerSlot,
                        overlapGroup = groupIndex,
                    )
            }
            groupIndex++
        }
    }

    /**
     * Determines if two events overlap in time.
     * Based on the original overlap logic from WeekView.kt
     */
    private fun eventsOverlap(
        event1: Event.Single,
        event2: Event.Single,
    ): Boolean {
        // If different dates, no overlap possible
        if (event1.date != event2.date) return false

        val event1Start = event1.timeSpan.start
        val event1End = event1.timeSpan.endExclusive
        val event2Start = event2.timeSpan.start
        val event2End = event2.timeSpan.endExclusive

        // Check if event2 starts within event1's timespan
        val event2StartsWithinEvent1 = event2Start >= event1Start && event2Start < event1End

        // Check if event2 ends within event1's timespan
        val event2EndsWithinEvent1 = event2End > event1Start && event2End <= event1End

        // Check if event1 is completely within event2
        val event1WithinEvent2 = event1Start >= event2Start && event1End <= event2End

        // Check if event2 is completely within event1
        val event2WithinEvent1 = event2Start >= event1Start && event2End <= event1End

        return event2StartsWithinEvent1 || event2EndsWithinEvent1 || event1WithinEvent2 || event2WithinEvent1
    }
}
