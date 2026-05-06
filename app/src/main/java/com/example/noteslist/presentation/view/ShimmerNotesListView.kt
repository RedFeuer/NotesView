package com.example.noteslist.presentation.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.max

class ShimmerNotesListView @JvmOverloads constructor(
    context: Context,
    attrs : AttributeSet? = null,
    defStyleAttr : Int = 0,
) : View(context, attrs, defStyleAttr) {
    private val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = RectF()

    private var shimmerOffset = 0f

    private val horizontalPaddingPx = 16.dpToPx.toFloat()
    private val cardHeightPx = 96.dpToPx.toFloat()
    private val cardSpacingPx = 16.dpToPx.toFloat()
    private val cornerRadiusPx = 16.dpToPx.toFloat()

    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 1_000L
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()

        addUpdateListener { animation ->
            shimmerOffset = animation.animatedValue as Float
            invalidate()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (!animator.isStarted) {
            animator.start()
        }
    }

    override fun onDetachedFromWindow() {
        animator.cancel()

        super.onDetachedFromWindow()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (width == 0 || height == 0) return

        val shimmerWidth = width * 0.8f
        val startX = -shimmerWidth + (width + shimmerWidth * 2f) * shimmerOffset

        cardPaint.shader = LinearGradient(
            startX,
            0f,
            startX + shimmerWidth,
            0f,
            intArrayOf(
                0xFFE0E0E0.toInt(),
                0xFFF5F5F5.toInt(),
                0xFFE0E0E0.toInt(),
            ),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP,
        )

        val itemStep = cardHeightPx + cardSpacingPx
        val itemsCount = max(1, (height / itemStep).toInt() + 1)

        for (index in 0 until itemsCount) {
            val top = index * itemStep
            val bottom = top + cardHeightPx

            rect.set(
                horizontalPaddingPx,
                top,
                width - horizontalPaddingPx,
                bottom,
            )

            canvas.drawRoundRect(
                rect,
                cornerRadiusPx,
                cornerRadiusPx,
                cardPaint,
            )
        }
    }
}