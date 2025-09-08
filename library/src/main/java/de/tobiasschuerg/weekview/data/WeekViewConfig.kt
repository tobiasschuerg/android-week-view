package de.tobiasschuerg.weekview.data

import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Persists the WeekViewConfig.
 * This makes it possible to keep the same zoom level across views and sessions.
 *
 * Created by Tobias Schürg on 01.03.2018.
 */
class WeekViewConfig(
    private val prefs: SharedPreferences,
) {
    var scalingFactor: Float = prefs.getFloat(PREFS_KEY_SCALING_FACTOR, 1f)
        set(value) {
            field = value
            prefs.edit { putFloat(PREFS_KEY_SCALING_FACTOR, value) }
        }

    var showCurrentTimeIndicator: Boolean = prefs.getBoolean(PREFS_KEY_SHOW_CURRENT_TIME_INDICATOR, true)
        set(value) {
            field = value
            prefs.edit { putBoolean(PREFS_KEY_SHOW_CURRENT_TIME_INDICATOR, value) }
        }

    var highlightCurrentDay: Boolean = prefs.getBoolean(PREFS_KEY_HIGHLIGHT_CURRENT_DAY, true)
        set(value) {
            field = value
            prefs.edit { putBoolean(PREFS_KEY_HIGHLIGHT_CURRENT_DAY, value) }
        }

    /**
     * If true, only the current day's column will be highlighted.
     * If false, the entire row will be highlighted.
     */
    var currentTimeLineOnlyToday: Boolean = prefs.getBoolean(PREFS_KEY_CURRENT_TIME_LINE_ONLY_TODAY, false)
        set(value) {
            field = value
            prefs.edit { putBoolean(PREFS_KEY_CURRENT_TIME_LINE_ONLY_TODAY, value) }
        }

    companion object {
        private const val PREFS_KEY_SCALING_FACTOR = "awv_scaling_factor"
        private const val PREFS_KEY_SHOW_CURRENT_TIME_INDICATOR = "awv_show_current_time_indicator"
        private const val PREFS_KEY_HIGHLIGHT_CURRENT_DAY = "awv_highlight_current_day"
        private const val PREFS_KEY_CURRENT_TIME_LINE_ONLY_TODAY = "awv_current_time_line_only_today"
    }
}
