package de.tobiasschuerg.weekview.util

import org.junit.Assert.assertEquals
import org.junit.Test
import org.threeten.bp.DayOfWeek
import java.util.*

class DayOfWeekUtilTest {

    /**
     * In Germany monday is the first day of the week.
     */
    @Test
    fun germany() {
        Locale.setDefault(Locale.GERMANY)

        val col1 = DayOfWeekUtil.mapDayToColumn(DayOfWeek.MONDAY, false, false)
        assertEquals(0, col1)

        val col2 = DayOfWeekUtil.mapDayToColumn(DayOfWeek.SUNDAY, true, true)
        assertEquals(6, col2)

        val col3 = DayOfWeekUtil.mapDayToColumn(DayOfWeek.SUNDAY, false, true)
        assertEquals(5, col3)
    }

    /**
     * In the us sunday is the first day of the week.
     */
    @Test
    fun us() {
        Locale.setDefault(Locale.US)

        val col1 = DayOfWeekUtil.mapDayToColumn(DayOfWeek.MONDAY, false, false)
        assertEquals(0, col1)

        val col2 = DayOfWeekUtil.mapDayToColumn(DayOfWeek.MONDAY, true, true)
        assertEquals(1, col2)

        val col3 = DayOfWeekUtil.mapDayToColumn(DayOfWeek.FRIDAY, false, false)
        assertEquals(4, col3)

        val col4 = DayOfWeekUtil.mapDayToColumn(DayOfWeek.FRIDAY, true, true)
        assertEquals(5, col4)
    }

    /**
     * In Qatar saturday is the first day of the week.
     */
    @Test
    fun quatar() {
        Locale.setDefault(Locale.forLanguageTag("ar"))

        val col1 = DayOfWeekUtil.mapDayToColumn(DayOfWeek.MONDAY, false, false)
        assertEquals(0, col1)

        val col2 = DayOfWeekUtil.mapDayToColumn(DayOfWeek.MONDAY, true, true)
        assertEquals(2, col2)

        val col3 = DayOfWeekUtil.mapDayToColumn(DayOfWeek.FRIDAY, false, false)
        assertEquals(4, col3)

        val col4 = DayOfWeekUtil.mapDayToColumn(DayOfWeek.FRIDAY, true, true)
        assertEquals(6, col4)
    }
}
