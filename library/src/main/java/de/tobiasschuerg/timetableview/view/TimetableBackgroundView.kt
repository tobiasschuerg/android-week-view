package de.tobiasschuerg.timetableview.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.View
import de.tobiasschuerg.timetableview.data.TimeTableConfig
import de.tobiasschuerg.timetableview.util.dipToPixeel
import de.tobiasschuerg.timetableview.util.toLocalString
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.temporal.ChronoUnit
import org.threeten.bp.temporal.WeekFields
import java.text.DateFormatSymbols
import java.util.*
import kotlin.math.roundToInt

class TimetableBackgroundView(
        context: Context,
        private val config: TimeTableConfig,
        earliest: LocalTime,
        private val endTime: LocalTime,
        private val days: List<Int> = emptyList()
) : View(context) {

    val TAG: String = javaClass.simpleName

    private val paintDivider: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            strokeWidth = DIVIDER_WIDTH_PX
            color = DIVIDER_COLOR
        }
    }
    private val mPaintLabels: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.GRAY
            textSize = context.dipToPixeel(12f).toFloat()
            textAlign = Paint.Align.CENTER
        }
    }

    val startTime: LocalTime = earliest.truncatedTo(ChronoUnit.HOURS)
    private val durationMinutes: Long by lazy(Duration.between(startTime, endTime)::toMinutes)

    private var isInScreenshotMode = false

    var topOffset: Float = 0f
        private set
    private var drawCount = 0
    private var yBottom: Float = 0f


    /** Default constructor just for android system. Not used. */
    private constructor(context: Context) : this(context, TimeTableConfig(), LocalTime.of(9, 0), LocalTime.of(14, 0)) {}

    init {
        Log.v(TAG, "Initial start $earliest, end $endTime")
        Log.v(TAG, "Adjusted start $startTime, end $endTime")

        if (!earliest.isBefore(endTime)) {
            throw IllegalStateException("Earliest must not be after latest! $earliest + $endTime")
        }

        setPadding(5, 5, 5, 5)
        topOffset = context.dipToPixeel(30f)
    }

    override fun onDraw(canvas: Canvas) {
        Log.d(TAG, "Drawing timetable background for the ${++drawCount} time.")
        super.onDraw(canvas)
        canvas.drawColor(Color.WHITE)

        drawHorizontalDividers(canvas)
        drawVerticalDividers(canvas)

        Log.d(TAG, "Screenshot mode? $isInScreenshotMode")
        if (!isInScreenshotMode) {
            drawNowIndicator(canvas)
        }
        Log.d(TAG, "Drawing timetable background completed.")
    }

    private fun drawNowIndicator(canvas: Canvas) {
        if (startTime.isBefore(LocalTime.now()) && endTime.isAfter(LocalTime.now())) {
            Log.v(TAG, "Drawing now indicator")
            paintDivider.color = Color.BLUE
            val nowOffset = Duration.between(startTime, LocalTime.now())

            val minutes = nowOffset.toMinutes()
            val y = topOffset + context.dipToPixeel(minutes * config.stretchingFactor)
            canvas.drawLine(0f, y.toFloat(), canvas.width.toFloat(), y.toFloat(), paintDivider)
            paintDivider.color = DIVIDER_COLOR
        }
    }

    private fun drawVerticalDividers(canvas: Canvas) {
        Log.v(TAG, "Drawing vertical dividers on canvas")
        val date = LocalDate.now()
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        date.with(firstDayOfWeek)
        var column = 0
        for (dayId in days) {
            val shortName = DateFormatSymbols().shortWeekdays[dayId]
            val xValue = getLeftOffset(column, false)
            canvas.drawLine(xValue.toFloat(), 0f, xValue.toFloat(), yBottom.toFloat(), paintDivider)
            val xLabel = (getLeftOffset(column, false) + getRightOffset(column, false)) / 2
            canvas.drawText(shortName, xLabel.toFloat(), context.dipToPixeel(20f).toFloat(), mPaintLabels)

            //            // if today is a holiday, we mark this column
            // FIXME: get date from day. Maybe move into timetable. This has nothing to do with bg.
            //            date = date.plusDays(1);
            //            if (holidays != null) {
            //                for (Holiday h : holidays) {
            //                    if (h.isHoliday(date)) {
            //                        int y = context.dipToPixeel(80);
            //                        for (String s : h.getNameMaybe().toUpperCase(Locale.getDefault()).split("")) {
            //                            canvas.drawText(s, xLabel, y, mPaintLabels);
            //                            y += context.dipToPixeel(20);
            //                        }
            //
            //                        break;
            //                    }
            //                }
            //            }
            column++
        }
    }

    private fun drawHorizontalDividers(canvas: Canvas) {
        Log.d(TAG, "Drawing horizontal dividers")
        var localTime = startTime
        var last = LocalTime.MIN
        while (localTime.isBefore(endTime) && !last.isAfter(localTime)) {
            val offset = Duration.between(startTime, localTime)
            Log.v(TAG, "Offset $offset")
            val y = topOffset + context.dipToPixeel(offset.toMinutes() * config.stretchingFactor)
            canvas.drawLine(0f, y.toFloat(), canvas.width.toFloat(), y.toFloat(), paintDivider)

            // final String timeString = localTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
            val timeString = localTime.toLocalString(context)
            drawMultiLineText(canvas, timeString, context.dipToPixeel(25f), y + context.dipToPixeel(20f), mPaintLabels)

            last = localTime
            localTime = localTime.plusHours(1)
        }
        val offset = Duration.between(startTime, localTime)
        Log.v(TAG, "Offset + $offset")
        yBottom = topOffset + context.dipToPixeel(offset.toMinutes() * config.stretchingFactor)
        canvas.drawLine(0f, yBottom.toFloat(), canvas.width.toFloat(), yBottom.toFloat(), paintDivider)
    }

    private fun drawMultiLineText(canvas: Canvas, text: String, initialX: Float, initialY: Float, paint: Paint) {
        var currentY = initialY
        text.split(" ")
                .dropLastWhile(String::isEmpty)
                .forEach {
                    canvas.drawText(it, initialX, currentY, paint)
                    currentY += (-paint.ascent() + paint.descent()).toInt()
                }
    }

    /**
     * Returns the offset (px!) from left for a given column.
     * First column is the first weekday.
     *
     * @param column starting to count at 0
     * @return offset in px
     */
    internal fun getLeftOffset(column: Int, considerDivider: Boolean): Int {
        val contentWidth: Float = width - context.dipToPixeel(50f)
        var offset: Float = context.dipToPixeel(50f) + contentWidth * column / days.size
        if (considerDivider) {
            offset += (DIVIDER_WIDTH_PX / 2f)
        }
        return offset.roundToInt()
    }

    internal fun getRightOffset(column: Int, considerDivider: Boolean): Int {
        val contentWidth: Float = width - context.dipToPixeel(50f)
        var offset: Float = context.dipToPixeel(50f) + contentWidth * (column + 1) / days.size
        if (considerDivider) {
            offset -= (DIVIDER_WIDTH_PX / 2f)
        }
        return offset.roundToInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightDp = durationMinutes * config.stretchingFactor + 100
        val newHeight = topOffset + context.dipToPixeel(heightDp)
        val newHMS = View.MeasureSpec.makeMeasureSpec(newHeight.roundToInt(), View.MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, newHMS)
    }

    //    public void setHolidays(ArrayList<Holiday> holidays) {
    //        ArrayList<Holiday> holidays1 = holidays;
    //    }

    fun setScreenshotMode(screenshotMode: Boolean) {
        isInScreenshotMode = screenshotMode
    }

    companion object {
        private val DIVIDER_WIDTH_PX = 2f
        private val DIVIDER_COLOR = Color.LTGRAY
    }
}
