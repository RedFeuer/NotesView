package com.example.noteslist.presentation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
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
            updateDescriptionLayout() // при изменении текста пересчитываем layout
            invalidate()
        }
    /* layout для разметки текста, обработки переносов и fade */
    private var descriptionLayout: StaticLayout? = null
    /* флаг, указывающий, что текст описания не помещается и нужно делать fade в конце */
    private var descriptionOverflow: Boolean = false
    /* ширина текста описания в пикселях */
    private var descriptionTextWidthPx: Int = 0
    /* высота 2 строку в пикселях */
    private var descriptionTextMaxHeightPx: Int = 0
    /* дата и время создание заметки */
    var createdAtText: String? = null
        set(value) {
            field = value
            invalidate()
        }

    private fun buildStaticLayout(
        text: String,
        maxLines: Int,
        paint: TextPaint,
        widthPx: Int,
    ) : StaticLayout {
        return StaticLayout.Builder.obtain(text, 0, text.length, paint, widthPx)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setIncludePad(false)
            .setLineSpacing(0f,1.0f)
            .setMaxLines(maxLines)
            .build()
    }

    /* заметка */
    private val cardRect = RectF()
    /* заголовок */
    private val headerRect = RectF()

    /* Paint'ы */
    private val cardPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.RED
    }

    private val headerPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.BLUE
    }

    private val titleTextPaint = TextPaint().apply {
        isAntiAlias = true
        isSubpixelText = true
        typeface = Typeface.DEFAULT_BOLD
    }

    private val descriptionTextPaint = TextPaint().apply {
        isAntiAlias = true
        isSubpixelText = true
    }

    private val createdAtTextPaint = TextPaint().apply {
        isAntiAlias = true
        isSubpixelText = true
    }

    private val fadePaint = Paint().apply {
        isAntiAlias = true
    }

    /* константы */
    companion object {
        /* скругление карточки */
        const val cornerRadiusPx = 32f
        /* высота заголовка */
        const val headerHeightPx = 144f
        /* ширина fade для description */
        const val fadeWidthPx = 108f
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
        /* обновляем description текст */
        updateDescriptionLayout()
    }

    private fun updateDescriptionLayout() {
        /* если описание пустое, не строим layout и сбрасываем флаг overflow */
        val text = description?.takeIf { it.isNotBlank() } ?: run {
            descriptionLayout = null
            descriptionOverflow = false
            return
        }

        val w = descriptionTextWidthPx
        if (w <= 0) return // если ширина не задана, не строим layout

        /* выставляем флаг, что строк больше 2 */
        val full = buildStaticLayout(
            text = text,
            maxLines = Int.MAX_VALUE,
            paint = descriptionTextPaint,
            widthPx = w,
        )
        descriptionOverflow = full.lineCount > 2

        /* текстовая разметка, ограниченная 2 строками*/
        descriptionLayout = buildStaticLayout(
            text = text,
            maxLines = 2,
            paint = descriptionTextPaint,
            widthPx = w,
        ).also { layout ->
            descriptionTextMaxHeightPx = when {
                layout.lineCount >= 2 -> layout.getLineBottom(1)
                layout.lineCount == 1 -> layout.getLineBottom(0)
                else -> 0
            }
        }
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

        /* считаем ширину description (отнимаем два отступа - слева и справа) */
        val innerPaddingX = 36f
        descriptionTextWidthPx = (cardRect.width() - 2 * innerPaddingX).toInt()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        /* при каждой перерисовке рисуем карточку заметки и заголовок */
        drawCard(canvas)
        drawHeader(canvas)

        /* текст */
        drawTitle(canvas)
        drawDescription(canvas)
        drawCreatedAt(canvas)
    }

    private fun drawCard(canvas: Canvas) {
        canvas.drawRoundRect(cardRect, cornerRadiusPx, cornerRadiusPx, cardPaint)
    }

    private fun drawHeader(canvas: Canvas) {
        canvas.drawRoundRect(headerRect,cornerRadiusPx, cornerRadiusPx, headerPaint)
    }

    private fun drawTitle(canvas: Canvas) {
        val text = title?.takeIf { it.isNotBlank() } ?: return

        /* отступы внутри header */
        val startX = headerRect.left + 36f
        val startY = headerRect.top +36f

        val fontMetrics = titleTextPaint.fontMetrics
        val baseline = startY - fontMetrics.ascent // базовая линия для текста

        canvas.drawText(text, startX, baseline, titleTextPaint)
    }

    /* отрисовка текста описания заметки: 2 строки максимум + фейд, если больше*/
    private fun drawDescription(canvas: Canvas) {
        val layout = descriptionLayout ?: return
        if (descriptionTextMaxHeightPx <= 0 || descriptionTextWidthPx <= 0) return

        val startX = cardRect.left + 36f
        val startY = headerRect.bottom + 36f

        canvas.save()
        canvas.translate(startX, startY)

        /* рисуем только первые 2 строки */
        canvas.clipRect(
            0f,
            0f,
            descriptionTextWidthPx.toFloat(),
            descriptionTextMaxHeightPx.toFloat()
        )
        layout.draw(canvas)

        /* если текста больше 2 строчек, то фейдим конец 2-й строки */
        if (descriptionOverflow && layout.lineCount >= 2) {
            val line = 1 // 2-я строка
            val top = layout.getLineTop(line).toFloat()
            val bottom = layout.getLineBottom(line).toFloat()

            /* конец текста на 2-й строке */
            val lineRight = layout.getLineRight(line).coerceAtMost(descriptionTextWidthPx.toFloat())

            if (lineRight > 0) { // если строка пустая - нечего фейдить
                /* границы фейда:
                * справа - конец текса, слева - отступ для фейда */
                val fadeRight = lineRight
                val fadeLeft = (lineRight - fadeWidthPx).coerceAtLeast(0f)

                /* фон под фейд - цвет карточки */
                val bgColor = cardPaint.color
                val transparentBgColor = (bgColor and 0x00FFFFFF) // alpha = 0

                fadePaint.shader = LinearGradient(
                    fadeLeft, 0f,
                    fadeRight, 0f,
                    transparentBgColor,
                    bgColor,
                    Shader.TileMode.CLAMP
                )

                canvas.drawRect(fadeLeft, top, fadeRight, bottom, fadePaint)
                fadePaint.shader = null
            }
        }

        canvas.restore()
    }

    private fun drawCreatedAt(canvas: Canvas) {
        val text = createdAtText?.takeIf { it.isNotBlank() } ?: return

        /* отступы внутри карточки */
        val startX = cardRect.left + 36f
        val startY = cardRect.bottom - 36f

        val fontMetrics = createdAtTextPaint.fontMetrics
        val baseline = startY - fontMetrics.descent // базовая линия для текста

        canvas.drawText(text, startX, baseline, createdAtTextPaint)
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

                cardPaint.color = typedArray.getColor(R.styleable.NoteView_cardColor, cardPaint.color)
                headerPaint.color = typedArray.getColor(R.styleable.NoteView_headerColor, headerPaint.color)
            }
            finally {
                /* избегаем утечек памяти */
                typedArray.recycle()
            }
        }
    }

    private fun initPaints() {
        titleTextPaint.apply {
            textSize = resources.getDimension(R.dimen.note_view_title_text_size)
        }
        descriptionTextPaint.apply {
            textSize = resources.getDimension(R.dimen.note_view_description_text_size)
        }
        createdAtTextPaint.apply {
            textSize = resources.getDimension(R.dimen.note_view_created_at_text_size)
        }
    }
}