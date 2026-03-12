package com.example.noteslist.presentation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
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
    /* координаты Title */
    private var titleTextStartX = 0f
    private var titleTextBaselineY = 0f
    var isImportant: Boolean = false
        set(value) {
            field = value
            updateTitleTextPosition()
            invalidate()
        }
    var isViewed: Boolean = false
        set(value) {
            field = value
            initStyle()
            updateSize()
            updateDescriptionLayout()
            invalidate()
        }
    var description: String? = null
        set(value) {
            field = value
            updateDescriptionLayout() // при изменении текста пересчитываем layout
            invalidate()
        }

    /* иконка-галочка для просмотренной заметки */
    private val viewedIcon: Drawable? = AppCompatResources.getDrawable(context, R.drawable.baseline_done_outline_24)?.mutate()
    private val viewedIconSizePx = dp(VIEWED_ICON_SIZE_DP).toInt()
    private val viewedIconMarginPx = dp(VIEWED_ICON_MARGIN_DP).toInt()
    /* размеры иконки просмотренной задачи */
    private val viewedIconBounds = Rect()
    /* иконка-звездочка для важной заметки */
    private val importantIcon: Drawable? = AppCompatResources.getDrawable(context, R.drawable.outline_bookmark_star_24)?.mutate()
    private val importantIconSizePx = dp(IMPORTANT_ICON_SIZE_DP).toInt()
    private val importantIconMarginPx = dp(IMPORTANT_ICON_MARGIN_DP).toInt()
    /* размеры иконки для важной заметки */
    private val importantIconBounds = Rect()
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
    /* параметры для фейда описания, если текста больше 2 строк */
    private var descriptionFadeLeft = 0f
    private var descriptionFadeRight = 0f
    private var descriptionFadeTop = 0f
    private var descriptionFadeBottom = 0f
    /* флаг видимости фейда */
    private var descriptionFadeVisible = false

    private val createdAtTextPaint = TextPaint().apply {
        isAntiAlias = true
        isSubpixelText = true
    }

    private val fadePaint = Paint().apply {
        isAntiAlias = true
    }

    private val innerTextPaddingPx = dp(INNER_TEXT_PADDING_DP)
    private var cornerRadiusPx = dp(CORNER_RADIUS_DP)
    private var elevationPx = dp(ELEVATION_DP)
    private val headerHeightPx = dp(HEADER_HEIGHT_DP)
    private val fadeWidthPx = dp(FADE_WIDTH_DP)

    /* константы - значения по умолчанию. По сути дублируют dimens.xml */
    companion object {
        /* ширина карточки по умолчанию, если не указано в разметке (dimens.xml) */
        private const val DEFAULT_WIDTH_DP = 200f
        /* высота карточки по умолчанию, если не указано в разметке (dimens.xml) */
        private const val DEFAULT_HEIGHT_DP = 80f
        /* отступ любого текста от начала карточки */
        private const val INNER_TEXT_PADDING_DP = 18f
        /* скругление карточки */
        private const val CORNER_RADIUS_DP = 16f
        /* тень для карточки */
        private const val ELEVATION_DP = 8f
        /* высота заголовка */
        private const val HEADER_HEIGHT_DP = 72f
        /* максимальное количество строк в description */
        private const val MAX_DESCRIPTION_LINES = 2
        /* ширина fade для description */
        private const val FADE_WIDTH_DP = 72f
        /* размер иконки галочки, что заметка прочитана */
        private const val VIEWED_ICON_SIZE_DP = 20f
         /* отступ иконки галочки от края карточки */
         private const val VIEWED_ICON_MARGIN_DP = 16f
        /* размер иконки звездочки, что заметка важная */
        private const val IMPORTANT_ICON_SIZE_DP = 32f
        /* отступ иконки звездочки от края карточки */
        private const val IMPORTANT_ICON_MARGIN_DP = 16f
    }

    private var defaultWidthPx = dp(DEFAULT_WIDTH_DP)
    private var defaultHeightPx = dp(DEFAULT_HEIGHT_DP)

    init {
        /* добавили кликабельность */
        isClickable = true
        isFocusable = true

        /* отключаем аппаратное ускорение для корректного отображения тени и фейда */
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        context.resources.apply {
            /* получаем размеры карточки из dimens.xml */
            defaultWidthPx = getDimension(R.dimen.note_view_width)
            defaultHeightPx = getDimension(R.dimen.note_view_height)
        }

        /* инициализация атрибутов */
        initAttrs(attrs, defStyleAttr, defStyleRes)

        /* инициализация цветов */
        initStyle()

        /* инициализация Paint'ов */
        initPaints()
    }

    /* вспомогательная функция для конвертации dp в пиксели для корректного отображения на разных устройствах */
    private fun dp(v: Float) = v * resources.displayMetrics.density

    private fun titleStartX(): Float {
        val base = headerRect.left + innerTextPaddingPx
        if (!isImportant) {
            return base
        }
        return base + importantIconSizePx + importantIconMarginPx
    }

    override fun performClick(): Boolean {
        super.performClick()
        /* при клике помечаем заметку как просмотренную и перерисовываем */
        if (!isViewed) {
            isViewed = true
        }
//        isViewed = !isViewed // для теста - переключение состояния при каждом клике
        return true
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

    private fun updateDescriptionFadeShader() {
        if (!descriptionFadeVisible) {
            fadePaint.shader = null
            return
        }

        /* фон под фейд - цвет карточки в зависимости от того просмотрена она или нет */
        val bgColor = cardPaint.color
        val transparentBgColor = (bgColor and 0x00FFFFFF) // alpha = 0

        fadePaint.shader = LinearGradient(
            descriptionFadeLeft, 0f,
            descriptionFadeRight, 0f,
            transparentBgColor,
            bgColor,
            Shader.TileMode.CLAMP
        )
    }

    private fun updateDescriptionLayout() {
        /* если описание пустое, не строим layout и сбрасываем флаг overflow */
        val text = description?.takeIf { it.isNotBlank() } ?: run {
            descriptionLayout = null
            descriptionOverflow = false
            descriptionTextMaxHeightPx = 0
            descriptionFadeVisible = false
            fadePaint.shader = null
            return
        }

        val w = descriptionTextWidthPx
        if (w <= 0) return // если ширина не задана, не строим layout

        /* текстовая разметка, ограниченная MAX_DESCRIPTION_LINES строками*/
        descriptionLayout = buildStaticLayout(
            text = text,
            maxLines = Int.MAX_VALUE,
            paint = descriptionTextPaint,
            widthPx = w,
        ).also { layout ->
            descriptionOverflow = layout.lineCount > MAX_DESCRIPTION_LINES

            val lastLineIndex = min(layout.lineCount, MAX_DESCRIPTION_LINES) - 1
            descriptionTextMaxHeightPx = if (lastLineIndex >= 0) layout.getLineBottom(lastLineIndex) else 0

            if (descriptionOverflow && lastLineIndex >= 0) {
                /* если текст не помещается, то показываем фейд */
                val lineRight = layout.getLineRight(lastLineIndex)
                    .coerceAtMost(descriptionTextWidthPx.toFloat())

                descriptionFadeVisible = lineRight > 0f
                descriptionFadeRight = lineRight
                descriptionFadeLeft = (lineRight - fadeWidthPx).coerceAtLeast(0f)
                descriptionFadeTop = layout.getLineTop(lastLineIndex).toFloat()
                descriptionFadeBottom = layout.getLineBottom(lastLineIndex).toFloat()
            }
            else {
                descriptionFadeVisible = false
                descriptionFadeLeft = 0f
                descriptionFadeRight = 0f
                descriptionFadeTop = 0f
                descriptionFadeBottom = 0f
            }
        }
        updateDescriptionFadeShader()
    }

    /* пересчет размеров заметки (карточка + название) */
    private fun updateSize(w: Int = width, h: Int = height) {
        val shadowPaddingPx = elevationPx

        val left = paddingLeft.toFloat() + shadowPaddingPx
        val top = paddingTop.toFloat() + shadowPaddingPx
        val right = w.toFloat() - paddingRight.toFloat() - shadowPaddingPx
        val bottom = h.toFloat() - paddingBottom.toFloat() - shadowPaddingPx

        /* карточка и хедер */
        cardRect.set(left, top, right, bottom)

        val headerBottom = min(cardRect.top + headerHeightPx, cardRect.bottom)
        headerRect.set(cardRect.left, cardRect.top, cardRect.right, headerBottom)

        /* текст */
        updateTitleTextPosition()

        /* иконки */
        updateViewedIconSize()
        updateImportantIconSize()

        /* считаем ширину description (отнимаем два отступа - слева и справа) */
        val innerPaddingX = innerTextPaddingPx
        descriptionTextWidthPx = (cardRect.width() - 2 * innerPaddingX).toInt()
    }

    /* подсчет размеров иконки "просмотрено" */
    private fun updateViewedIconSize() {
        val right = (cardRect.right - viewedIconMarginPx).toInt()
        val left = right - viewedIconSizePx
        val bottom = (cardRect.bottom - viewedIconMarginPx).toInt()
        val top = bottom - viewedIconSizePx
        viewedIconBounds.set(left, top, right, bottom)
    }

    /* подсчет размеров иконки "важное" */
    private fun updateImportantIconSize() {
        val left = (headerRect.left + importantIconMarginPx).toInt()
        val right = left + importantIconSizePx
        val top = (headerRect.top + importantIconMarginPx).toInt()
        val bottom = top + importantIconSizePx
        importantIconBounds.set(left, top, right, bottom)
    }

    /* вычисляем позицию текста заголовка заметки */
    private fun updateTitleTextPosition() {
        /* отступы внутри header */
        titleTextStartX = titleStartX()

        val startY = headerRect.top + innerTextPaddingPx
        val fontMetrics = titleTextPaint.fontMetrics
        titleTextBaselineY = startY - fontMetrics.ascent // базовая линия для текста
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        /* при каждой перерисовке рисуем карточку заметки и заголовок */
        drawCard(canvas)
        drawHeader(canvas)

        if (isImportant) {
            drawImportantIcon(canvas)
        }

        /* текст */
        drawTitle(canvas)
        drawDescription(canvas)
        drawCreatedAt(canvas)

        /* если заметка просмотрена, рисуем галочку */
        if (isViewed) {
            drawViewedIcon(canvas)
        }
    }

    /* заливаем фон карточки в зависимости от того, прочитана ли заметка */
    private fun drawCard(canvas: Canvas) {
        val paint = cardPaint

        if (elevationPx > 0) {
            /* рисуем тень для карточки, если задано значение elevation */
            paint.setShadowLayer(
                elevationPx,
                0f,
                elevationPx / 2, // смещение тени вниз для более реалистичного эффекта
                Color.argb(50, 0, 0, 0) // полупрозрачная белая тень
            )
        }
        else {
            paint.clearShadowLayer() // если elevation 0, убираем тень
        }

        canvas.drawRoundRect(cardRect, cornerRadiusPx, cornerRadiusPx, paint)
    }

    /* заливаем фон заголовка в зависимости от того, прочитана ли заметка */
    private fun drawHeader(canvas: Canvas) {
        val paint = headerPaint
        canvas.drawRoundRect(headerRect,cornerRadiusPx, cornerRadiusPx, paint)
    }

    private fun drawImportantIcon(canvas: Canvas) {
        val icon = importantIcon ?: return

        icon.bounds = importantIconBounds

        icon.draw(canvas)
    }

    private fun drawTitle(canvas: Canvas) {
        val text = title?.takeIf { it.isNotBlank() } ?: return

        canvas.drawText(text, titleTextStartX, titleTextBaselineY, titleTextPaint)
    }

    /* отрисовка текста описания заметки: 2 строки максимум + фейд, если больше*/
    private fun drawDescription(canvas: Canvas) {
        val layout = descriptionLayout ?: return
        if (descriptionTextMaxHeightPx <= 0 || descriptionTextWidthPx <= 0) return

        val startX = cardRect.left + innerTextPaddingPx
        val startY = headerRect.bottom + innerTextPaddingPx

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
        if (descriptionFadeVisible) {
            canvas.drawRect(
                descriptionFadeLeft,
                descriptionFadeTop,
                descriptionFadeRight,
                descriptionFadeBottom,
                fadePaint
            )
        }

        canvas.restore()
    }

    private fun drawCreatedAt(canvas: Canvas) {
        val text = createdAtText?.takeIf { it.isNotBlank() } ?: return

        /* отступы внутри карточки */
        val startX = cardRect.left + innerTextPaddingPx
        val startY = cardRect.bottom - innerTextPaddingPx

        val fontMetrics = createdAtTextPaint.fontMetrics
        val baseline = startY - fontMetrics.descent // базовая линия для текста

        val paint = createdAtTextPaint

        canvas.drawText(text, startX, baseline, paint)
    }

    private fun drawViewedIcon(canvas: Canvas) {
        val icon = viewedIcon ?: return

        icon.bounds = viewedIconBounds

        icon.draw(canvas)
    }

    /* инициализация атрибутов-свойств */
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

    /* инициализация стилевых атрибутов отдельно от других атрибутов-свойств */
    private fun initStyle() {
        val styleRes = if (isViewed) {
            R.style.NoteStyle_IsViewed
        } else {
            R.style.NoteStyle_IsNotViewed
        }

        val typedArray = context.obtainStyledAttributes(styleRes, R.styleable.NoteView)
        try {
            /* считываем параметры из styles.xml */
            cornerRadiusPx = typedArray.getDimension(R.styleable.NoteView_noteCornerRadius, cornerRadiusPx)
            elevationPx = typedArray.getDimension(R.styleable.NoteView_noteElevation, elevationPx)

            /* цвета заливки карточки и заголовка */
            cardPaint.color = typedArray.getColor(R.styleable.NoteView_cardColor, cardPaint.color)
            headerPaint.color = typedArray.getColor(R.styleable.NoteView_headerColor, headerPaint.color)

            /* цвета заливки текста */
            titleTextPaint.color = typedArray.getColor(R.styleable.NoteView_titleTextColor, titleTextPaint.color)
            descriptionTextPaint.color = typedArray.getColor(R.styleable.NoteView_descriptionTextColor, descriptionTextPaint.color)
            createdAtTextPaint.color = typedArray.getColor(R.styleable.NoteView_createdAtTextColor, createdAtTextPaint.color)
        }
        finally {
            typedArray.recycle()
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