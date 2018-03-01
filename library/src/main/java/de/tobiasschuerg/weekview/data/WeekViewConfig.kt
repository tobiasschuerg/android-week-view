package de.tobiasschuerg.weekview.data

import android.content.SharedPreferences

/**
 * Persists the WeekViewConfig.
 *
 * Created by Tobias Schürg on 01.03.2018.
 */
class WeekViewConfig(val prefs: SharedPreferences) {

    private val SCALING_FACTOR = "scaling_facor"

    var scalingFactor: Float = prefs.getFloat(SCALING_FACTOR, 1f)
        set(value) {
            field = value
            prefs.edit().putFloat(SCALING_FACTOR, value).apply()
        }
}