package de.tobiasschuerg.weekview.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek
import java.time.temporal.WeekFields
import java.util.Locale

class DayOfWeekUtilTest {

    @Test
    fun `test mapping for Germany where monday is the first day of the week`() {
        Locale.setDefault(Locale.GERMANY)

        val col1 = DayOfWeekUtil.mapDayToColumn(DayOfWeek.MONDAY, saturdayEnabled = false, sundayEnabled = false)
        assertEquals(0, col1)

        val col2 = DayOfWeekUtil.mapDayToColumn(DayOfWeek.SUNDAY, saturdayEnabled = true, sundayEnabled = true)
        assertEquals(6, col2)

        val col3 = DayOfWeekUtil.mapDayToColumn(DayOfWeek.SUNDAY, saturdayEnabled = false, sundayEnabled = true)
        assertEquals(5, col3)
    }

    @Test
    fun `test mapping for the US where sunday is the first day of the week`() {
        Locale.setDefault(Locale.US)

        val col1 = DayOfWeekUtil.mapDayToColumn(DayOfWeek.MONDAY, saturdayEnabled = false, sundayEnabled = false)
        assertEquals(0, col1)

        val col2 = DayOfWeekUtil.mapDayToColumn(DayOfWeek.MONDAY, saturdayEnabled = true, sundayEnabled = true)
        assertEquals(1, col2)

        val col3 = DayOfWeekUtil.mapDayToColumn(DayOfWeek.FRIDAY, saturdayEnabled = false, sundayEnabled = false)
        assertEquals(4, col3)

        val col4 = DayOfWeekUtil.mapDayToColumn(DayOfWeek.FRIDAY, saturdayEnabled = true, sundayEnabled = true)
        assertEquals(5, col4)
    }

    @Test
    fun `test mapping for egypt where saturday is the first day of the week`() {
        val egyptLocale = Locale("ar", "EG")

        val firstDayOfWeek = WeekFields.of(egyptLocale).firstDayOfWeek
        assertEquals(DayOfWeek.SATURDAY, firstDayOfWeek)

        Locale.setDefault(egyptLocale)

        val col1 = DayOfWeekUtil.mapDayToColumn(DayOfWeek.MONDAY, saturdayEnabled = false, sundayEnabled = false)
        assertEquals(0, col1)

        val col2 = DayOfWeekUtil.mapDayToColumn(DayOfWeek.MONDAY, saturdayEnabled = true, sundayEnabled = true)
        assertEquals(2, col2)

        val col3 = DayOfWeekUtil.mapDayToColumn(DayOfWeek.FRIDAY, saturdayEnabled = false, sundayEnabled = false)
        assertEquals(4, col3)

        val col4 = DayOfWeekUtil.mapDayToColumn(DayOfWeek.FRIDAY, saturdayEnabled = true, sundayEnabled = true)
        assertEquals(6, col4)
    }
}
