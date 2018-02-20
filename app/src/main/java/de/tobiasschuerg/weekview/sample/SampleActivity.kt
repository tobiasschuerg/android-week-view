package de.tobiasschuerg.weekview.sample

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.jakewharton.threetenabp.AndroidThreeTen
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.WeekData
import de.tobiasschuerg.weekview.view.EventView
import de.tobiasschuerg.weekview.view.WeekView
import kotlinx.android.synthetic.main.activity_sample.*
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import java.util.*
import kotlin.math.absoluteValue

class SampleActivity : AppCompatActivity() {

    private val random = Random()
    private val name = listOf("Foo", "Bar", "Android")

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidThreeTen.init(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        // create and fill a week data object
        val data: List<Event.Single> = createSampleData().getSingleEvents()
        // set up the WeekView with the data
        findViewById<WeekView>(R.id.week_view_foo).addLessonsToTimetable(data)
        // optional: add an onClickListener for each event
        week_view_foo.setLessonClickListener { Toast.makeText(applicationContext, it.event.fullName, Toast.LENGTH_SHORT).show() }
        // optional: register a context menu to each event
        registerForContextMenu(week_view_foo)
    }

    /**
     * Creates a week data object with random events.
     */
    private fun createSampleData(): WeekData {
        val data = WeekData()
        (0..10).map { data.add(createSampleEntry(it)) }
        return data
    }

    private fun createSampleEntry(it: Int): Event.Single {
        val startTime = LocalTime.of(8 + random.nextInt(8), random.nextInt(60))
        val name = name[random.nextInt(name.size)] + it
        return Event.Single(
                random.nextLong().absoluteValue,
                LocalDate.now(),
                name,
                name,
                random.nextInt(7) + 1,
                startTime,
                startTime.plusMinutes(20 + random.nextInt(60).toLong()),
                null,
                null,
                null,
                Color.WHITE,
                randomColor()
        )
    }

    private fun randomColor(): Int {
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        val (event) = menuInfo as EventView.LessonViewContextInfo
        menu.setHeaderTitle(event.fullName)
        menu.add("First Option")
        menu.add("Second Option")
        menu.add("Third Option")
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("Add").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        week_view_foo.addLessonsToTimetable(listOf(createSampleEntry(0)))
        registerForContextMenu(week_view_foo)
        return true
    }

}
