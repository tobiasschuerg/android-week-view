package de.tobiasschuerg.weekview

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jakewharton.threetenabp.AndroidThreeTen
import de.tobiasschuerg.weekview.data.Lesson
import de.tobiasschuerg.weekview.data.TimeTableConfig
import de.tobiasschuerg.weekview.data.TimetableData
import de.tobiasschuerg.weekview.data.TimetableItem
import de.tobiasschuerg.weekview.view.TimetableView
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


        layout.removeAllViews()
        val data = createSampleData()
        layout.addView(TimetableView(applicationContext, TimeTableConfig(), data))

    }

    private fun createSampleData(): TimetableData {
        val data = TimetableData()
        for (i in 0..20) {
            data.add(createSampleEntry())
        }
        return data
    }

    private fun createSampleEntry(): TimetableItem.Regular {
        val startTime = LocalTime.of(8 + random.nextInt(8), random.nextInt(60))
        return TimetableItem.Regular(LocalDate.now(), Lesson(
                random.nextLong().absoluteValue,
                "Hello World",
                name[random.nextInt(name.size)],
                random.nextInt(7) + 1,
                startTime,
                startTime.plusMinutes(20 + random.nextInt(60).toLong()),
                null,
                null,
                null,
                Color.WHITE,
                randomColor()
        ))
    }

    private fun randomColor(): Int {
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

}
