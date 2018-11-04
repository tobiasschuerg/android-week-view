package de.tobiasschuerg.weekview.sample

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.jakewharton.threetenabp.AndroidThreeTen
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.data.WeekData
import de.tobiasschuerg.weekview.view.EventView
import kotlinx.android.synthetic.main.activity_sample.*
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import java.util.*
import kotlin.math.absoluteValue


class SampleActivity : AppCompatActivity() {

    private val random = Random()
    private val titles = listOf("Title", "Event", "Android", "Sport", "Yoga", "Shopping", "Meeting")
    private val subTitles = listOf("City Center", "@Home", "urgent", "New York", null)

    private val minEventLength = 30
    private val maxEventLength = 90

    private val data: WeekData by lazy {
        WeekData().apply {
            var startTime: LocalTime
            (1..7).filter { it != Calendar.SATURDAY }.map {
                startTime = LocalTime.of(8 + random.nextInt(1), random.nextInt(60))
                while (startTime.isBefore(LocalTime.of(15, 0))) {
                    val endTime = startTime.plusMinutes(minEventLength + random.nextInt(maxEventLength - minEventLength).toLong())
                    this.add(createSampleEntry(it, startTime, endTime))
                    startTime = endTime.plusMinutes(5 + random.nextInt(95).toLong())
                }
            }
            earliestStart = LocalTime.MIN
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidThreeTen.init(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)


        val config = EventConfig(showSubtitle = false, showTimeEnd = false)
        week_view_foo.eventConfig = config

        // set up the WeekView with the data
        week_view_foo.addLessonsToTimetable(data)
        // optional: add an onClickListener for each event
        week_view_foo.setLessonClickListener {
            Toast.makeText(applicationContext, "Removing " + it.event.title, Toast.LENGTH_SHORT).show()
            week_view_foo.removeView(it)
        }
        // optional: register a context menu to each event
        registerForContextMenu(week_view_foo)

        week_view_foo.setOnTouchListener { v, event ->
            when (event.pointerCount) {
                1 -> {
                    Log.d("Scroll", "1-pointer touch")
                    v.parent.requestDisallowInterceptTouchEvent(false)

                }
                2 -> {
                    Log.d("Zoom", "2-pointer touch")
                    v.parent.requestDisallowInterceptTouchEvent(true)

                }
            }
            false
        }
    }

    private fun createSampleEntry(day: Int, startTime: LocalTime, endTime: LocalTime): Event.Single {
        val name = titles[random.nextInt(titles.size)]
        val subTitle = subTitles[random.nextInt(subTitles.size)]
        return Event.Single(
                random.nextLong().absoluteValue,
                LocalDate.now(),
                name,
                name,
                subTitle,
                day,
                startTime,
                endTime,
                null, //"upper",
                null, // "lower",
                Color.WHITE,
                randomColor()
        )
    }

    private fun randomColor(): Int {
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        val (event) = menuInfo as EventView.LessonViewContextInfo
        menu.setHeaderTitle(event.title)
        menu.add("First Option")
        menu.add("Second Option")
        menu.add("Third Option")
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("Add").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val startTime = LocalTime.of(8 + random.nextInt(8), random.nextInt(60))
        val endTime = startTime.plusMinutes((30 + random.nextInt(60)).toLong())
        val day = random.nextInt(7) + 1
        val newEvents = listOf(createSampleEntry(day, startTime, endTime))
        newEvents.forEach { data.add(it) }
        week_view_foo.addLessonsToTimetable(data)
        registerForContextMenu(week_view_foo)
        return true
    }

}
