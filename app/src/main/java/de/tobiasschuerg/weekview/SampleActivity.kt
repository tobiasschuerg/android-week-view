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

class SampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidThreeTen.init(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)


        layout.removeAllViews()
        val data = TimetableData()

        data.add(TimetableItem.Regular(LocalDate.now(), Lesson(
                1,
                "Hello World",
                "Hi",
                Calendar.TUESDAY,
                LocalTime.NOON,
                LocalTime.NOON.plusMinutes(90),
                null,
                null,
                null,
                Color.WHITE,
                Color.BLUE
        )))

        layout.addView(TimetableView(applicationContext, TimeTableConfig(), data))

    }

}
