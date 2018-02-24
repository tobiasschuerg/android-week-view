package de.tobiasschuerg.weekview.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.PaintDrawable
import android.os.Debug
import android.util.Log
import android.view.ContextMenu.ContextMenuInfo
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import de.tobiasschuerg.weekview.BuildConfig
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.util.*

/** this view is only constructed during runtime. */
@SuppressLint("ViewConstructor")
class EventView(
        context: Context,
        val event: Event.Single,
        var scalingFactor: Float = 1f,
        val useShortNames: Boolean = true,
        val showTimeStart: Boolean = true,
        val showType: Boolean = true,
        val showTeacher: Boolean = true,
        val showLocation: Boolean = true,
        val showTimeEnd: Boolean = true

) : View(context) {

    private val TAG = javaClass.simpleName
    private val CORNER_RADIUS_PX = context.dipToPixelF(2f)

    private val textPaint: Paint by lazy { Paint().apply { isAntiAlias = true } }

    private val subjectName: String by lazy { if (useShortNames) event.shortName else event.fullName }

    private val textBounds: Rect = Rect()

    private val weightSum: Int
    private val lessonWeight = 3
    private val fromweight: Int
    private val toWeight: Int
    private val teacherweight: Int
    private val locationWeight: Int
    private val typeWeight: Int


    init {
        val padding = this.context.dipToPixelI(2f)
        setPadding(padding, padding, padding, padding)

        background = PaintDrawable().apply {
            paint.color = event.backgroundColor
            setCornerRadius(CORNER_RADIUS_PX)
        }

        /** Calculate weights above & below. */
        if (showTimeStart) fromweight = 1 else fromweight = 0
        if (showType) typeWeight = 1 else typeWeight = 0
        if (showTeacher) teacherweight = 1 else teacherweight = 0
        if (showLocation) locationWeight = 1 else locationWeight = 0
        if (showTimeEnd) toWeight = 1 else toWeight = 0

        weightSum = fromweight + typeWeight + teacherweight + locationWeight + toWeight + lessonWeight

        textPaint.color = event.textColor
    }

    // TODO: clean up
    override fun onDraw(canvas: Canvas) {
        Log.d(TAG, "Drawing ${event.fullName}")

        // only for debugging
        if (Debug.isDebuggerConnected()) {
            for (i in 0..weightSum) {
                val content = height - (paddingTop + paddingBottom)
                val y = content * i / weightSum + paddingTop
                canvas.drawLine(0f, y.toFloat(), canvas.width.toFloat(), y.toFloat(), textPaint)
            }
        }

        // subject
        val maxTextSize = TextHelper.fitText(subjectName, textPaint.textSize * 3, width - (paddingLeft + paddingRight), height / 4)
        textPaint.textSize = maxTextSize
        textPaint.getTextBounds(subjectName, 0, subjectName.length, textBounds)
        var weight = fromweight + typeWeight
        if (weight == 0) {
            weight++
        }
        val subjectY = getY(weight, lessonWeight, textBounds)
        canvas.drawText(subjectName, (width / 2 - textBounds.centerX()).toFloat(), subjectY.toFloat(), textPaint)

        textPaint.textSize = TextHelper.fitText("123456", maxTextSize, width / 2,
                getY(position = 1, bounds = textBounds) - getY(position = 0, bounds = textBounds))

        // start time
        if (showTimeStart) {
            val startText = event.startTime.toLocalString(context)
            textPaint.getTextBounds(startText, 0, startText.length, textBounds)
            canvas.drawText(startText, (textBounds.left + paddingLeft).toFloat(), (textBounds.height() + paddingTop).toFloat(), textPaint)
        }

        // end time
        if (showTimeEnd) {
            val endText = event.endTime.toLocalString(context)
            textPaint.getTextBounds(endText, 0, endText.length, textBounds)
            canvas.drawText(endText, (width - (textBounds.right + paddingRight)).toFloat(), (height - paddingBottom).toFloat(), textPaint)
        }

        // type
        if (showType && event.type != null) {
            textPaint.getTextBounds(event.type, 0, event.type.length, textBounds)
            val typeY = getY(position = fromweight, bounds = textBounds)
            canvas.drawText(event.type, (width / 2 - textBounds.centerX()).toFloat(), typeY.toFloat(), textPaint)
        }

        // teacher
        if (showTeacher && event.teacher != null) {
            textPaint.getTextBounds(event.teacher, 0, event.teacher.length, textBounds)
            val teacherY = getY(position = fromweight + typeWeight + lessonWeight, bounds = textBounds)
            canvas.drawText(event.teacher, (width / 2 - textBounds.centerX()).toFloat(), teacherY.toFloat(), textPaint)
        }

        // location
        if (showLocation && event.location != null) {
            textPaint.getTextBounds(event.location, 0, event.location.length, textBounds)
            val locationY = getY(position = fromweight + typeWeight + lessonWeight + teacherweight, bounds = textBounds)
            canvas.drawText(event.location, (width / 2 - textBounds.centerX()).toFloat(), locationY.toFloat(), textPaint)
        }

    }

    private fun getY(position: Int, weight: Int = 1, bounds: Rect): Int {
        val content = height - (paddingTop + paddingBottom)
        val y = (content * (position + 0.5f * weight) / weightSum) + paddingTop
        return Math.round(y) - bounds.centerY()
    }

    var measureCount = 0
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.v(TAG, "Measuring ${event.fullName} ${measureCount++}")
        if (BuildConfig.DEBUG) {
            val debugWidth = ViewHelper.debugMeasureSpec(widthMeasureSpec)
            val debugHeight = ViewHelper.debugMeasureSpec(heightMeasureSpec)
            Log.v(TAG, "-> width: $debugWidth\n-> height: $debugHeight")
        }

        val desiredHeightDp = event.duration.toMinutes() * scalingFactor
        val desiredHeightPx = context.dipToPixelI(desiredHeightDp)
        val resolvedHeight = resolveSize(desiredHeightPx, heightMeasureSpec)

        setMeasuredDimension(width, resolvedHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        Log.d(TAG, "Laying out ${event.fullName}: changed[$changed] ($left, $top),($right, $bottom)")
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val anim = ScaleAnimation(
                0f, 1f, // Start and end values for the X axis scaling
                0f, 1f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f) // Pivot point of Y scaling
        anim.fillAfter = true // Needed to keep the result of the animation
        anim.duration = 1000
        this.startAnimation(anim)
    }

    override fun getContextMenuInfo(): ContextMenuInfo {
        return LessonViewContextInfo(event)
    }

    data class LessonViewContextInfo(var event: Event.Single) : ContextMenuInfo
}
