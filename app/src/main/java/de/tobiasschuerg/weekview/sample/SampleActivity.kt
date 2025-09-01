package de.tobiasschuerg.weekview.sample

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
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
        ViewCompat.setOnApplyWindowInsetsListener(layout) { v, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                top = systemInsets.top,
                bottom = systemInsets.bottom,
                left = systemInsets.left,
                right = systemInsets.right,
            )
            insets
        }
        setContentView(layout)
    }
}
