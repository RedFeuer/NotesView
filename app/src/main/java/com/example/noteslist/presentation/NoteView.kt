package com.example.noteslist.presentation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.noteslist.R
import kotlin.math.min

class NoteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    var title: String? = null
        set(value) {
            field = value // backing field
            invalidate()
        }
    var isImportant: Boolean = false
        set(value) {
            field = value
            invalidate()
        }
    var isViewed: Boolean = false
        set(value) {
            field = value
            invalidate()
        }
    var description: String? = null
        set(value) {
            field = value
            invalidate()
        }
    var createdAtText: String? = null
        set(value) {
            field = value
            invalidate()
        }

    /* заметка */
    private val cardRect = RectF()
    /* заголовок */
    private val headerRect = RectF()

    /* Paint'ы */
    private val cardPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.WHITE
    }

    private val headerPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.BLUE
    }

    private val titleTextPaint = Paint().apply {
        isAntiAlias = true
        isSubpixelText = true
    }

    private val descriptionTextPaint = Paint().apply {
        isAntiAlias = true
        isSubpixelText = true
    }

    private val createdAtTextPaint = Paint().apply {
        isAntiAlias = true
        isSubpixelText = true
    }

    /* константы */
    companion object {
        /* скругление карточки */
        const val cornerRadiusPx = 16f
        /* высота заголовка */
        const val headerHeightPx = 56f
    }

    /* размеры по умолчанию, если не указано в разметке (dimens.xml) */
    private var defaultWidthPx = 200f
    private var defaultHeightPx = 80f

    init {
        context.resources.apply {
            /* получаем размеры карточки из dimens.xml */
            defaultWidthPx = getDimension(R.dimen.note_view_width)
            defaultHeightPx = getDimension(R.dimen.note_view_height)
        }

        /* инициализация атрибутов */
        initAttrs(attrs, defStyleAttr, defStyleRes)

        /* инициализация Paint'ов */
        initPaints()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        /* предпочитаемые ширина и высота */
        val desiredWidth = defaultWidthPx.toInt() + paddingLeft + paddingRight
        val desiredHeight = defaultHeightPx.toInt() + paddingTop + paddingBottom

        /* measureSpec:
        * EXACTLY - берем ровно measureSpec
        * AT_MOST - если desired влазит - берем его. Иначе - AT_MOST
        * UNSPECIFIED - берем desired */
        val measuredWidth = resolveSize(desiredWidth, widthMeasureSpec)
        val measuredHeight = resolveSize(desiredHeight, heightMeasureSpec)

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    /* callback вызывается при изменении размера после onMeasure() и onLayout() перед onDraw() */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateSize(w, h) // пересчитываем размеры только при их изменении
    }

    /* пересчет размеров заметки (карточка + название) */
    private fun updateSize(w: Int = width, h: Int = height) {
        val left = paddingLeft.toFloat()
        val top = paddingTop.toFloat()
        val right = w.toFloat() - paddingRight.toFloat()
        val bottom = h.toFloat() - paddingBottom.toFloat()

        cardRect.set(left, top, right, bottom)

        val headerBottom = min(cardRect.top + headerHeightPx, cardRect.bottom)
        headerRect.set(cardRect.left, cardRect.top, cardRect.right, headerBottom)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        /* при каждой перерисовке рисуем карточку заметки и заголовок */
        drawCard(canvas)
        drawHeader(canvas)
    }

    private fun drawCard(canvas: Canvas) {
        canvas.drawRoundRect(cardRect, cornerRadiusPx, cornerRadiusPx, cardPaint)
    }

    private fun drawHeader(canvas: Canvas) {
        canvas.drawRoundRect(headerRect,cornerRadiusPx, cornerRadiusPx, headerPaint)
    }

    private fun initAttrs(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(
                it,
                R.styleable.NoteView,
                defStyleAttr,
                defStyleRes,
            )
            try {
                /* получаем свойства из /res/values/attrs.xml и записываем в поля класса */
                title = typedArray.getString(R.styleable.NoteView_title)
                isImportant = typedArray.getBoolean(R.styleable.NoteView_isImportant, isImportant)
                isViewed = typedArray.getBoolean(R.styleable.NoteView_isViewed, isViewed)
                description = typedArray.getString(R.styleable.NoteView_description)
                createdAtText = typedArray.getString(R.styleable.NoteView_createdAtText)
            }
            finally {
                /* избегаем утечек памяти */
                typedArray.recycle()
            }
        }
    }

    private fun initPaints() {
        titleTextPaint.apply {
            textSize = 18.toFloat()
        }
        descriptionTextPaint.apply {
            textSize = 14.toFloat()
        }
        createdAtTextPaint.apply {
            textSize = 12.toFloat()
        }
    }
}