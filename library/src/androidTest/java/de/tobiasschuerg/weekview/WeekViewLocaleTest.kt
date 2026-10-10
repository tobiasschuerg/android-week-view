package de.tobiasschuerg.weekview

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tobiasschuerg.weekview.model.LocalDateRange
import de.tobiasschuerg.weekview.model.WeekData
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale

/** Without an explicit [WeekViewConfig.locale], day names follow the locale of the composition. */
@RunWith(AndroidJUnit4::class)
class WeekViewLocaleTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val tuesday = LocalDate.of(2025, 9, 2)

    private fun setWeekView(
        compositionLocale: Locale,
        config: WeekViewConfig,
    ) {
        val weekData = WeekData(LocalDateRange(tuesday, tuesday), LocalTime.of(8, 0), LocalTime.of(18, 0))
        composeTestRule.setContent {
            val configuration = Configuration(LocalConfiguration.current).apply { setLocale(compositionLocale) }
            CompositionLocalProvider(LocalConfiguration provides configuration) {
                MaterialTheme {
                    WeekView(weekData = weekData, weekViewConfig = config, modifier = Modifier.size(300.dp, 600.dp))
                }
            }
        }
    }

    @Test
    fun shouldUseTheCompositionLocaleWhenNoLocaleIsConfigured() {
        setWeekView(compositionLocale = Locale.FRANCE, config = WeekViewConfig())

        composeTestRule.onNodeWithText(tuesday.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.FRANCE)).assertIsDisplayed()
    }

    @Test
    fun shouldUseTheConfiguredLocaleWhenOneIsSet() {
        setWeekView(compositionLocale = Locale.FRANCE, config = WeekViewConfig(locale = Locale.GERMANY))

        composeTestRule.onNodeWithText(tuesday.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.GERMANY)).assertIsDisplayed()
    }
}
