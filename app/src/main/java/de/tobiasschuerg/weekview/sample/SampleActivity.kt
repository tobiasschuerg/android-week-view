package de.tobiasschuerg.weekview.sample

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ContextMenu
import android.view.View
import android.widget.Toast
import com.jakewharton.threetenabp.AndroidThreeTen
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.WeekData
import de.tobiasschuerg.weekview.data.WeekViewConfig
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

        scroll_view.removeAllViews()

        // create and fill a week data object
        val data: WeekData = createSampleData()
        // set up the WeekView with the data
        val weekView = WeekView(applicationContext, WeekViewConfig(), data)
        // optional: add an onClickListener for each event
        weekView.setLessonClickListener { Toast.makeText(applicationContext, it.event.fullName, Toast.LENGTH_SHORT).show() }
        // optional: register a context menu to each event
        registerForContextMenu(weekView)

        // add the view to a layout
        scroll_view.addView(weekView)
    }

    /**
     * Creates a week data object with random events.
     */
    private fun createSampleData(): WeekData {
        val data = WeekData()
        (0..20).map { data.add(createSampleEntry()) }
        return data
    }

    private fun createSampleEntry(): Event.Single {
        val startTime = LocalTime.of(8 + random.nextInt(8), random.nextInt(60))
        val name = name[random.nextInt(name.size)]
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
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        val (event) = menuInfo as EventView.LessonViewContextInfo
        menu.setHeaderTitle(event.fullName)
        menu.add("First Option")
        menu.add("Second Option")
        menu.add("Third Option")
        super.onCreateContextMenu(menu, v, menuInfo)
    }

}
