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
        val showUpperText: Boolean = true,
        val showSubtitle: Boolean = true,
        val showLowerText: Boolean = true,
        val showTimeEnd: Boolean = true

) : View(context) {

    private val TAG = javaClass.simpleName
    private val CORNER_RADIUS_PX = context.dipToPixelF(2f)

    private val textPaint: Paint by lazy { Paint().apply { isAntiAlias = true } }

    private val subjectName: String by lazy { if (useShortNames) event.shortTitle else event.title }

    private val textBounds: Rect = Rect()

    private val weightSum: Int
    private val weightStartTime: Int
    private val weightUpperText: Int
    private val weightTitle = 3
    private val weightSubTitle: Int
    private val weightLowerText: Int
    private val weightEndTime: Int


    init {
        val padding = this.context.dipToPixelI(2f)
        setPadding(padding, padding, padding, padding)

        background = PaintDrawable().apply {
            paint.color = event.backgroundColor
            setCornerRadius(CORNER_RADIUS_PX)
        }

        /** Calculate weights above & below. */
        if (showTimeStart) weightStartTime = 1 else weightStartTime = 0
        if (showUpperText) weightUpperText = 1 else weightUpperText = 0
        if (showSubtitle) weightSubTitle = 1 else weightSubTitle = 0
        if (showLowerText) weightLowerText = 1 else weightLowerText = 0
        if (showTimeEnd) weightEndTime = 1 else weightEndTime = 0

        weightSum = weightStartTime + weightUpperText + weightSubTitle + weightLowerText + weightEndTime + weightTitle

        textPaint.color = event.textColor
    }

    // TODO: clean up
    override fun onDraw(canvas: Canvas) {
        Log.d(TAG, "Drawing ${event.title}")

        // only for debugging
        if (Debug.isDebuggerConnected()) {
            for (i in 0..weightSum) {
                val content = height - (paddingTop + paddingBottom)
                val y = content * i / weightSum + paddingTop
                canvas.drawLine(0f, y.toFloat(), canvas.width.toFloat(), y.toFloat(), textPaint)
            }
        }

        // title
        val maxTextSize = TextHelper.fitText(subjectName, textPaint.textSize * 3, width - (paddingLeft + paddingRight), height / 4)
        textPaint.textSize = maxTextSize
        textPaint.getTextBounds(subjectName, 0, subjectName.length, textBounds)
        var weight = weightStartTime + weightUpperText
        if (weight == 0) {
            weight++
        }
        val subjectY = getY(weight, weightTitle, textBounds)
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

        // upper text
        if (showUpperText && event.upperText != null) {
            textPaint.getTextBounds(event.upperText, 0, event.upperText.length, textBounds)
            val typeY = getY(position = weightStartTime, bounds = textBounds)
            canvas.drawText(event.upperText, (width / 2 - textBounds.centerX()).toFloat(), typeY.toFloat(), textPaint)
        }

        // subtitle
        if (showSubtitle && event.subTitle != null) {
            textPaint.getTextBounds(event.subTitle, 0, event.subTitle.length, textBounds)
            val teacherY = getY(position = weightStartTime + weightUpperText + weightTitle, bounds = textBounds)
            canvas.drawText(event.subTitle, (width / 2 - textBounds.centerX()).toFloat(), teacherY.toFloat(), textPaint)
        }

        // lower text
        if (showLowerText && event.lowerText != null) {
            textPaint.getTextBounds(event.lowerText, 0, event.lowerText.length, textBounds)
            val locationY = getY(position = weightStartTime + weightUpperText + weightTitle + weightSubTitle, bounds = textBounds)
            canvas.drawText(event.lowerText, (width / 2 - textBounds.centerX()).toFloat(), locationY.toFloat(), textPaint)
        }

    }

    private fun getY(position: Int, weight: Int = 1, bounds: Rect): Int {
        val content = height - (paddingTop + paddingBottom)
        val y = (content * (position + 0.5f * weight) / weightSum) + paddingTop
        return Math.round(y) - bounds.centerY()
    }

    private var measureCount = 0
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.v(TAG, "Measuring ${event.title} ${measureCount++}")
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
        Log.d(TAG, "Laying out ${event.title}: changed[$changed] ($left, $top),($right, $bottom)")
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
