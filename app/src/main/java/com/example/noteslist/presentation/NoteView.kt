package com.example.noteslist.presentation

import android.content.Context
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
        const val headerHeightPx = 28f
    }

    init {
        /* инициализация атрибутов */
        initAttrs(attrs, defStyleAttr, defStyleRes)

        /* инициализация Paint'ов */
        initPaints()
    }

    /* callback вызывается при изменении размера после onMeasure() и onLayout() перед onDraw() */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateSize() // пересчитываем размеры только при их изменении
    }

    /* пересчет размеров заметки (карточка + название) */
    private fun updateSize() {
        val left = paddingLeft.toFloat()
        val top = paddingTop.toFloat()
        val right = width.toFloat() - paddingRight.toFloat()
        val bottom = height.toFloat() - paddingBottom.toFloat()

        cardRect.set(left, top, right, bottom)

        val headerBottom = min(cardRect.top + headerHeightPx, cardRect.bottom)
        headerRect.set(cardRect.left, cardRect.top, cardRect.right, headerBottom)
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