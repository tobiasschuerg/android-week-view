package de.tobiasschuerg.weekview.sample.data

import androidx.compose.ui.graphics.Color
import de.tobiasschuerg.weekview.model.Event
import de.tobiasschuerg.weekview.model.LocalDateRange
import de.tobiasschuerg.weekview.model.TimeSpan
import de.tobiasschuerg.weekview.model.WeekData
import java.time.LocalTime

/** Work sample: standups, meetings, double-booked slots, and a multi-day offsite. */
object SampleTimetableWork {
    fun create(dateRange: LocalDateRange): WeekData {
        val days = dateRange.toList()
        val events = mutableListOf<Event>()

        val mon = days[0]
        val tue = days.getOrNull(1)
        val wed = days.getOrNull(2)
        val thu = days.getOrNull(3)
        val fri = days.getOrNull(4)

        var nextId = 100L

        // Monday
        events.add(
            Event.Single(
                id = (nextId++).toString(),
                date = mon,
                title = "Team Standup",
                shortTitle = "Standup",
                subTitle = "Meeting Room A",
                timeSpan = TimeSpan(LocalTime.of(9, 0), LocalTime.of(9, 30)),
                textColor = Color.White,
                backgroundColor = Color(0xFF0277BD),
            ),
        )
        events.add(
            Event.Single(
                id = (nextId++).toString(),
                date = mon,
                title = "Sprint Planning",
                shortTitle = "Planning",
                subTitle = "Conference Room",
                timeSpan = TimeSpan(LocalTime.of(10, 0), LocalTime.of(12, 0)),
                textColor = Color.White,
                backgroundColor = Color(0xFFAD1457),
                upperText = "Sprint 24",
            ),
        )
        events.add(
            Event.Single(
                id = (nextId++).toString(),
                date = mon,
                title = "1:1 with Manager",
                shortTitle = "1:1",
                subTitle = "Office",
                timeSpan = TimeSpan(LocalTime.of(14, 0), LocalTime.of(14, 30)),
                textColor = Color.White,
                backgroundColor = Color(0xFF4527A0),
            ),
        )

        // Tuesday
        tue?.let { d ->
            events.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Team Standup",
                    shortTitle = "Standup",
                    subTitle = "Meeting Room A",
                    timeSpan = TimeSpan(LocalTime.of(9, 0), LocalTime.of(9, 30)),
                    textColor = Color.White,
                    backgroundColor = Color(0xFF0277BD),
                ),
            )
            events.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Code Review Session",
                    shortTitle = "Review",
                    subTitle = "Virtual",
                    timeSpan = TimeSpan(LocalTime.of(11, 0), LocalTime.of(12, 0)),
                    textColor = Color.White,
                    backgroundColor = Color(0xFF00695C),
                ),
            )
            events.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Design Workshop",
                    shortTitle = "Design",
                    subTitle = "Whiteboard Room",
                    timeSpan = TimeSpan(LocalTime.of(14, 0), LocalTime.of(16, 0)),
                    textColor = Color.White,
                    backgroundColor = Color(0xFFBF360C),
                    upperText = "Q2 Features",
                ),
            )
        }

        // Wednesday — includes double-booked meetings
        wed?.let { d ->
            events.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Team Standup",
                    shortTitle = "Standup",
                    subTitle = "Meeting Room A",
                    timeSpan = TimeSpan(LocalTime.of(9, 0), LocalTime.of(9, 30)),
                    textColor = Color.White,
                    backgroundColor = Color(0xFF0277BD),
                ),
            )
            // Two meetings at the same time (overlap)
            events.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Hiring Committee",
                    shortTitle = "Hiring",
                    subTitle = "Meeting Room C",
                    timeSpan = TimeSpan(LocalTime.of(10, 0), LocalTime.of(11, 0)),
                    textColor = Color.White,
                    backgroundColor = Color(0xFFE65100),
                ),
            )
            events.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Tech Sync",
                    shortTitle = "Tech",
                    subTitle = "Virtual",
                    timeSpan = TimeSpan(LocalTime.of(10, 0), LocalTime.of(11, 30)),
                    textColor = Color.White,
                    backgroundColor = Color(0xFF1B5E20),
                ),
            )
            events.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Lunch & Learn",
                    shortTitle = "L&L",
                    subTitle = "Cafeteria",
                    timeSpan = TimeSpan(LocalTime.of(12, 0), LocalTime.of(13, 0)),
                    textColor = Color.White,
                    backgroundColor = Color(0xFF558B2F),
                    upperText = "Kotlin Coroutines",
                ),
            )
            events.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Architecture Review",
                    shortTitle = "ArchReview",
                    subTitle = "Conference Room",
                    timeSpan = TimeSpan(LocalTime.of(15, 0), LocalTime.of(16, 30)),
                    textColor = Color.White,
                    backgroundColor = Color(0xFF283593),
                ),
            )
        }

        // Thursday
        thu?.let { d ->
            events.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Team Standup",
                    shortTitle = "Standup",
                    subTitle = "Meeting Room A",
                    timeSpan = TimeSpan(LocalTime.of(9, 0), LocalTime.of(9, 30)),
                    textColor = Color.White,
                    backgroundColor = Color(0xFF0277BD),
                ),
            )
            events.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Product Demo",
                    shortTitle = "Demo",
                    subTitle = "Main Hall",
                    timeSpan = TimeSpan(LocalTime.of(10, 0), LocalTime.of(11, 0)),
                    textColor = Color.White,
                    backgroundColor = Color(0xFF6A1B9A),
                    upperText = "Stakeholders",
                ),
            )
            events.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Retrospective",
                    shortTitle = "Retro",
                    subTitle = "Meeting Room B",
                    timeSpan = TimeSpan(LocalTime.of(15, 0), LocalTime.of(16, 0)),
                    textColor = Color.White,
                    backgroundColor = Color(0xFFAD1457),
                    upperText = "Sprint 23",
                ),
            )
        }

        // Friday
        fri?.let { d ->
            events.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Team Standup",
                    shortTitle = "Standup",
                    subTitle = "Meeting Room A",
                    timeSpan = TimeSpan(LocalTime.of(9, 0), LocalTime.of(9, 30)),
                    textColor = Color.White,
                    backgroundColor = Color(0xFF0277BD),
                ),
            )
            events.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Knowledge Sharing",
                    shortTitle = "KnowShare",
                    subTitle = "Virtual",
                    timeSpan = TimeSpan(LocalTime.of(11, 0), LocalTime.of(12, 0)),
                    textColor = Color.White,
                    backgroundColor = Color(0xFF00695C),
                ),
            )
        }

        // Multi-day: team offsite
        if (days.size >= 2) {
            events.add(
                Event.MultiDay(
                    id = (nextId++).toString(),
                    date = days[0],
                    title = "Team Offsite",
                    shortTitle = "Offsite",
                    lastDate = days[1],
                    textColor = Color.White,
                    backgroundColor = Color(0xFF00838F),
                ),
            )
        }

        // All-day
        fri?.let { d ->
            events.add(
                Event.AllDay(
                    id = nextId.toString(),
                    date = d,
                    title = "Casual Friday",
                    shortTitle = "Casual",
                    textColor = Color.White,
                    backgroundColor = Color(0xFF7B1FA2),
                ),
            )
        }

        return WeekData(dateRange, LocalTime.of(8, 0), LocalTime.of(18, 0), events)
    }
}
