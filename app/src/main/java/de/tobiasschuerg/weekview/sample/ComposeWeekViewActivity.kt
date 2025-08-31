package de.tobiasschuerg.weekview.sample

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import de.tobiasschuerg.weekview.compose.WeekViewCompose
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.data.WeekData
import de.tobiasschuerg.weekview.data.WeekViewConfig

class ComposeWeekViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("weekview_prefs", MODE_PRIVATE)
        val weekViewConfig = WeekViewConfig(prefs)
        setContentView(FrameLayout(this).apply { id = R.id.content })
        val composeView = ComposeView(this)

        weekViewConfig.scalingFactor = 1f

        composeView.setContent {
            var events by remember { mutableStateOf(EventCreator.weekData.getSingleEvents()) }

            WeekViewCompose(
                weekData = WeekData().apply { events.forEach { this.add(it) } },
                eventConfig = EventConfig(),
                weekViewConfig = weekViewConfig,
                modifier = Modifier,
                onEventClick = { eventId ->
                    val event: Event.Single = events.single { it.id == eventId }
                    Toast.makeText(this, "Clicked event ${event.title}", Toast.LENGTH_SHORT).show()
                },
                onEventLongPress = { eventId ->
                    events = events.filterNot { it.id == eventId }
                    Toast.makeText(this, "Removed event $eventId", Toast.LENGTH_SHORT).show()
                },
            )
        }
        (findViewById<FrameLayout>(R.id.content)).addView(composeView)
    }
}
