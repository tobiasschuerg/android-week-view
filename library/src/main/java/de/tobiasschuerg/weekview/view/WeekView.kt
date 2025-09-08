package de.tobiasschuerg.weekview.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.RelativeLayout
import de.tobiasschuerg.weekview.R
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.data.WeekData
import de.tobiasschuerg.weekview.data.WeekViewConfig
import de.tobiasschuerg.weekview.util.DayOfWeekUtil
import de.tobiasschuerg.weekview.util.TimeSpan
import de.tobiasschuerg.weekview.util.dipToPixelF
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalTime
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Classic View-based WeekView component for displaying weekly schedules.
 *
 * @deprecated This View-based implementation is deprecated in favor of the modern Compose-based WeekViewCompose.
 * Consider migrating to WeekViewCompose for better performance, modern UI patterns, and future support.
 * This class will be maintained for backwards compatibility but new features will primarily target the Compose version.
 *
 * @see de.tobiasschuerg.weekview.compose.WeekViewCompose
 */
@Deprecated(
    message = "Use WeekViewCompose instead for better performance and modern UI patterns",
    replaceWith =
        ReplaceWith(
            "WeekViewCompose",
            "de.tobiasschuerg.weekview.compose.WeekViewCompose",
        ),
    level = DeprecationLevel.WARNING,
)
class WeekView(
    context: Context,
    attributeSet: AttributeSet,
) : RelativeLayout(context, attributeSet) {
    private val backgroundView: WeekBackgroundView
    private val overlapsWith = ArrayList<EventView>()

    private var layoutCount = 0

    private var clickListener: ((view: EventView) -> Unit)? = null
    private var contextMenuListener: OnCreateContextMenuListener? = null
    private var eventTransitionName: String? = null

    private val accentColor: Int

    private val scaleGestureDetector: ScaleGestureDetector
    private val weekViewConfig: WeekViewConfig

    var eventConfig = EventConfig()

    init {
        val arr = context.obtainStyledAttributes(attributeSet, R.styleable.WeekView)
        accentColor = arr.getColor(R.styleable.WeekView_accent_color, Color.BLUE)
        val start = arr.getInt(R.styleable.WeekView_start_hour, 10)
        val end = arr.getInt(R.styleable.WeekView_end_hour, 14)
        arr.recycle() // Do this when done.

        val prefs = context.getSharedPreferences("ts_week_view", Context.MODE_PRIVATE)
        weekViewConfig = WeekViewConfig(prefs)

        backgroundView = WeekBackgroundView(context)
        backgroundView.defaultTimeSpan = TimeSpan(LocalTime.of(start, 0), LocalTime.of(end, 0))
        backgroundView.setAccentColor(accentColor)
        backgroundView.scalingFactor = weekViewConfig.scalingFactor

        addView(backgroundView)

        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val factor = weekViewConfig.scalingFactor * detector.scaleFactor
            // Don't let the object get too small or too large.
            val scaleFactor = max(0.25f, min(factor, 3.0f))
            weekViewConfig.scalingFactor = scaleFactor
            backgroundView.scalingFactor = scaleFactor
            Log.d(TAG, "Scale factor is $scaleFactor")
            invalidate()
            requestLayout()
            return true
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)

        // Handle click detection for accessibility
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                // If this was a simple tap (not a scale gesture), trigger performClick
                if (!scaleGestureDetector.isInProgress) {
                    performClick()
                }
            }
        }

        return super.onTouchEvent(event)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        super.dispatchTouchEvent(event)
        return scaleGestureDetector.onTouchEvent(event)
    }

    fun setEventTransitionName(transitionName: String) {
        this.eventTransitionName = transitionName
        for (childId in 0 until childCount) {
            val child: View = getChildAt(childId)
            if (child is EventView) {
                child.transitionName = transitionName
            }
        }
    }

    fun setEventClickListener(clickListener: (view: EventView) -> Unit) {
        this.clickListener = clickListener
        for (childIndex in 0 until childCount) {
            val view: View = getChildAt(childIndex)
            if (view is EventView) {
                view.setOnClickListener {
                    clickListener.invoke(view)
                }
            }
        }
    }

    override fun setOnCreateContextMenuListener(contextMenuListener: OnCreateContextMenuListener?) {
        this.contextMenuListener = contextMenuListener
        for (childIndex in 0 until childCount) {
            val view: View = getChildAt(childIndex)
            if (view is EventView) {
                view.setOnCreateContextMenuListener(contextMenuListener)
            }
        }
    }

    fun addEvents(weekData: WeekData) {
        Log.d(TAG, "Adding ${weekData.getSingleEvents().size} weekData to week view")

        weekData.getTimeSpan()?.let {
            backgroundView.updateTimes(it)
        }

        for (event in weekData.getSingleEvents()) {
            addEvent(event)
        }

        // TODO: support multi day weekData
        Log.d(TAG, " - Done adding weekData to timetable")
    }

    fun addEvent(event: Event.Single) {
        // enable weekend if not enabled yet
        when (event.date.dayOfWeek) {
            DayOfWeek.SATURDAY -> {
                Log.i(TAG, "Enabling Saturday")
                if (!backgroundView.days.contains(DayOfWeek.SATURDAY)) {
                    backgroundView.days.add(DayOfWeek.SATURDAY)
                }
            }

            DayOfWeek.SUNDAY -> {
                Log.i(TAG, "Enabling Saturday")
                if (!backgroundView.days.contains(DayOfWeek.SATURDAY)) {
                    backgroundView.days.add(DayOfWeek.SATURDAY)
                }
                Log.i(TAG, "Enabling Sunday")
                if (!backgroundView.days.contains(DayOfWeek.SUNDAY)) {
                    backgroundView.days.add(DayOfWeek.SUNDAY)
                }
            }

            else -> {
                // nothing to do, just add the event
            }
        }

        val lv = EventView(context, event, eventConfig, weekViewConfig.scalingFactor)
        backgroundView.updateTimes(event.timeSpan)

        lv.setOnClickListener { clickListener?.invoke(lv) }
        lv.setOnCreateContextMenuListener(contextMenuListener)
        lv.transitionName = eventTransitionName

        addView(lv)
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
    ) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        Log.v(TAG, "Measuring ($widthSize x $heightSize)")
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int,
    ) {
        Log.v(TAG, "Laying out timetable for the ${++layoutCount} time.")
        Log.v(TAG, "l: $l, t: $t, r: $r, b: $b")
        super.onLayout(true, l, t, r, b)

        val saturdayEnabled = backgroundView.days.contains(DayOfWeek.SATURDAY)
        val sundayEnabled = backgroundView.days.contains(DayOfWeek.SUNDAY)

        for (childIndex in 0 until childCount) {
            Log.i(TAG, "child $childIndex of $childCount")
            val eventView: EventView
            val childView = getChildAt(childIndex)
            if (childView is EventView) {
                eventView = childView
            } else {
                continue
            }

            // FIXME   lessonView.setShortNameEnabled(isShortNameEnabled);
            val column: Int =
                DayOfWeekUtil.mapDayToColumn(
                    eventView.event.date.dayOfWeek,
                    saturdayEnabled,
                    sundayEnabled,
                )
            if (column < 0) {
                // should not be necessary as wrong days get filtered before.
                Log.v(TAG, "Removing view for event $eventView")
                childView.visibility = GONE
                removeView(childView)
                continue
            }
            var left: Int = backgroundView.getColumnStart(column, true)
            val right: Int = backgroundView.getColumnEnd(column, true)

            overlapsWith.clear()
            for (j in 0 until childIndex) {
                val v2 = getChildAt(j)
                // get next LessonView
                if (v2 is EventView) {
                    // check for overlap
                    if (v2.event.date != eventView.event.date) {
                        continue // days differ, overlap not possible
                    } else if (overlaps(eventView, v2)) {
                        overlapsWith.add(v2)
                    }
                }
            }

            if (overlapsWith.size > 0) {
                val width = (right - left) / (overlapsWith.size + 1)
                for ((index, view) in overlapsWith.withIndex()) {
                    val left2 = left + index * width
                    view.layout(left2, view.top, left2 + width, view.bottom)
                }
                left = right - width
            }

            eventView.scalingFactor = weekViewConfig.scalingFactor
            val startTime = backgroundView.defaultTimeSpan.start
            val lessonStart = eventView.event.timeSpan.start
            val offset = Duration.between(startTime, lessonStart)

            val yOffset = offset.toMinutes() * weekViewConfig.scalingFactor
            val top = context.dipToPixelF(yOffset) + backgroundView.topOffsetPx

            val bottom = top + eventView.measuredHeight
            eventView.layout(left, top.roundToInt(), right, bottom.roundToInt())
        }
    }

    fun setShowNowIndicator(enabled: Boolean) {
        backgroundView.showNowIndicator = enabled
    }

    private fun overlaps(
        left: EventView,
        right: EventView,
    ): Boolean {
        val rightStartsAfterLeftStarts = right.event.timeSpan.start >= left.event.timeSpan.start
        val rightStartsBeforeLeftEnds = right.event.timeSpan.start < left.event.timeSpan.endExclusive
        val lessonStartsWithing = rightStartsAfterLeftStarts && rightStartsBeforeLeftEnds

        val leftStartsBeforeRightEnds = left.event.timeSpan.start < right.event.timeSpan.endExclusive
        val rightEndsBeforeOrWithLeftEnds = right.event.timeSpan.endExclusive <= left.event.timeSpan.endExclusive
        val lessonEndsWithing = leftStartsBeforeRightEnds && rightEndsBeforeOrWithLeftEnds

        val leftStartsAfterRightStarts = left.event.timeSpan.start > right.event.timeSpan.start
        val rightEndsAfterLeftEnds = right.event.timeSpan.start > left.event.timeSpan.endExclusive
        val lessonWithin = leftStartsAfterRightStarts && rightEndsAfterLeftEnds

        return lessonStartsWithing || lessonEndsWithing || lessonWithin
    }

    /**
     * Removed all [EventView]s.
     */
    fun removeAllEvents() = removeViews(1, childCount - 1)

    /**
     * Removes ALL [View]s - also the background.
     * This is probably not what you want.
     */
    @Deprecated(
        message = "Better use removeAllEvents in order to keep the background!",
        replaceWith = ReplaceWith("removeAllEvents()"),
    )
    override fun removeAllViews() = super.removeAllViews()

    override fun performClick(): Boolean {
        // Call the superclass method to ensure accessibility events are fired
        return super.performClick()
    }

    companion object {
        private const val TAG = "WeekView"
    }
}
