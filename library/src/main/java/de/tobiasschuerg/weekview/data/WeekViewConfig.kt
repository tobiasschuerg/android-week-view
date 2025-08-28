package de.tobiasschuerg.weekview.data

import android.content.SharedPreferences

/**
 * Persists the WeekViewConfig.
 * This makes it possible to keep the same zoom level across views and sessions.
 *
 * Created by Tobias Schürg on 01.03.2018.
 */
class WeekViewConfig(private val prefs: SharedPreferences) {
    var scalingFactor: Float = prefs.getFloat(PREFS_KEY_SCALING_FACTOR, 1f)
        set(value) {
            field = value
            prefs.edit().putFloat(PREFS_KEY_SCALING_FACTOR, value).apply()
        }

    companion object {
        private const val PREFS_KEY_SCALING_FACTOR = "awv_scaling_factor"
    }
}
