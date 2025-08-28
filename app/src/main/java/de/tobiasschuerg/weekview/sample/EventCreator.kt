package de.tobiasschuerg.weekview.sample

import android.graphics.Color
import de.tobiasschuerg.weekview.data.Event
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
    private val weekDays =
        DayOfWeek.entries
            // Filter the weekend
            .filter { it != DayOfWeek.SATURDAY }
            .filter { it != DayOfWeek.SUNDAY }

    private const val MIN_EVENT_LENGTH = 30
    private const val MAX_EVENT_LENGTH = 90

    val weekData: WeekData by lazy {
        WeekData().apply {
            var startTime: LocalTime
            weekDays
                .map { dayOfWeek ->
                    startTime = LocalTime.of(8 + random.nextInt(2), random.nextInt(60))
                    while (startTime.isBefore(LocalTime.of(15, 0))) {
                        val endTime = startTime.plusMinutes(MIN_EVENT_LENGTH + random.nextInt(MAX_EVENT_LENGTH - MIN_EVENT_LENGTH).toLong())
                        this.add(createSampleEntry(dayOfWeek, startTime, endTime))
                        startTime = endTime.plusMinutes(5 + random.nextInt(95).toLong())
                    }
                }
        }
    }

    fun createRandomEvent(): Event.Single {
        val startTime = LocalTime.of(8 + random.nextInt(8), random.nextInt(60))
        val endTime = startTime.plusMinutes((30 + random.nextInt(60)).toLong())
        val day = weekDays.shuffled().first()
        return createSampleEntry(day, startTime, endTime)
    }

    private fun createSampleEntry(
        day: DayOfWeek,
        startTime: LocalTime,
        endTime: LocalTime,
    ): Event.Single {
        val name = titles[random.nextInt(titles.size)]
        val subTitle = subTitles[random.nextInt(subTitles.size)]
        return Event.Single(
            id = random.nextLong(),
            date = LocalDate.now().with(day),
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
