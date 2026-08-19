package de.tobiasschuerg.weekview.sample.data

import android.graphics.Color
import androidx.core.graphics.toColorInt
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.LocalDateRange
import de.tobiasschuerg.weekview.data.WeekData
import de.tobiasschuerg.weekview.util.TimeSpan
import java.time.LocalTime

/** Work sample: standups, meetings, double-booked slots, and a multi-day offsite. */
object SampleTimetableWork {
    fun create(dateRange: LocalDateRange): WeekData {
        val days = dateRange.toList()
        val weekData = WeekData(dateRange, LocalTime.of(8, 0), LocalTime.of(18, 0))

        val mon = days[0]
        val tue = days.getOrNull(1)
        val wed = days.getOrNull(2)
        val thu = days.getOrNull(3)
        val fri = days.getOrNull(4)

        var nextId = 100L

        // Monday
        weekData.add(
            Event.Single(
                id = nextId++,
                date = mon,
                title = "Team Standup",
                shortTitle = "Standup",
                subTitle = "Meeting Room A",
                timeSpan = TimeSpan(LocalTime.of(9, 0), LocalTime.of(9, 30)),
                textColor = Color.WHITE,
                backgroundColor = "#0277BD".toColorInt(),
            ),
        )
        weekData.add(
            Event.Single(
                id = nextId++,
                date = mon,
                title = "Sprint Planning",
                shortTitle = "Planning",
                subTitle = "Conference Room",
                timeSpan = TimeSpan(LocalTime.of(10, 0), LocalTime.of(12, 0)),
                textColor = Color.WHITE,
                backgroundColor = "#AD1457".toColorInt(),
                upperText = "Sprint 24",
            ),
        )
        weekData.add(
            Event.Single(
                id = nextId++,
                date = mon,
                title = "1:1 with Manager",
                shortTitle = "1:1",
                subTitle = "Office",
                timeSpan = TimeSpan(LocalTime.of(14, 0), LocalTime.of(14, 30)),
                textColor = Color.WHITE,
                backgroundColor = "#4527A0".toColorInt(),
            ),
        )

        // Tuesday
        tue?.let { d ->
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Team Standup",
                    shortTitle = "Standup",
                    subTitle = "Meeting Room A",
                    timeSpan = TimeSpan(LocalTime.of(9, 0), LocalTime.of(9, 30)),
                    textColor = Color.WHITE,
                    backgroundColor = "#0277BD".toColorInt(),
                ),
            )
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Code Review Session",
                    shortTitle = "Review",
                    subTitle = "Virtual",
                    timeSpan = TimeSpan(LocalTime.of(11, 0), LocalTime.of(12, 0)),
                    textColor = Color.WHITE,
                    backgroundColor = "#00695C".toColorInt(),
                ),
            )
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Design Workshop",
                    shortTitle = "Design",
                    subTitle = "Whiteboard Room",
                    timeSpan = TimeSpan(LocalTime.of(14, 0), LocalTime.of(16, 0)),
                    textColor = Color.WHITE,
                    backgroundColor = "#BF360C".toColorInt(),
                    upperText = "Q2 Features",
                ),
            )
        }

        // Wednesday — includes double-booked meetings
        wed?.let { d ->
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Team Standup",
                    shortTitle = "Standup",
                    subTitle = "Meeting Room A",
                    timeSpan = TimeSpan(LocalTime.of(9, 0), LocalTime.of(9, 30)),
                    textColor = Color.WHITE,
                    backgroundColor = "#0277BD".toColorInt(),
                ),
            )
            // Two meetings at the same time (overlap)
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Hiring Committee",
                    shortTitle = "Hiring",
                    subTitle = "Meeting Room C",
                    timeSpan = TimeSpan(LocalTime.of(10, 0), LocalTime.of(11, 0)),
                    textColor = Color.WHITE,
                    backgroundColor = "#E65100".toColorInt(),
                ),
            )
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Tech Sync",
                    shortTitle = "Tech",
                    subTitle = "Virtual",
                    timeSpan = TimeSpan(LocalTime.of(10, 0), LocalTime.of(11, 30)),
                    textColor = Color.WHITE,
                    backgroundColor = "#1B5E20".toColorInt(),
                ),
            )
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Lunch & Learn",
                    shortTitle = "L&L",
                    subTitle = "Cafeteria",
                    timeSpan = TimeSpan(LocalTime.of(12, 0), LocalTime.of(13, 0)),
                    textColor = Color.WHITE,
                    backgroundColor = "#558B2F".toColorInt(),
                    upperText = "Kotlin Coroutines",
                ),
            )
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Architecture Review",
                    shortTitle = "ArchReview",
                    subTitle = "Conference Room",
                    timeSpan = TimeSpan(LocalTime.of(15, 0), LocalTime.of(16, 30)),
                    textColor = Color.WHITE,
                    backgroundColor = "#283593".toColorInt(),
                ),
            )
        }

        // Thursday
        thu?.let { d ->
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Team Standup",
                    shortTitle = "Standup",
                    subTitle = "Meeting Room A",
                    timeSpan = TimeSpan(LocalTime.of(9, 0), LocalTime.of(9, 30)),
                    textColor = Color.WHITE,
                    backgroundColor = "#0277BD".toColorInt(),
                ),
            )
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Product Demo",
                    shortTitle = "Demo",
                    subTitle = "Main Hall",
                    timeSpan = TimeSpan(LocalTime.of(10, 0), LocalTime.of(11, 0)),
                    textColor = Color.WHITE,
                    backgroundColor = "#6A1B9A".toColorInt(),
                    upperText = "Stakeholders",
                ),
            )
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Retrospective",
                    shortTitle = "Retro",
                    subTitle = "Meeting Room B",
                    timeSpan = TimeSpan(LocalTime.of(15, 0), LocalTime.of(16, 0)),
                    textColor = Color.WHITE,
                    backgroundColor = "#AD1457".toColorInt(),
                    upperText = "Sprint 23",
                ),
            )
        }

        // Friday
        fri?.let { d ->
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Team Standup",
                    shortTitle = "Standup",
                    subTitle = "Meeting Room A",
                    timeSpan = TimeSpan(LocalTime.of(9, 0), LocalTime.of(9, 30)),
                    textColor = Color.WHITE,
                    backgroundColor = "#0277BD".toColorInt(),
                ),
            )
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Knowledge Sharing",
                    shortTitle = "KnowShare",
                    subTitle = "Virtual",
                    timeSpan = TimeSpan(LocalTime.of(11, 0), LocalTime.of(12, 0)),
                    textColor = Color.WHITE,
                    backgroundColor = "#00695C".toColorInt(),
                ),
            )
        }

        // Multi-day: team offsite
        if (days.size >= 2) {
            weekData.add(
                Event.MultiDay(
                    id = nextId++,
                    date = days[0],
                    title = "Team Offsite",
                    shortTitle = "Offsite",
                    lastDate = days[1],
                    textColor = Color.WHITE,
                    backgroundColor = "#00838F".toColorInt(),
                ),
            )
        }

        // All-day
        fri?.let { d ->
            weekData.add(
                Event.AllDay(
                    id = nextId,
                    date = d,
                    title = "Casual Friday",
                    shortTitle = "Casual",
                    textColor = Color.WHITE,
                    backgroundColor = "#7B1FA2".toColorInt(),
                ),
            )
        }

        return weekData
    }
}
