package de.tobiasschuerg.weekview.sample.data

import androidx.compose.ui.graphics.Color
import de.tobiasschuerg.weekview.model.Event
import de.tobiasschuerg.weekview.model.LocalDateRange
import de.tobiasschuerg.weekview.model.TimeSpan
import de.tobiasschuerg.weekview.model.WeekData
import java.time.LocalTime

/** Conference sample: a 3-day conference agenda with talks, workshops, and a multi-day banner event. */
object SampleTimetableConference {
    fun create(dateRange: LocalDateRange): WeekData {
        val days = dateRange.toList()
        val events = mutableListOf<Event>()

        val day1 = days[0]
        val day2 = days.getOrNull(1)
        val day3 = days.getOrNull(2)

        var nextId = 300L

        val keynoteColor = Color(0xFF1565C0)
        val workshopColor = Color(0xFFE65100)
        val talkColor = Color(0xFF2E7D32)
        val panelColor = Color(0xFF6A1B9A)
        val networkColor = Color(0xFF00838F)
        val breakColor = Color(0xFF78909C)

        // Day 1 — Opening & Keynotes
        events.add(
            Event.Single(
                id = (nextId++).toString(),
                date = day1,
                title = "Registration & Coffee",
                shortTitle = "Reg",
                subTitle = "Lobby",
                timeSpan = TimeSpan(LocalTime.of(8, 0), LocalTime.of(9, 0)),
                textColor = Color.White,
                backgroundColor = breakColor,
            ),
        )
        events.add(
            Event.Single(
                id = (nextId++).toString(),
                date = day1,
                title = "Opening Keynote",
                shortTitle = "Keynote",
                subTitle = "Main Stage",
                timeSpan = TimeSpan(LocalTime.of(9, 0), LocalTime.of(10, 30)),
                textColor = Color.White,
                backgroundColor = keynoteColor,
                upperText = "Dr. Sarah Chen",
            ),
        )
        events.add(
            Event.Single(
                id = (nextId++).toString(),
                date = day1,
                title = "Compose Internals",
                shortTitle = "Compose",
                subTitle = "Room A",
                timeSpan = TimeSpan(LocalTime.of(11, 0), LocalTime.of(12, 0)),
                textColor = Color.White,
                backgroundColor = talkColor,
                upperText = "Track: Android",
            ),
        )
        events.add(
            Event.Single(
                id = (nextId++).toString(),
                date = day1,
                title = "Lunch Break",
                shortTitle = "Lunch",
                subTitle = "Hall B",
                timeSpan = TimeSpan(LocalTime.of(12, 0), LocalTime.of(13, 0)),
                textColor = Color.White,
                backgroundColor = breakColor,
            ),
        )
        events.add(
            Event.Single(
                id = (nextId++).toString(),
                date = day1,
                title = "Workshop: Kotlin Multiplatform",
                shortTitle = "KMP",
                subTitle = "Lab 1",
                timeSpan = TimeSpan(LocalTime.of(13, 0), LocalTime.of(15, 0)),
                textColor = Color.White,
                backgroundColor = workshopColor,
            ),
        )
        events.add(
            Event.Single(
                id = (nextId++).toString(),
                date = day1,
                title = "Panel: Future of Mobile",
                shortTitle = "Panel",
                subTitle = "Main Stage",
                timeSpan = TimeSpan(LocalTime.of(15, 30), LocalTime.of(16, 30)),
                textColor = Color.White,
                backgroundColor = panelColor,
            ),
        )
        events.add(
            Event.Single(
                id = (nextId++).toString(),
                date = day1,
                title = "Networking Reception",
                shortTitle = "Network",
                subTitle = "Rooftop",
                timeSpan = TimeSpan(LocalTime.of(17, 0), LocalTime.of(19, 0)),
                textColor = Color.White,
                backgroundColor = networkColor,
            ),
        )

        // Day 2 — Deep Dives
        day2?.let { d ->
            events.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Keynote: AI in Dev Tools",
                    shortTitle = "AI Talk",
                    subTitle = "Main Stage",
                    timeSpan = TimeSpan(LocalTime.of(9, 0), LocalTime.of(10, 0)),
                    textColor = Color.White,
                    backgroundColor = keynoteColor,
                    upperText = "James Park",
                ),
            )
            events.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Performance Optimization",
                    shortTitle = "Perf",
                    subTitle = "Room A",
                    timeSpan = TimeSpan(LocalTime.of(10, 30), LocalTime.of(11, 30)),
                    textColor = Color.White,
                    backgroundColor = talkColor,
                    upperText = "Track: Android",
                ),
            )
            events.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Lunch Break",
                    shortTitle = "Lunch",
                    subTitle = "Hall B",
                    timeSpan = TimeSpan(LocalTime.of(12, 0), LocalTime.of(13, 0)),
                    textColor = Color.White,
                    backgroundColor = breakColor,
                ),
            )
            events.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Workshop: Testing Strategies",
                    shortTitle = "Testing",
                    subTitle = "Lab 1",
                    timeSpan = TimeSpan(LocalTime.of(13, 0), LocalTime.of(15, 0)),
                    textColor = Color.White,
                    backgroundColor = workshopColor,
                ),
            )
            events.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Lightning Talks",
                    shortTitle = "Lightning",
                    subTitle = "Main Stage",
                    timeSpan = TimeSpan(LocalTime.of(15, 30), LocalTime.of(17, 0)),
                    textColor = Color.White,
                    backgroundColor = talkColor,
                ),
            )
            events.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Conference Dinner",
                    shortTitle = "Dinner",
                    subTitle = "Restaurant",
                    timeSpan = TimeSpan(LocalTime.of(18, 0), LocalTime.of(19, 0)),
                    textColor = Color.White,
                    backgroundColor = networkColor,
                ),
            )
        }

        // Day 3 — Closing
        day3?.let { d ->
            events.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Unconference Sessions",
                    shortTitle = "Unconf",
                    subTitle = "Rooms A-D",
                    timeSpan = TimeSpan(LocalTime.of(9, 0), LocalTime.of(10, 30)),
                    textColor = Color.White,
                    backgroundColor = workshopColor,
                ),
            )
            events.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Panel: Open Source",
                    shortTitle = "OSS Panel",
                    subTitle = "Main Stage",
                    timeSpan = TimeSpan(LocalTime.of(11, 0), LocalTime.of(12, 0)),
                    textColor = Color.White,
                    backgroundColor = panelColor,
                ),
            )
            events.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Lunch Break",
                    shortTitle = "Lunch",
                    subTitle = "Hall B",
                    timeSpan = TimeSpan(LocalTime.of(12, 0), LocalTime.of(13, 0)),
                    textColor = Color.White,
                    backgroundColor = breakColor,
                ),
            )
            events.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Closing Keynote",
                    shortTitle = "Closing",
                    subTitle = "Main Stage",
                    timeSpan = TimeSpan(LocalTime.of(13, 30), LocalTime.of(15, 0)),
                    textColor = Color.White,
                    backgroundColor = keynoteColor,
                    upperText = "Community Awards",
                ),
            )
        }

        // Multi-day event spanning the conference
        if (days.size >= 3) {
            events.add(
                Event.MultiDay(
                    id = (nextId++).toString(),
                    date = days[0],
                    title = "DroidCon 2026",
                    shortTitle = "DroidCon",
                    lastDate = days[2],
                    textColor = Color.White,
                    backgroundColor = Color(0xFF3F51B5),
                ),
            )
        }

        return WeekData(dateRange, LocalTime.of(8, 0), LocalTime.of(19, 0), events)
    }
}
