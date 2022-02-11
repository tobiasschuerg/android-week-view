package de.tobiasschuerg.weekview.sample

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.threetenabp.AndroidThreeTen
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.util.TimeSpan
import de.tobiasschuerg.weekview.view.EventView
import de.tobiasschuerg.weekview.view.WeekView
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.temporal.ChronoUnit

class SampleActivity : AppCompatActivity() {

    private val weekView: WeekView by lazy { findViewById<WeekView>(R.id.week_view) }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidThreeTen.init(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        val config = EventConfig(showSubtitle = false, showTimeEnd = false)
        weekView.eventConfig = config

        // set up the WeekView with the data
        weekView.addEvents(EventCreator.weekData)

        val nowEvent = Event.Single(
            id = 1337,
            date = LocalDate.now(),
            title = "Current hour",
            shortTitle = "Now",
            timeSpan = TimeSpan.of(LocalTime.now().truncatedTo(ChronoUnit.HOURS), Duration.ofHours(1)),
            backgroundColor = Color.RED,
            textColor = Color.WHITE
        )
        weekView.addEvent(nowEvent)

        // optional: add an onClickListener for each event
        weekView.setLessonClickListener {
            Toast.makeText(applicationContext, "Removing " + it.event.title, Toast.LENGTH_SHORT).show()
            weekView.removeView(it)
        }

        // optional: register a context menu to each event
        registerForContextMenu(weekView)

        weekView.setOnTouchListener { v, event ->
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
        menu.add("Clear").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.title) {
            "Add" -> addRandomItem()
            "Clear" -> removeAllEvents()
        }
        return true
    }

    private fun removeAllEvents() {
        Log.i(TAG, "removeAllEvents()")
        weekView.removeViews(1, weekView.childCount - 1)
    }

    private fun addRandomItem() {
        Log.i(TAG, "addRandomItem()")
        val newEvent = EventCreator.createRandomEvent()
        weekView.addEvent(newEvent)
    }

    companion object {
        private const val TAG = "SampleActivity"
    }
}
