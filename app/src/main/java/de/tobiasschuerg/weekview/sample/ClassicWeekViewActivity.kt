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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.util.TimeSpan
import de.tobiasschuerg.weekview.view.EventView
import de.tobiasschuerg.weekview.view.WeekView
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class ClassicWeekViewActivity : AppCompatActivity() {
    private val weekView: WeekView by lazy { findViewById(R.id.week_view) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sample)
        val root = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                top = systemInsets.top,
                bottom = systemInsets.bottom,
                left = systemInsets.left,
                right = systemInsets.right,
            )
            insets
        }
        val config = EventConfig(showSubtitle = false, showTimeEnd = false)
        weekView.eventConfig = config
        weekView.setShowNowIndicator(true)
        weekView.addEvents(EventCreator.weekData)
        val nowEvent =
            Event.Single(
                id = 1337,
                date = LocalDate.now(),
                title = "Current hour",
                shortTitle = "Now",
                timeSpan = TimeSpan.of(LocalTime.now().truncatedTo(ChronoUnit.HOURS), Duration.ofHours(1).minusNanos(1)),
                backgroundColor = Color.RED,
                textColor = Color.WHITE,
            )
        weekView.addEvent(nowEvent)

        // Set transition name for shared element transitions
        weekView.setEventTransitionName("event_transition")

        weekView.setEventClickListener {
            Toast.makeText(applicationContext, "Removing " + it.event.title, Toast.LENGTH_SHORT).show()
            weekView.removeView(it)
        }
        registerForContextMenu(weekView)
        weekView.setOnTouchListener { v, event ->
            when (event.pointerCount) {
                1 -> {
                    Log.d("Scroll", "1-pointer touch")
                    v.parent.requestDisallowInterceptTouchEvent(false)
                    if (event.action == android.view.MotionEvent.ACTION_UP) {
                        v.performClick()
                    }
                }
                2 -> {
                    Log.d("Zoom", "2-pointer touch")
                    v.parent.requestDisallowInterceptTouchEvent(true)
                }
            }
            false
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo,
    ) {
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
            "Add" -> {
                Log.i(TAG, "add option clicked")
                weekView.addEvent(EventCreator.createRandomEvent())
            }
            "Clear" -> {
                Log.i(TAG, "clear option clicked")
                weekView.removeAllEvents()
            }
        }
        return true
    }

    companion object {
        private const val TAG = "ClassicWeekViewActivity"
    }
}
