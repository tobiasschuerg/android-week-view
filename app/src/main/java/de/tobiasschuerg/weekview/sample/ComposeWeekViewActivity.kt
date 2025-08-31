package de.tobiasschuerg.weekview.sample

import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import de.tobiasschuerg.weekview.compose.WeekViewCompose
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.data.WeekViewConfig

class ComposeWeekViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("weekview_prefs", MODE_PRIVATE)
        val weekViewConfig = WeekViewConfig(prefs)
        setContentView(FrameLayout(this).apply { id = android.R.id.content })
        val composeView = ComposeView(this)

        weekViewConfig.scalingFactor = 1.5f

        composeView.setContent {
            WeekViewCompose(
                weekData = EventCreator.weekData,
                eventConfig = EventConfig(),
                weekViewConfig = weekViewConfig,
                modifier = Modifier,
            )
        }
        (findViewById<FrameLayout>(android.R.id.content)).addView(composeView)
    }
}
