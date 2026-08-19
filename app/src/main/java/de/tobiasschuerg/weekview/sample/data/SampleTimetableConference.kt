package de.tobiasschuerg.weekview.sample.data

import android.graphics.Color
import androidx.core.graphics.toColorInt
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.LocalDateRange
import de.tobiasschuerg.weekview.data.WeekData
import de.tobiasschuerg.weekview.util.TimeSpan
import java.time.LocalTime

/** Conference sample: a 3-day conference agenda with talks, workshops, and a multi-day banner event. */
object SampleTimetableConference {
    fun create(dateRange: LocalDateRange): WeekData {
        val days = dateRange.toList()
        val weekData = WeekData(dateRange, LocalTime.of(8, 0), LocalTime.of(19, 0))

        val day1 = days[0]
        val day2 = days.getOrNull(1)
        val day3 = days.getOrNull(2)

        var nextId = 300L

        val keynoteColor = "#1565C0".toColorInt()
        val workshopColor = "#E65100".toColorInt()
        val talkColor = "#2E7D32".toColorInt()
        val panelColor = "#6A1B9A".toColorInt()
        val networkColor = "#00838F".toColorInt()
        val breakColor = "#78909C".toColorInt()

        // Day 1 — Opening & Keynotes
        weekData.add(
            Event.Single(
                id = nextId++,
                date = day1,
                title = "Registration & Coffee",
                shortTitle = "Reg",
                subTitle = "Lobby",
                timeSpan = TimeSpan(LocalTime.of(8, 0), LocalTime.of(9, 0)),
                textColor = Color.WHITE,
                backgroundColor = breakColor,
            ),
        )
        weekData.add(
            Event.Single(
                id = nextId++,
                date = day1,
                title = "Opening Keynote",
                shortTitle = "Keynote",
                subTitle = "Main Stage",
                timeSpan = TimeSpan(LocalTime.of(9, 0), LocalTime.of(10, 30)),
                textColor = Color.WHITE,
                backgroundColor = keynoteColor,
                upperText = "Dr. Sarah Chen",
            ),
        )
        weekData.add(
            Event.Single(
                id = nextId++,
                date = day1,
                title = "Compose Internals",
                shortTitle = "Compose",
                subTitle = "Room A",
                timeSpan = TimeSpan(LocalTime.of(11, 0), LocalTime.of(12, 0)),
                textColor = Color.WHITE,
                backgroundColor = talkColor,
                upperText = "Track: Android",
            ),
        )
        weekData.add(
            Event.Single(
                id = nextId++,
                date = day1,
                title = "Lunch Break",
                shortTitle = "Lunch",
                subTitle = "Hall B",
                timeSpan = TimeSpan(LocalTime.of(12, 0), LocalTime.of(13, 0)),
                textColor = Color.WHITE,
                backgroundColor = breakColor,
            ),
        )
        weekData.add(
            Event.Single(
                id = nextId++,
                date = day1,
                title = "Workshop: Kotlin Multiplatform",
                shortTitle = "KMP",
                subTitle = "Lab 1",
                timeSpan = TimeSpan(LocalTime.of(13, 0), LocalTime.of(15, 0)),
                textColor = Color.WHITE,
                backgroundColor = workshopColor,
            ),
        )
        weekData.add(
            Event.Single(
                id = nextId++,
                date = day1,
                title = "Panel: Future of Mobile",
                shortTitle = "Panel",
                subTitle = "Main Stage",
                timeSpan = TimeSpan(LocalTime.of(15, 30), LocalTime.of(16, 30)),
                textColor = Color.WHITE,
                backgroundColor = panelColor,
            ),
        )
        weekData.add(
            Event.Single(
                id = nextId++,
                date = day1,
                title = "Networking Reception",
                shortTitle = "Network",
                subTitle = "Rooftop",
                timeSpan = TimeSpan(LocalTime.of(17, 0), LocalTime.of(19, 0)),
                textColor = Color.WHITE,
                backgroundColor = networkColor,
            ),
        )

        // Day 2 — Deep Dives
        day2?.let { d ->
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Keynote: AI in Dev Tools",
                    shortTitle = "AI Talk",
                    subTitle = "Main Stage",
                    timeSpan = TimeSpan(LocalTime.of(9, 0), LocalTime.of(10, 0)),
                    textColor = Color.WHITE,
                    backgroundColor = keynoteColor,
                    upperText = "James Park",
                ),
            )
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Performance Optimization",
                    shortTitle = "Perf",
                    subTitle = "Room A",
                    timeSpan = TimeSpan(LocalTime.of(10, 30), LocalTime.of(11, 30)),
                    textColor = Color.WHITE,
                    backgroundColor = talkColor,
                    upperText = "Track: Android",
                ),
            )
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Lunch Break",
                    shortTitle = "Lunch",
                    subTitle = "Hall B",
                    timeSpan = TimeSpan(LocalTime.of(12, 0), LocalTime.of(13, 0)),
                    textColor = Color.WHITE,
                    backgroundColor = breakColor,
                ),
            )
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Workshop: Testing Strategies",
                    shortTitle = "Testing",
                    subTitle = "Lab 1",
                    timeSpan = TimeSpan(LocalTime.of(13, 0), LocalTime.of(15, 0)),
                    textColor = Color.WHITE,
                    backgroundColor = workshopColor,
                ),
            )
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Lightning Talks",
                    shortTitle = "Lightning",
                    subTitle = "Main Stage",
                    timeSpan = TimeSpan(LocalTime.of(15, 30), LocalTime.of(17, 0)),
                    textColor = Color.WHITE,
                    backgroundColor = talkColor,
                ),
            )
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Conference Dinner",
                    shortTitle = "Dinner",
                    subTitle = "Restaurant",
                    timeSpan = TimeSpan(LocalTime.of(18, 0), LocalTime.of(19, 0)),
                    textColor = Color.WHITE,
                    backgroundColor = networkColor,
                ),
            )
        }

        // Day 3 — Closing
        day3?.let { d ->
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Unconference Sessions",
                    shortTitle = "Unconf",
                    subTitle = "Rooms A-D",
                    timeSpan = TimeSpan(LocalTime.of(9, 0), LocalTime.of(10, 30)),
                    textColor = Color.WHITE,
                    backgroundColor = workshopColor,
                ),
            )
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Panel: Open Source",
                    shortTitle = "OSS Panel",
                    subTitle = "Main Stage",
                    timeSpan = TimeSpan(LocalTime.of(11, 0), LocalTime.of(12, 0)),
                    textColor = Color.WHITE,
                    backgroundColor = panelColor,
                ),
            )
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Lunch Break",
                    shortTitle = "Lunch",
                    subTitle = "Hall B",
                    timeSpan = TimeSpan(LocalTime.of(12, 0), LocalTime.of(13, 0)),
                    textColor = Color.WHITE,
                    backgroundColor = breakColor,
                ),
            )
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Closing Keynote",
                    shortTitle = "Closing",
                    subTitle = "Main Stage",
                    timeSpan = TimeSpan(LocalTime.of(13, 30), LocalTime.of(15, 0)),
                    textColor = Color.WHITE,
                    backgroundColor = keynoteColor,
                    upperText = "Community Awards",
                ),
            )
        }

        // Multi-day event spanning the conference
        if (days.size >= 3) {
            weekData.add(
                Event.MultiDay(
                    id = nextId++,
                    date = days[0],
                    title = "DroidCon 2026",
                    shortTitle = "DroidCon",
                    lastDate = days[2],
                    textColor = Color.WHITE,
                    backgroundColor = "#3F51B5".toColorInt(),
                ),
            )
        }

        return weekData
    }
}
