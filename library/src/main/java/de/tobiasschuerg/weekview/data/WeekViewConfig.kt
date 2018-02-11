package de.tobiasschuerg.weekview.data

data class WeekViewConfig(

        val stretchingFactor: Float = 1f,

        val useShortNames: Boolean = true,
        val saturdayEnabled: Boolean = true,
        val sundayEnabled: Boolean = true,

        val showTimeStart: Boolean = true,
        val showType: Boolean = true,
        val showTeacher: Boolean = true,
        val showLocation: Boolean = true,
        val showTimeEnd: Boolean = true

)