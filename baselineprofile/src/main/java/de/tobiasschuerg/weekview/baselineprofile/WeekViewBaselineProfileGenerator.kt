package de.tobiasschuerg.weekview.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Records the code paths of typical week view use in the sample app: first layout, scrolling,
 * pinch-zooming, switching timetables and event styles. Run on an API 33+ emulator or device:
 * `./gradlew :library:generateBaselineProfile`.
 */
@RunWith(AndroidJUnit4::class)
class WeekViewBaselineProfileGenerator {
    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() =
        rule.collect(packageName = PACKAGE_NAME, includeInStartupProfile = true) {
            pressHome()
            startActivityAndWait()
            device.waitForGrid()

            device.scrollAndZoomGrid()
            listOf("Work", "Conference", "School", "Special Cases").forEach { timetable ->
                device.pickFromMenu(menu = "Data", item = timetable)
                device.scrollAndZoomGrid()
            }
            listOf("Tinted", "Outlined", "Custom").forEach { style ->
                device.pickFromMenu(menu = "Style", item = style)
                device.scrollAndZoomGrid()
            }
        }

    private fun UiDevice.waitForGrid() {
        wait(Until.hasObject(By.scrollable(true)), TIMEOUT_MS)
    }

    private fun UiDevice.scrollAndZoomGrid() {
        val grid = findObject(By.scrollable(true)) ?: return
        // Keep the gestures off the edges, where they would trigger system navigation.
        grid.setGestureMarginPercentage(GESTURE_MARGIN)
        grid.fling(Direction.DOWN)
        grid.fling(Direction.UP)
        grid.pinchOpen(PINCH_PERCENT)
        grid.pinchClose(PINCH_PERCENT)
        waitForIdle()
    }

    private fun UiDevice.pickFromMenu(
        menu: String,
        item: String,
    ) {
        findObject(By.text(menu))?.click() ?: return
        wait(Until.findObject(By.text(item)), TIMEOUT_MS)?.click()
        waitForIdle()
    }

    private companion object {
        const val PACKAGE_NAME = "de.tobiasschuerg.weekview.sample"
        const val TIMEOUT_MS = 5_000L
        const val GESTURE_MARGIN = 0.2f
        const val PINCH_PERCENT = 0.5f
    }
}
