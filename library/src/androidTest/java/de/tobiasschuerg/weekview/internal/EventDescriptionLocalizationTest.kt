package de.tobiasschuerg.weekview.internal

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tobiasschuerg.weekview.WeekViewConfig
import de.tobiasschuerg.weekview.model.Event
import de.tobiasschuerg.weekview.model.LocalDateRange
import de.tobiasschuerg.weekview.model.TimeSpan
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

/** Screen reader descriptions of all-day and multi-day events are translated and use localized dates. */
@RunWith(AndroidJUnit4::class)
class EventDescriptionLocalizationTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val monday = LocalDate.of(2025, 9, 1)

    @Test
    fun shouldDescribeAllDayAndMultiDayEventsInGermanWhenTheAppRunsInGerman() {
        val holiday = Event.AllDay(id = "a", date = monday, title = "Feiertag", shortTitle = "F", textColor = 0, backgroundColor = 0)
        val conference =
            Event.MultiDay(
                id = "m",
                date = monday,
                lastDate = monday.plusDays(2),
                title = "Konferenz",
                shortTitle = "K",
                textColor = 0,
                backgroundColor = 0,
            )

        composeTestRule.setContent {
            val configuration = Configuration(LocalConfiguration.current).apply { setLocale(Locale.GERMANY) }
            val context = LocalContext.current.createConfigurationContext(configuration)
            CompositionLocalProvider(
                LocalConfiguration provides configuration,
                LocalContext provides context,
                LocalResources provides context.resources,
            ) {
                MaterialTheme {
                    WeekBackgroundCompose(
                        dateRange = LocalDateRange(monday, monday.plusDays(4)),
                        timeRange = TimeSpan.of(LocalTime.of(8, 0), Duration.ofHours(8)),
                        allDayEvents = listOf(holiday),
                        multiDayEvents = listOf(conference),
                        weekViewConfig = WeekViewConfig(),
                    )
                }
            }
        }

        val dates = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.GERMANY)
        composeTestRule.onNodeWithTag("AllDayEventView_a").assert(hasDescription("Feiertag, ganztägig"))
        composeTestRule
            .onNodeWithTag("MultiDayEventView_m")
            .assert(hasDescription("Konferenz, ${dates.format(monday)} bis ${dates.format(monday.plusDays(2))}"))
    }

    private fun hasDescription(expected: String) = SemanticsMatcher.expectValue(SemanticsProperties.ContentDescription, listOf(expected))
}
