package de.tobiasschuerg.weekview.sample

import android.graphics.Color
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
    private val subTitles = listOf("City Center", "@Home", "urgent", "New York", null)

    private const val MIN_EVENT_LENGTH = 30
    private const val MAX_EVENT_LENGTH = 90

    // Create a week range for the current week
    private val today = LocalDate.now()
    private val startOfWeek = today.with(DayOfWeek.MONDAY)
    private val endOfWeek = today.with(DayOfWeek.FRIDAY)
    private val weekRange = LocalDateRange(startOfWeek, endOfWeek)

    val weekData: WeekData by lazy {
        WeekData(weekRange).apply {
            var startTime: LocalTime
            for (date in weekRange) {
                startTime = LocalTime.of(8 + random.nextInt(2), random.nextInt(60))
                while (startTime.isBefore(LocalTime.of(15, 0))) {
                    val endTime = startTime.plusMinutes(MIN_EVENT_LENGTH + random.nextInt(MAX_EVENT_LENGTH - MIN_EVENT_LENGTH).toLong())
                    this.add(createSampleEntry(date, startTime, endTime))
                    startTime = endTime.plusMinutes(5 + random.nextInt(95).toLong())
                }
            }
            // add some random events so that we get duplicates
            repeat(10) {
                this.add(createRandomEvent())
            }
        }
    }

    fun createRandomEvent(): Event.Single {
        val startTime = LocalTime.of(8 + random.nextInt(8), random.nextInt(60))
        val endTime = startTime.plusMinutes((30 + random.nextInt(60)).toLong())
        val date = weekRange.toList().random()
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
