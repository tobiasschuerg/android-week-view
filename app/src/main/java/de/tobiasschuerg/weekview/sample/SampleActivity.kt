package de.tobiasschuerg.weekview.sample

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import kotlin.jvm.java

class SampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout =
            LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                val classicBtn =
                    Button(context).apply {
                        text = "Classic WeekView"
                        setOnClickListener {
                            startActivity(Intent(context, ClassicWeekViewActivity::class.java))
                        }
                    }
                val composeBtn =
                    Button(context).apply {
                        text = "Compose WeekView"
                        setOnClickListener {
                            startActivity(Intent(context, ComposeWeekViewActivity::class.java))
                        }
                    }
                addView(classicBtn)
                addView(composeBtn)
            }
        setContentView(layout)
    }
}
