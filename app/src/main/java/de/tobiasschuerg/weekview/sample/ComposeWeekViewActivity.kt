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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import de.tobiasschuerg.weekview.compose.WeekViewActions
import de.tobiasschuerg.weekview.compose.WeekViewCompose
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.data.LocalDateRange
import de.tobiasschuerg.weekview.data.WeekViewConfig
import java.time.DayOfWeek
import java.time.LocalDate

class ComposeWeekViewActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle =
                SystemBarStyle.dark(
                    scrim = Color.Transparent.toArgb(),
                ),
        )

        val weekViewConfig =
            WeekViewConfig(
                scalingFactor = 1f,
                showCurrentTimeIndicator = true,
                highlightCurrentDay = true,
                currentTimeLineOnlyToday = false,
            )

        val eventConfig =
            EventConfig(
                alwaysUseFullName = true,
                showTimeStart = true,
                showUpperText = true,
                showSubtitle = true,
                showLowerText = true,
                showTimeEnd = true,
            )

        setContent {
            val today = LocalDate.now()
            val monday = today.with(DayOfWeek.MONDAY)
            val friday = today.with(DayOfWeek.FRIDAY)
            val dateRange = remember { LocalDateRange(monday, friday) }

            var selectedTimetable by remember { mutableStateOf(SampleTimetables.Timetable.UNIVERSITY) }
            var weekData by remember { mutableStateOf(SampleTimetables.create(selectedTimetable, dateRange)) }
            var menuExpanded by remember { mutableStateOf(false) }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(selectedTimetable.label) },
                        colors =
                            TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                        actions = {
                            TextButton(onClick = { menuExpanded = true }) {
                                Text("Switch", color = MaterialTheme.colorScheme.onPrimary)
                            }
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false },
                            ) {
                                SampleTimetables.Timetable.entries.forEach { timetable ->
                                    DropdownMenuItem(
                                        text = { Text(timetable.label) },
                                        onClick = {
                                            selectedTimetable = timetable
                                            weekData = SampleTimetables.create(timetable, dateRange)
                                            menuExpanded = false
                                        },
                                    )
                                }
                            }
                        },
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
                    actions =
                        WeekViewActions(
                            onEventClick = { event ->
                                Toast.makeText(this@ComposeWeekViewActivity, "Clicked: ${event.title}", Toast.LENGTH_SHORT).show()
                            },
                            onEventLongPress = { event ->
                                Toast.makeText(this@ComposeWeekViewActivity, "Long press: ${event.title}", Toast.LENGTH_SHORT).show()
                            },
                            onScalingFactorChange = {
                                Log.d("WeekView", "Scaling factor: $it")
                            },
                        ),
                )
            }
        }
    }
}
