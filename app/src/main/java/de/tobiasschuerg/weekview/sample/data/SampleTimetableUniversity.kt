package de.tobiasschuerg.weekview.sample.data

import androidx.compose.ui.graphics.Color
import de.tobiasschuerg.weekview.model.Event
import de.tobiasschuerg.weekview.model.LocalDateRange
import de.tobiasschuerg.weekview.model.TimeSpan
import de.tobiasschuerg.weekview.model.WeekData
import java.time.LocalTime

/** University sample: typical lecture schedule, including overlapping electives and an all-day deadline. */
object SampleTimetableUniversity {
    fun create(dateRange: LocalDateRange): WeekData {
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
                id = (nextId++).toString(),
                date = mon,
                title = "Linear Algebra",
                shortTitle = "LinAlg",
                subTitle = "Room A101",
                timeSpan = TimeSpan(LocalTime.of(8, 15), LocalTime.of(9, 45)),
                textColor = Color.White,
                backgroundColor = Color(0xFF1565C0),
                upperText = "Prof. Schmidt",
            ),
        )
        weekData.add(
            Event.Single(
                id = (nextId++).toString(),
                date = mon,
                title = "Physics I",
                shortTitle = "Phys",
                subTitle = "Room B202",
                timeSpan = TimeSpan(LocalTime.of(10, 15), LocalTime.of(11, 45)),
                textColor = Color.White,
                backgroundColor = Color(0xFF2E7D32),
                upperText = "Prof. Weber",
            ),
        )
        weekData.add(
            Event.Single(
                id = (nextId++).toString(),
                date = mon,
                title = "Programming Lab",
                shortTitle = "ProgLab",
                subTitle = "PC Pool 3",
                timeSpan = TimeSpan(LocalTime.of(14, 0), LocalTime.of(15, 30)),
                textColor = Color.White,
                backgroundColor = Color(0xFFE65100),
            ),
        )

        // Tuesday
        tue?.let { d ->
            weekData.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Analysis I",
                    shortTitle = "Ana",
                    subTitle = "Auditorium",
                    timeSpan = TimeSpan(LocalTime.of(8, 15), LocalTime.of(9, 45)),
                    textColor = Color.White,
                    backgroundColor = Color(0xFF6A1B9A),
                    upperText = "Prof. Müller",
                ),
            )
            weekData.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Computer Architecture",
                    shortTitle = "CompArch",
                    subTitle = "Room C305",
                    timeSpan = TimeSpan(LocalTime.of(10, 15), LocalTime.of(11, 45)),
                    textColor = Color.White,
                    backgroundColor = Color(0xFF00838F),
                    upperText = "Dr. Braun",
                ),
            )
            weekData.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Tutorial: LinAlg",
                    shortTitle = "Tut LinAlg",
                    subTitle = "Room A103",
                    timeSpan = TimeSpan(LocalTime.of(14, 0), LocalTime.of(15, 30)),
                    textColor = Color.White,
                    backgroundColor = Color(0xFF1565C0),
                ),
            )
        }

        // Wednesday — includes overlapping electives
        wed?.let { d ->
            // Two electives at the same time (overlap)
            weekData.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Elective: Machine Learning",
                    shortTitle = "ML",
                    subTitle = "Room E201",
                    timeSpan = TimeSpan(LocalTime.of(8, 15), LocalTime.of(9, 45)),
                    textColor = Color.White,
                    backgroundColor = Color(0xFF4E342E),
                    upperText = "Prof. Richter",
                ),
            )
            weekData.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Elective: Databases",
                    shortTitle = "DB",
                    subTitle = "Room C102",
                    timeSpan = TimeSpan(LocalTime.of(8, 15), LocalTime.of(9, 45)),
                    textColor = Color.White,
                    backgroundColor = Color(0xFF33691E),
                    upperText = "Dr. Fischer",
                ),
            )
            weekData.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Physics I",
                    shortTitle = "Phys",
                    subTitle = "Room B202",
                    timeSpan = TimeSpan(LocalTime.of(10, 15), LocalTime.of(11, 45)),
                    textColor = Color.White,
                    backgroundColor = Color(0xFF2E7D32),
                    upperText = "Prof. Weber",
                ),
            )
            weekData.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Algorithms & Data Structures",
                    shortTitle = "ADS",
                    subTitle = "Room D110",
                    timeSpan = TimeSpan(LocalTime.of(12, 15), LocalTime.of(13, 45)),
                    textColor = Color.White,
                    backgroundColor = Color(0xFFC62828),
                    upperText = "Prof. Koch",
                ),
            )
            weekData.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Tutorial: Analysis",
                    shortTitle = "Tut Ana",
                    subTitle = "Room A205",
                    timeSpan = TimeSpan(LocalTime.of(16, 0), LocalTime.of(17, 30)),
                    textColor = Color.White,
                    backgroundColor = Color(0xFF6A1B9A),
                ),
            )
        }

        // Thursday
        thu?.let { d ->
            weekData.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Linear Algebra",
                    shortTitle = "LinAlg",
                    subTitle = "Room A101",
                    timeSpan = TimeSpan(LocalTime.of(8, 15), LocalTime.of(9, 45)),
                    textColor = Color.White,
                    backgroundColor = Color(0xFF1565C0),
                    upperText = "Prof. Schmidt",
                ),
            )
            weekData.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Analysis I",
                    shortTitle = "Ana",
                    subTitle = "Auditorium",
                    timeSpan = TimeSpan(LocalTime.of(10, 15), LocalTime.of(11, 45)),
                    textColor = Color.White,
                    backgroundColor = Color(0xFF6A1B9A),
                    upperText = "Prof. Müller",
                ),
            )
            weekData.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Physics Lab",
                    shortTitle = "PhysLab",
                    subTitle = "Lab B01",
                    timeSpan = TimeSpan(LocalTime.of(14, 0), LocalTime.of(17, 0)),
                    textColor = Color.White,
                    backgroundColor = Color(0xFF2E7D32),
                ),
            )
        }

        // Friday
        fri?.let { d ->
            weekData.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Algorithms & Data Structures",
                    shortTitle = "ADS",
                    subTitle = "Room D110",
                    timeSpan = TimeSpan(LocalTime.of(10, 15), LocalTime.of(11, 45)),
                    textColor = Color.White,
                    backgroundColor = Color(0xFFC62828),
                    upperText = "Prof. Koch",
                ),
            )
            weekData.add(
                Event.Single(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Programming Lab",
                    shortTitle = "ProgLab",
                    subTitle = "PC Pool 3",
                    timeSpan = TimeSpan(LocalTime.of(12, 15), LocalTime.of(13, 45)),
                    textColor = Color.White,
                    backgroundColor = Color(0xFFE65100),
                ),
            )
        }

        // Exam period all-day event
        wed?.let { d ->
            weekData.add(
                Event.AllDay(
                    id = (nextId++).toString(),
                    date = d,
                    title = "Enrollment Deadline",
                    shortTitle = "Deadline",
                    textColor = Color.White,
                    backgroundColor = Color(0xFFFF6F00),
                ),
            )
        }

        return weekData
    }
}
