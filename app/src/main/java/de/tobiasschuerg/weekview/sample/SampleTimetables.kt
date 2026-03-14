package de.tobiasschuerg.weekview.sample

import android.graphics.Color
import androidx.core.graphics.toColorInt
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.LocalDateRange
import de.tobiasschuerg.weekview.data.WeekData
import de.tobiasschuerg.weekview.util.TimeSpan
import java.time.LocalTime

/**
 * Pre-defined sample timetables for the demo app.
 * Each timetable returns a [WeekData] populated with fixed, realistic events.
 */
object SampleTimetables {
    enum class Timetable(
        val label: String,
        val days: Int = 5,
    ) {
        UNIVERSITY("University"),
        WORK("Work"),
        SCHOOL("School"),
        CONFERENCE("Conference", days = 3),
    }

    fun create(
        timetable: Timetable,
        dateRange: LocalDateRange,
    ): WeekData {
        return when (timetable) {
            Timetable.UNIVERSITY -> createUniversity(dateRange)
            Timetable.WORK -> createWork(dateRange)
            Timetable.SCHOOL -> createSchool(dateRange)
            Timetable.CONFERENCE -> createConference(dateRange)
        }
    }

    private fun createUniversity(dateRange: LocalDateRange): WeekData {
        val days = dateRange.toList()
        val weekData = WeekData(dateRange, LocalTime.of(8, 0), LocalTime.of(18, 0))

        val mon = days[0]
        val tue = days.getOrNull(1)
        val wed = days.getOrNull(2)
        val thu = days.getOrNull(3)
        val fri = days.getOrNull(4)

        var nextId = 1L

        // Monday
        weekData.add(
            Event.Single(
                id = nextId++,
                date = mon,
                title = "Linear Algebra",
                shortTitle = "LinAlg",
                subTitle = "Room A101",
                timeSpan = TimeSpan(LocalTime.of(8, 15), LocalTime.of(9, 45)),
                textColor = Color.WHITE,
                backgroundColor = "#1565C0".toColorInt(),
                upperText = "Prof. Schmidt",
            ),
        )
        weekData.add(
            Event.Single(
                id = nextId++,
                date = mon,
                title = "Physics I",
                shortTitle = "Phys",
                subTitle = "Room B202",
                timeSpan = TimeSpan(LocalTime.of(10, 15), LocalTime.of(11, 45)),
                textColor = Color.WHITE,
                backgroundColor = "#2E7D32".toColorInt(),
                upperText = "Prof. Weber",
            ),
        )
        weekData.add(
            Event.Single(
                id = nextId++,
                date = mon,
                title = "Programming Lab",
                shortTitle = "ProgLab",
                subTitle = "PC Pool 3",
                timeSpan = TimeSpan(LocalTime.of(14, 0), LocalTime.of(15, 30)),
                textColor = Color.WHITE,
                backgroundColor = "#E65100".toColorInt(),
            ),
        )

        // Tuesday
        tue?.let { d ->
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Analysis I",
                    shortTitle = "Ana",
                    subTitle = "Auditorium",
                    timeSpan = TimeSpan(LocalTime.of(8, 15), LocalTime.of(9, 45)),
                    textColor = Color.WHITE,
                    backgroundColor = "#6A1B9A".toColorInt(),
                    upperText = "Prof. Müller",
                ),
            )
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Computer Architecture",
                    shortTitle = "CompArch",
                    subTitle = "Room C305",
                    timeSpan = TimeSpan(LocalTime.of(10, 15), LocalTime.of(11, 45)),
                    textColor = Color.WHITE,
                    backgroundColor = "#00838F".toColorInt(),
                    upperText = "Dr. Braun",
                ),
            )
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Tutorial: LinAlg",
                    shortTitle = "Tut LinAlg",
                    subTitle = "Room A103",
                    timeSpan = TimeSpan(LocalTime.of(14, 0), LocalTime.of(15, 30)),
                    textColor = Color.WHITE,
                    backgroundColor = "#1565C0".toColorInt(),
                ),
            )
        }

        // Wednesday — includes overlapping electives
        wed?.let { d ->
            // Two electives at the same time (overlap)
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Elective: Machine Learning",
                    shortTitle = "ML",
                    subTitle = "Room E201",
                    timeSpan = TimeSpan(LocalTime.of(8, 15), LocalTime.of(9, 45)),
                    textColor = Color.WHITE,
                    backgroundColor = "#4E342E".toColorInt(),
                    upperText = "Prof. Richter",
                ),
            )
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Elective: Databases",
                    shortTitle = "DB",
                    subTitle = "Room C102",
                    timeSpan = TimeSpan(LocalTime.of(8, 15), LocalTime.of(9, 45)),
                    textColor = Color.WHITE,
                    backgroundColor = "#33691E".toColorInt(),
                    upperText = "Dr. Fischer",
                ),
            )
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Physics I",
                    shortTitle = "Phys",
                    subTitle = "Room B202",
                    timeSpan = TimeSpan(LocalTime.of(10, 15), LocalTime.of(11, 45)),
                    textColor = Color.WHITE,
                    backgroundColor = "#2E7D32".toColorInt(),
                    upperText = "Prof. Weber",
                ),
            )
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Algorithms & Data Structures",
                    shortTitle = "ADS",
                    subTitle = "Room D110",
                    timeSpan = TimeSpan(LocalTime.of(12, 15), LocalTime.of(13, 45)),
                    textColor = Color.WHITE,
                    backgroundColor = "#C62828".toColorInt(),
                    upperText = "Prof. Koch",
                ),
            )
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Tutorial: Analysis",
                    shortTitle = "Tut Ana",
                    subTitle = "Room A205",
                    timeSpan = TimeSpan(LocalTime.of(16, 0), LocalTime.of(17, 30)),
                    textColor = Color.WHITE,
                    backgroundColor = "#6A1B9A".toColorInt(),
                ),
            )
        }

        // Thursday
        thu?.let { d ->
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Linear Algebra",
                    shortTitle = "LinAlg",
                    subTitle = "Room A101",
                    timeSpan = TimeSpan(LocalTime.of(8, 15), LocalTime.of(9, 45)),
                    textColor = Color.WHITE,
                    backgroundColor = "#1565C0".toColorInt(),
                    upperText = "Prof. Schmidt",
                ),
            )
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Analysis I",
                    shortTitle = "Ana",
                    subTitle = "Auditorium",
                    timeSpan = TimeSpan(LocalTime.of(10, 15), LocalTime.of(11, 45)),
                    textColor = Color.WHITE,
                    backgroundColor = "#6A1B9A".toColorInt(),
                    upperText = "Prof. Müller",
                ),
            )
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Physics Lab",
                    shortTitle = "PhysLab",
                    subTitle = "Lab B01",
                    timeSpan = TimeSpan(LocalTime.of(14, 0), LocalTime.of(17, 0)),
                    textColor = Color.WHITE,
                    backgroundColor = "#2E7D32".toColorInt(),
                ),
            )
        }

        // Friday
        fri?.let { d ->
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Algorithms & Data Structures",
                    shortTitle = "ADS",
                    subTitle = "Room D110",
                    timeSpan = TimeSpan(LocalTime.of(10, 15), LocalTime.of(11, 45)),
                    textColor = Color.WHITE,
                    backgroundColor = "#C62828".toColorInt(),
                    upperText = "Prof. Koch",
                ),
            )
            weekData.add(
                Event.Single(
                    id = nextId++,
                    date = d,
                    title = "Programming Lab",
                    shortTitle = "ProgLab",
                    subTitle = "PC Pool 3",
                    timeSpan = TimeSpan(LocalTime.of(12, 15), LocalTime.of(13, 45)),
                    textColor = Color.WHITE,
                    backgroundColor = "#E65100".toColorInt(),
                ),
            )
        }

        // Exam period all-day event
        wed?.let { d ->
            weekData.add(
                Event.AllDay(
                    id = nextId++,
                    date = d,
                    title = "Enrollment Deadline",
                    shortTitle = "Deadline",
                    textColor = Color.WHITE,
                    backgroundColor = "#FF6F00".toColorInt(),
                ),
            )
        }

        return weekData
    }

    private fun createWork(dateRange: LocalDateRange): WeekData {
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

    private fun createSchool(dateRange: LocalDateRange): WeekData {
        val days = dateRange.toList()
        val weekData = WeekData(dateRange, LocalTime.of(7, 30), LocalTime.of(15, 30))

        val mon = days[0]
        val tue = days.getOrNull(1)
        val wed = days.getOrNull(2)
        val thu = days.getOrNull(3)
        val fri = days.getOrNull(4)

        var nextId = 200L

        val mathColor = "#1565C0".toColorInt()
        val englishColor = "#2E7D32".toColorInt()
        val historyColor = "#BF360C".toColorInt()
        val scienceColor = "#6A1B9A".toColorInt()
        val artColor = "#E65100".toColorInt()
        val peColor = "#00838F".toColorInt()
        val musicColor = "#AD1457".toColorInt()
        val germanColor = "#283593".toColorInt()

        // Monday: 4 lessons + lunch
        weekData.add(lesson(nextId++, mon, "Mathematics", "Math", "Room 12", 8, 0, 8, 45, mathColor))
        weekData.add(lesson(nextId++, mon, "English", "Eng", "Room 7", 8, 50, 9, 35, englishColor))
        weekData.add(lesson(nextId++, mon, "History", "Hist", "Room 21", 9, 50, 10, 35, historyColor))
        weekData.add(lesson(nextId++, mon, "Science", "Sci", "Lab 2", 10, 40, 11, 25, scienceColor))
        weekData.add(lesson(nextId++, mon, "Art", "Art", "Art Room", 12, 0, 12, 45, artColor))
        weekData.add(lesson(nextId++, mon, "PE", "PE", "Gym", 12, 50, 13, 35, peColor))

        // Tuesday
        tue?.let { d ->
            weekData.add(lesson(nextId++, d, "German", "Ger", "Room 5", 8, 0, 8, 45, germanColor))
            weekData.add(lesson(nextId++, d, "Mathematics", "Math", "Room 12", 8, 50, 9, 35, mathColor))
            weekData.add(lesson(nextId++, d, "Science", "Sci", "Lab 2", 9, 50, 10, 35, scienceColor))
            weekData.add(lesson(nextId++, d, "Music", "Mus", "Music Room", 10, 40, 11, 25, musicColor))
            weekData.add(lesson(nextId++, d, "English", "Eng", "Room 7", 12, 0, 12, 45, englishColor))
        }

        // Wednesday
        wed?.let { d ->
            weekData.add(lesson(nextId++, d, "English", "Eng", "Room 7", 8, 0, 8, 45, englishColor))
            weekData.add(lesson(nextId++, d, "History", "Hist", "Room 21", 8, 50, 9, 35, historyColor))
            weekData.add(lesson(nextId++, d, "Mathematics", "Math", "Room 12", 9, 50, 10, 35, mathColor))
            weekData.add(lesson(nextId++, d, "German", "Ger", "Room 5", 10, 40, 11, 25, germanColor))
            weekData.add(lesson(nextId++, d, "PE", "PE", "Gym", 12, 0, 13, 30, peColor))
        }

        // Thursday
        thu?.let { d ->
            weekData.add(lesson(nextId++, d, "Science", "Sci", "Lab 2", 8, 0, 8, 45, scienceColor))
            weekData.add(lesson(nextId++, d, "Mathematics", "Math", "Room 12", 8, 50, 9, 35, mathColor))
            weekData.add(lesson(nextId++, d, "Art", "Art", "Art Room", 9, 50, 10, 35, artColor))
            weekData.add(lesson(nextId++, d, "German", "Ger", "Room 5", 10, 40, 11, 25, germanColor))
            weekData.add(lesson(nextId++, d, "History", "Hist", "Room 21", 12, 0, 12, 45, historyColor))
            weekData.add(lesson(nextId++, d, "Music", "Mus", "Music Room", 12, 50, 13, 35, musicColor))
        }

        // Friday (short day)
        fri?.let { d ->
            weekData.add(lesson(nextId++, d, "German", "Ger", "Room 5", 8, 0, 8, 45, germanColor))
            weekData.add(lesson(nextId++, d, "English", "Eng", "Room 7", 8, 50, 9, 35, englishColor))
            weekData.add(lesson(nextId++, d, "Mathematics", "Math", "Room 12", 9, 50, 10, 35, mathColor))
            weekData.add(lesson(nextId++, d, "Science", "Sci", "Lab 2", 10, 40, 11, 25, scienceColor))
        }

        // All-day: school trip
        thu?.let { d ->
            weekData.add(
                Event.AllDay(
                    id = nextId,
                    date = d,
                    title = "Parent-Teacher Day",
                    shortTitle = "PT Day",
                    textColor = Color.WHITE,
                    backgroundColor = "#FF6F00".toColorInt(),
                ),
            )
        }

        return weekData
    }

    private fun createConference(dateRange: LocalDateRange): WeekData {
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

    private fun lesson(
        id: Long,
        date: java.time.LocalDate,
        title: String,
        shortTitle: String,
        room: String,
        startHour: Int,
        startMin: Int,
        endHour: Int,
        endMin: Int,
        color: Int,
    ): Event.Single =
        Event.Single(
            id = id,
            date = date,
            title = title,
            shortTitle = shortTitle,
            subTitle = room,
            timeSpan = TimeSpan(LocalTime.of(startHour, startMin), LocalTime.of(endHour, endMin)),
            textColor = Color.WHITE,
            backgroundColor = color,
        )
}
