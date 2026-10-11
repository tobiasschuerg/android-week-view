package de.tobiasschuerg.weekview.sample

import de.tobiasschuerg.weekview.WeekLayout
import de.tobiasschuerg.weekview.sample.data.SamplePeriods

/** The week view layouts the sample app can switch between. */
enum class SampleLayout(
    val label: String,
    val layout: WeekLayout,
) {
    TIMELINE("Timeline", WeekLayout.Timeline),
    PERIODS("School periods", WeekLayout.Periods(SamplePeriods.school)),
}
