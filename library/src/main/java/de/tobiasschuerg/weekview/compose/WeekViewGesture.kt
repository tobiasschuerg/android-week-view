package de.tobiasschuerg.weekview.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal fun Modifier.weekViewGestures(
    onSwipeRight: () -> Unit,
    onSwipeLeft: () -> Unit,
    getOffsetX: () -> Float,
    setOffsetX: (Float) -> Unit,
    getAnimatingOffsetX: () -> Float,
    setAnimatingOffsetX: (Float) -> Unit,
): Modifier =
    composed {
        val coroutineScope = rememberCoroutineScope()
        var containerWidth by remember { mutableIntStateOf(0) }
        val animationSpec = tween<Float>(durationMillis = 300)

        pointerInput(Unit) {
            containerWidth = size.width
            detectHorizontalDragGestures(
                onHorizontalDrag = { change, dragAmount ->
                    change.consume()
                    setOffsetX(getOffsetX() + dragAmount)
                },
                onDragEnd = {
                    handleDragEnd(
                        offsetX = getOffsetX(),
                        containerWidth = containerWidth,
                        coroutineScope = coroutineScope,
                        animationSpec = animationSpec,
                        onSwipeRight = onSwipeRight,
                        onSwipeLeft = onSwipeLeft,
                        setAnimatingOffsetX = setAnimatingOffsetX,
                        setOffsetX = setOffsetX,
                    )
                },
                onDragCancel = {
                    setOffsetX(0f)
                    setAnimatingOffsetX(0f)
                },
            )
        }
    }

private fun handleDragEnd(
    offsetX: Float,
    containerWidth: Int,
    coroutineScope: CoroutineScope,
    animationSpec: androidx.compose.animation.core.TweenSpec<Float>,
    onSwipeRight: () -> Unit,
    onSwipeLeft: () -> Unit,
    setAnimatingOffsetX: (Float) -> Unit,
    setOffsetX: (Float) -> Unit,
) {
    if (containerWidth <= 0) {
        setOffsetX(0f)
        setAnimatingOffsetX(0f)
        return
    }
    val threshold = containerWidth / 4
    when {
        offsetX > threshold -> {
            coroutineScope.launch {
                val anim = Animatable(offsetX)
                anim.animateTo(
                    targetValue = containerWidth.toFloat(),
                    animationSpec = animationSpec,
                ) {
                    setAnimatingOffsetX(value)
                }
                onSwipeRight()
                setOffsetX(0f)
                setAnimatingOffsetX(0f)
            }
        }

        offsetX < -threshold -> {
            coroutineScope.launch {
                val anim = Animatable(offsetX)
                anim.animateTo(
                    targetValue = -containerWidth.toFloat(),
                    animationSpec = animationSpec,
                ) {
                    setAnimatingOffsetX(value)
                }
                onSwipeLeft()
                setOffsetX(0f)
                setAnimatingOffsetX(0f)
            }
        }

        else -> {
            coroutineScope.launch {
                val anim = Animatable(offsetX)
                anim.animateTo(
                    targetValue = 0f,
                    animationSpec = animationSpec,
                ) {
                    setOffsetX(value)
                }
            }
        }
    }
}
