package de.tobiasschuerg.weekview.sample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import de.tobiasschuerg.weekview.compose.WeekViewCompose
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.data.WeekViewConfig

class ComposeWeekViewActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure edge-to-edge with proper status bar
        enableEdgeToEdge(
            statusBarStyle =
                SystemBarStyle.dark(
                    scrim = Color.Transparent.toArgb(),
                ),
        )

        val prefs = getSharedPreferences("weekview_prefs", MODE_PRIVATE)
        val weekViewConfig = WeekViewConfig(prefs)
        weekViewConfig.scalingFactor = 1f

        val eventConfig =
            EventConfig(
                alwaysUseFullName = false,
                showTimeStart = true,
                showUpperText = true,
                showSubtitle = true,
                showLowerText = true,
                showTimeEnd = true,
            )

        setContent {
            // Initialisiere die aktuelle Woche (Montag bis Freitag)
            val today = java.time.LocalDate.now()
            val initialStartOfWeek = today.with(java.time.DayOfWeek.MONDAY)
            val initialEndOfWeek = today.with(java.time.DayOfWeek.FRIDAY)
            var currentDateRange by remember {
                mutableStateOf(
                    de.tobiasschuerg.weekview.data.LocalDateRange(
                        initialStartOfWeek,
                        initialEndOfWeek,
                    ),
                )
            }
            var weekData by remember { mutableStateOf(EventCreator.createWeekData(currentDateRange)) }
            var events by remember { mutableStateOf(weekData.getSingleEvents()) }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("WeekView Demo") },
                        colors =
                            TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                    )
                },
            ) { paddingValues ->
                WeekViewCompose(
                    weekData = weekData,
                    eventConfig = eventConfig,
                    weekViewConfig = weekViewConfig,
                    modifier =
                        Modifier
                            .padding(paddingValues)
                            .fillMaxSize(),
                    onEventClick = { eventId ->
                        val event: Event.Single = events.single { it.id == eventId }
                        Toast.makeText(this@ComposeWeekViewActivity, "Clicked event ${event.title}", Toast.LENGTH_SHORT).show()
                    },
                    onEventLongPress = { eventId ->
                        events = events.filterNot { it.id == eventId }
                        Toast.makeText(this@ComposeWeekViewActivity, "Removed event $eventId", Toast.LENGTH_SHORT).show()
                    },
                    onSwipeLeft = {
                        // Nächste Woche
                        val nextStart = currentDateRange.start.plusWeeks(1)
                        val nextEnd = currentDateRange.endInclusive.plusWeeks(1)
                        currentDateRange = de.tobiasschuerg.weekview.data.LocalDateRange(nextStart, nextEnd)
                        weekData = EventCreator.createWeekData(currentDateRange)
                        events = weekData.getSingleEvents()
                        Log.d("WeekView", "Swiped left: $currentDateRange")
                    },
                    onSwipeRight = {
                        // Vorherige Woche
                        val prevStart = currentDateRange.start.minusWeeks(1)
                        val prevEnd = currentDateRange.endInclusive.minusWeeks(1)
                        currentDateRange = de.tobiasschuerg.weekview.data.LocalDateRange(prevStart, prevEnd)
                        weekData = EventCreator.createWeekData(currentDateRange)
                        events = weekData.getSingleEvents()
                        Log.d("WeekView", "Swiped right: $currentDateRange")
                    },
                )
            }
        }
    }
}
