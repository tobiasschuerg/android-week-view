package de.tobiasschuerg.weekview.sample

import android.graphics.Color
import androidx.core.graphics.toColorInt
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.LocalDateRange
import de.tobiasschuerg.weekview.data.WeekData
import de.tobiasschuerg.weekview.util.TimeSpan
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.Random

object EventCreator {
    private val random = Random()
    private val titles = listOf("Title", "Event", "Android", "Sport", "Yoga", "Shopping", "Meeting")
    private val subTitles = listOf("City Center", "@Home", "Office", "New York", null)

    private const val MIN_EVENT_LENGTH = 30
    private const val MAX_EVENT_LENGTH = 90

    // Create a week range for the current week
    private val today = LocalDate.now()
    private val startOfWeek = today.with(DayOfWeek.MONDAY)
    private val endOfWeek = today.with(DayOfWeek.FRIDAY)
    private val weekRange = LocalDateRange(startOfWeek, endOfWeek)

    fun createEmptyWeekData(dateRange: LocalDateRange): WeekData =
        WeekData(
            dateRange = dateRange,
            start = LocalTime.of(9, 0),
            end = LocalTime.of(15, 0),
        )

    fun createWeekData(dateRange: LocalDateRange): WeekData {
        val random = Random()
        val weekData = createEmptyWeekData(dateRange)
        var startTime: LocalTime
        for (date in dateRange) {
            startTime = LocalTime.of(8 + random.nextInt(2), random.nextInt(60))
            while (startTime.isBefore(LocalTime.of(14, 0))) {
                val endTime = startTime.plusMinutes(MIN_EVENT_LENGTH + random.nextInt(MAX_EVENT_LENGTH - MIN_EVENT_LENGTH).toLong())
                weekData.add(createSampleEntry(date, startTime, endTime))
                startTime = endTime.plusMinutes(5 + random.nextInt(95).toLong())
            }
        }
        repeat(10) {
            weekData.add(createRandomEvent(dateRange))
        }
        // Add sample all-day events
        weekData.add(
            Event.AllDay(
                id = random.nextLong(),
                date = dateRange.start,
                title = "Holiday",
                shortTitle = "Holiday",
                textColor = Color.WHITE,
                backgroundColor = "#E91E63".toColorInt(),
            ),
        )
        weekData.add(
            Event.AllDay(
                id = random.nextLong(),
                date = dateRange.start.plusDays(2),
                title = "Team Offsite",
                shortTitle = "Offsite",
                textColor = Color.WHITE,
                backgroundColor = "#4CAF50".toColorInt(),
            ),
        )
        weekData.add(
            Event.AllDay(
                id = random.nextLong(),
                date = dateRange.start.plusDays(2),
                title = "Deadline",
                shortTitle = "Deadline",
                textColor = Color.WHITE,
                backgroundColor = "#FF9800".toColorInt(),
            ),
        )

        // add just a single event at 9:00
        val endOfWeek = dateRange.endInclusive
        weekData.add(
            Event.Single(
                id = random.nextLong(),
                date = endOfWeek,
                title = "Single Event",
                shortTitle = "SE",
                subTitle = "subtitle",
                timeSpan = TimeSpan(LocalTime.of(21, 0), LocalTime.of(23, 20)),
                textColor = Color.WHITE,
                backgroundColor = "#FF0000".toColorInt(),
                upperText = "upper",
                lowerText = "lower",
            ),
        )
        return weekData
    }

    fun createRandomEvent(dateRange: LocalDateRange): Event.Single {
        val random = Random()
        val startTime = LocalTime.of(8 + random.nextInt(8), random.nextInt(60))
        val endTime = startTime.plusMinutes((30 + random.nextInt(60)).toLong())
        val date = dateRange.toList().random()
        return createSampleEntry(date, startTime, endTime)
    }

    private fun createSampleEntry(
        date: LocalDate,
        startTime: LocalTime,
        endTime: LocalTime,
    ): Event.Single {
        val name = titles[random.nextInt(titles.size)]
        val subTitle = subTitles[random.nextInt(subTitles.size)]
        return Event.Single(
            id = random.nextLong(),
            date = date,
            title = name,
            shortTitle = name,
            subTitle = subTitle,
            timeSpan = TimeSpan(startTime, endTime),
            textColor = Color.WHITE,
            backgroundColor = randomColor(),
        )
    }

    private fun randomColor(): Int {
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
    }
}
