package de.tobiasschuerg.weekview.util

import org.junit.Test
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class LocalTimeExtTest {

    @Test
    fun `toLocalString uses locale-aware formatting`() {
        val testTime = LocalTime.of(14, 30)
        
        // Save current default locale
        val originalLocale = Locale.getDefault()
        
        try {
            // Test US locale (12-hour format)
            Locale.setDefault(Locale.US)
            val usFormat = testTime.toLocalString()
            // Should contain PM for 14:30 in US format
            assert(usFormat.contains("PM")) { "US format should contain PM for 14:30, but got: $usFormat" }
            
            // Test German locale (24-hour format)  
            Locale.setDefault(Locale.GERMANY)
            val germanFormat = testTime.toLocalString()
            // Should contain 14:30 in German format
            assert(germanFormat.contains("14:30")) { "German format should contain 14:30, but got: $germanFormat" }
            
            // Verify formats are different
            assertNotEquals(usFormat, germanFormat, "US and German formats should be different")
            
        } finally {
            // Restore original locale
            Locale.setDefault(originalLocale)
        }
    }
    
    @Test 
    fun `toLocalString is consistent with DateTimeFormatter ofLocalizedTime SHORT`() {
        val testTime = LocalTime.of(9, 15)
        
        val extensionResult = testTime.toLocalString()
        val formatterResult = testTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
        
        assertEquals(formatterResult, extensionResult, "Extension function should match DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)")
    }
}