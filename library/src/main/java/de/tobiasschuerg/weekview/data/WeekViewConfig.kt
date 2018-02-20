package de.tobiasschuerg.weekview.data

import android.graphics.Color
import android.support.annotation.ColorInt

data class WeekViewConfig(

        val stretchingFactor: Float = 1f,

        @ColorInt
        val accentColor: Int = Color.BLUE,

        val useShortNames: Boolean = true,
        val saturdayEnabled: Boolean = true,
        val sundayEnabled: Boolean = true,

        val showTimeStart: Boolean = true,
        val showType: Boolean = true,
        val showTeacher: Boolean = true,
        val showLocation: Boolean = true,
        val showTimeEnd: Boolean = true

)