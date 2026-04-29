package com.example.noteslist.presentation.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.PathInterpolator
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.doOnNextLayout
import androidx.core.view.isGone
import androidx.core.view.isNotEmpty
import com.example.noteslist.R
import com.example.noteslist.domain.domainModel.Note
import com.example.noteslist.domain.settings.StackSettings
import com.example.noteslist.presentation.view.animation.NoteStackViewAnimation
import com.example.noteslist.presentation.view.animation.NoteStackViewAnimationSpec.COLLAPSE_BUTTON_DELAY_MS
import com.example.noteslist.presentation.view.animation.NoteStackViewAnimationSpec.COLLAPSE_BUTTON_END_SCALE
import com.example.noteslist.presentation.view.animation.NoteStackViewAnimationSpec.COLLAPSE_BUTTON_START_SCALE
import com.example.noteslist.presentation.view.animation.NoteStackViewAnimationSpec.ITEM_START_DELAY_MS

class NoteStackView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ViewGroup(context, attrs, defStyleAttr) {
    private var stackSpacingVerticallyPx: Int =
        StackSettings.DEFAULT_STACK_SPACING_VERTICAL_DP.dpToPx
    private var stackSpacingHorizontallyPx: Int =
        StackSettings.DEFAULT_STACK_SPACING_HORIZONTAL_DP.dpToPx
    private var stackMaxSize: Int =
        StackSettings.DEFAULT_STACK_MAX_VISIBLE
    private var collapseTextSizePx = COLLAPSE_TEXT_SIZE_SP.spToPx
    private var horizontalPaddingPx = HORIZONTAL_PADDING_DP.dpToPx
    private var verticalPaddingPx = VERTICAL_PADDING_DP.dpToPx

    /* флаг, указывающий, развернут ли стек заметок или свернут */
    private var isExpanded: Boolean = false
    /* список заметок, отображаемых в стеке.
    Храним Note всех заметок, а отображаемые задаются через NoteView внутри NoteStackView.
    В реальной реализации это будет приходить из ViewModel, а не храниться внутри View */
    private val notes = mutableListOf<Note>()
    /* маппер Note -> NoteUi */
    private val noteMapper = NoteMapper()

    /* кнопка сворачивания заметок в развернутом состоянии */
    private val collapseView = AppCompatTextView(context)
    /** флаг, что идет анимация */
    private var isAnimating: Boolean = false
    /** множество Animator'ов с сохранением порядка */
    private var currentAnimatorSet: AnimatorSet? = null
    /** вспомогательный класс с функциями анимаций */
    /* TODO: когда добавим Hilt, нужно будет вынести в Module через DI вместе с stackInterpolator */
    private val viewAnimation by lazy {
        NoteStackViewAnimation(stackInterpolator)
    }
    /** cubic-bezier интерполятор */
    private val stackInterpolator by lazy {
        PathInterpolator(0.4f, 0.1f, 0.2f, 1f)
    }

    /** Callback клика по заметке (редактирования заметки)*/
    private var onNoteClick : ((Note) -> Unit)? = null
    /** Callback долгого клика по заметке (отметки заметки прочитанной) */
    private var onNoteLongClick : ((Note) -> Unit)? = null
    /** Callback клика по стеку (раскрытия стека заметок) */
    private var onExpandedChange : ((Boolean) -> Unit)? = null

    /* константы - значения по умолчанию. По сути дублируют dimens.xml */
    companion object {
        /* размер шрифта для текста "Свернуть" */
        private const val COLLAPSE_TEXT_SIZE_SP = 16f
        /* горизонтальные отступы */
        private const val HORIZONTAL_PADDING_DP = 16
         /* вертикальные отступы */
         private const val VERTICAL_PADDING_DP = 16
    }

    init {
        initView()
        initAttrs(attrs, defStyleAttr)
        initCollapseView()
        initListener()
    }

    private fun initCollapseView() {
        collapseView.apply {
            text = resources.getString(R.string.stack_collapse) // устанавливаем текст "Свернуть" из ресурсов
            setTextSize(TypedValue.COMPLEX_UNIT_PX, collapseTextSizePx) // px -> sp
            setPadding(horizontalPaddingPx, verticalPaddingPx, horizontalPaddingPx, verticalPaddingPx)
            isClickable = true
            isFocusable = true
            setOnClickListener {
                if (!isAnimating) {
                    setExpanded(false)
                }
            }
        }
    }

    private fun initListener() {
        /* при клике на NoteStackView переключаем состояние между свернутым и развернутым */
        setOnClickListener {
            if (notes.isNotEmpty() && !isExpanded && !isAnimating) {
                expandWithAnimation()
            }
        }
    }

    /** раскрытие с анимацией */
    fun expandWithAnimation() {
        /* пустой список или уже раскрыт или анимация уже запущена */
        if (notes.isEmpty() || isExpanded || isAnimating) return

        cancelCurrentAnimation()
        isAnimating = true

        setExpanded(true)

        collapseView.alpha = 0f
        collapseView.scaleX = COLLAPSE_BUTTON_START_SCALE
        collapseView.scaleY = COLLAPSE_BUTTON_START_SCALE
        collapseView.visibility = View.VISIBLE

        doOnNextLayout {
            startExpandAnimation()
        }
    }

    /** основная анимация раскрытия */
    private fun startExpandAnimation() {
        val noteCount = notes.size // количество заметок в стеке
        /* если пустой стек или не нужно открывать, то не запускаем анимацию */
        if (!isExpanded || noteCount == 0) {
            isAnimating = false
            return
        }

        val visibleCount = minOf(noteCount, stackMaxSize) // количество видимых в стеке заметок
        val moveDuration = viewAnimation.calculateMoveDuration(noteCount) // время выполнения анимации
        val animators = mutableListOf<Animator>()

        for (i in 0 until noteCount) {
            val child = getChildAt(i)

            /* индекс карточки внутри видимого стека
            * то есть первые три карточки стартуют со своих видимых слоев, а
            * все остальные карточки стартуют из позиции последнего видимого элемента */
            val collapsedLayerIndex = if (i < visibleCount) {
                visibleCount - 1 - i
            } else {
                0
            }

            /* стартовая позиция текущей карточки в стопке */
            val startLeft = paddingLeft + collapsedLayerIndex * stackSpacingHorizontallyPx
            val startTop = paddingTop + collapsedLayerIndex * stackSpacingVerticallyPx
            /* конечная позиция карточки в развернутом списке */
            val endLeft = child.left
            val endTop = child.top

            /* делаем временный визуальный сдвиг текущей карточки
            * никак не влияет на layout(). просто визуально отображает в другом месте
            * проще говоря: экранная_позиция = layout_позиция + translation */
            val startTranslationX = (startLeft - endLeft).toFloat()
            val startTranslationY = (startTop - endTop).toFloat()
            child.translationX = startTranslationX
            child.translationY = startTranslationY

            val translateXAnimator = ObjectAnimator.ofFloat(
                child, // объект, свойство которого меняем
                View.TRANSLATION_X, // свойство объекта
                startTranslationX, // ОТ: начинаем с визуально сдвинутого положения
                0f // ДО: без сдвига относительно layout
            )
            val translateYAnimator = ObjectAnimator.ofFloat(
                child, // объект, свойство которого меняем
                View.TRANSLATION_Y, // свойство объекта
                startTranslationY, // ОТ: начинаем с визуально сдвинутого положения
                0f // ДО: без сдвига относительно layout
            )

            animators += AnimatorSet().apply {
                playTogether(translateXAnimator, translateYAnimator) // комбинируем движение
                startDelay = i * ITEM_START_DELAY_MS // задержка перед стартом для текущей карточки
                duration = moveDuration // продолжительность движения
                interpolator = stackInterpolator // неравномерность анимации
            }
        }

        /* момент появления кнопки = последняя заметка + выполнение + ожидание появления кнопки */
        val buttonStartDelay =
            (noteCount - 1) * ITEM_START_DELAY_MS +
                    moveDuration +
                    COLLAPSE_BUTTON_DELAY_MS
        /* запуск анимации кнопки Свернуть */
        animators += viewAnimation.buildCollapseButtonAnimator(buttonStartDelay, collapseView)

        currentAnimatorSet = AnimatorSet().apply {
            playTogether(animators) // запуск всех анимаций в порядке по startDelay
            addListener(object : AnimatorListenerAdapter() {
                /* что делаем в конце анимации */
                override fun onAnimationEnd(animation: Animator) {
                    isAnimating = false
                    currentAnimatorSet = null

                    for (i in 0 until noteCount) {
                        val child = getChildAt(i)
                        child.translationX = 0f
                        child.translationY = 0f
                    }

                    collapseView.alpha = 1f
                    collapseView.scaleX = COLLAPSE_BUTTON_END_SCALE
                    collapseView.scaleY = COLLAPSE_BUTTON_END_SCALE
                }

                /* что делаем при отмене анимиции */
                override fun onAnimationCancel(animation: Animator) {
                    isAnimating = false
                    currentAnimatorSet = null
                }
            })
            start() // запускаем анимации
        }
    }

    /** отмена текущей анимации */
    private fun cancelCurrentAnimation() {
        currentAnimatorSet?.cancel()
        currentAnimatorSet = null
        isAnimating = false
    }

    private fun initAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(
                it,
                R.styleable.NoteStackView,
                defStyleAttr,
                0
            )

            try {
                collapseTextSizePx = typedArray.getDimension(
                    R.styleable.NoteStackView_stackCollapseTextSize,
                    COLLAPSE_TEXT_SIZE_SP.spToPx
                )
                horizontalPaddingPx = typedArray.getDimensionPixelSize(
                    R.styleable.NoteStackView_stackHorizontalPadding,
                    HORIZONTAL_PADDING_DP.dpToPx
                )
                verticalPaddingPx = typedArray.getDimensionPixelSize(
                    R.styleable.NoteStackView_stackVerticalPadding,
                    VERTICAL_PADDING_DP.dpToPx
                )
            }
            finally {
                /* избегаем утечек памяти */
                typedArray.recycle()
            }
        }
    }

    private fun initView() {
        /* добавили кликабельность */
        isClickable = true
        isFocusable = true

        /* отключили обрезку дочерних элементов, чтобы заметки могли выходить за пределы NoteStackView при наложении */
        clipChildren = false
        clipToPadding = false
    }

    /* метод для передачи новых заметок в NoteStackView и обновления отображения */
    fun submitNotes(
        newNotes: List<Note>,
        expanded : Boolean,
        settings: StackSettings,
    ) {
        updateStackSettings(settings)

        notes.clear()
        notes += newNotes.sortedByDescending { it.createdAtMillis } // сортируем заметки по времени создания, самые свежие сверху

        isExpanded = expanded

        rebuildChildren() // обновляем отображение заметок в стеке
    }

    private fun updateStackSettings(settings: StackSettings) : Boolean {
        val safeVerticalSpacingPx = settings.stackSpacingVerticalDp.dpToPx.coerceAtLeast(0)
        val safeHorizontalSpacingPx = settings.stackSpacingHorizontalDp.dpToPx.coerceAtLeast(0)
        val safeStackMaxVisible = settings.stackMaxVisible.coerceIn(
            minimumValue = StackSettings.MIN_STACK_MAX_VISIBLE,
            maximumValue = StackSettings.MAX_STACK_MAX_VISIBLE
        )

        val isChanged =
            stackSpacingVerticallyPx != safeVerticalSpacingPx ||
                    stackSpacingHorizontallyPx != safeHorizontalSpacingPx ||
                    stackMaxSize != safeStackMaxVisible

        if (!isChanged) return false

        if (isAnimating) {
            cancelCurrentAnimation()
        }

        stackSpacingVerticallyPx = safeVerticalSpacingPx
        stackSpacingHorizontallyPx = safeHorizontalSpacingPx
        stackMaxSize = safeStackMaxVisible

        return true
    }

    fun updateNote(updatedNote : Note) {
        val noteIndex = notes.indexOfFirst { it.id == updatedNote.id }
        if (noteIndex == -1) return

        notes[noteIndex] = updatedNote

        val childIndex = findChildIndexForNote(updatedNote.id) ?: return
        val child = getChildAt(childIndex) as? NoteView ?: return

        child.bind(noteMapper.mapDomainModelToUi(updatedNote))
    }

    /* ищем какой дочерний NoteView соответствует заметке */
    private fun findChildIndexForNote(noteId : Long) : Int? {
        return if (isExpanded) {
            /* развернутый стек */
            val index = notes.indexOfFirst { it.id == noteId }
            if (index == -1) null else index
        } else {
            /* свернутый стек */
            val visibleNotes = notes.take(stackMaxSize)
            val visibleIndex = visibleNotes.indexOfFirst { it.id == noteId }
            if (visibleIndex == -1) {
                /* невидимую карточку не обновляем */
                null
            } else {
                visibleNotes.size - 1 - visibleIndex // из-за asReversed()
            }
        }
    }

    /* метод для изменения состояния стека между развернутым и свернутым
    * обновляет состояние экрана */
    private fun setExpanded(expanded: Boolean) {
        if (expanded == isExpanded) return // если состояние не изменилось, ничего не делаем
        isExpanded = expanded
        onExpandedChange?.invoke(expanded)
        rebuildChildren() // обновляем отображение заметок в стеке при изменении состояния
    }

    fun setOnExpandedChange(onExpandedChange : (Boolean) -> Unit) {
        this.onExpandedChange = onExpandedChange
    }

    /* метод для перерисовки дочерних элементов NoteStackView при изменении данных или состояния стека */
    private fun rebuildChildren() {
        /* удаляем все текущие NoteView из NoteStackView */
        removeAllViews()

        if (notes.isEmpty()) {
            requestLayout()
            return
        }

        if (isExpanded) {
            /* развернутый стек,
            * самые свежие сверху, а внизу добавить кнопку "Свернуть"*/
            notes.forEachIndexed { index, note ->
                val child = createNoteView(note)
                child.elevation = (notes.size - index).toFloat()
                addView(child)
            }
            addView(collapseView) // добавляем кнопку "Свернуть" в конец стека
        } else {
            /* свернутый стек,
            * отображаем только верхнюю (самую новую) заметку, остальные скрываем */
            val visibleNotes = notes.take(stackMaxSize)
            visibleNotes.asReversed().forEachIndexed { index, note ->
                val child = createNoteView(note)
                child.elevation = (index + 1).toFloat()
                addView(child)
            }
        }
        requestLayout()
    }

    /* вспомогательный метод, создающий NoteView по View */
    private fun createNoteView(note: Note): NoteView {
        val noteUi : NoteUi =  noteMapper.mapDomainModelToUi(note) // преобразуем Note в NoteUi

        return NoteView(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )

            bind(noteUi) // привязываем данные заметки к NoteView: NoteUi -> NoteView

            /* при обычном клике - редактирование заметки
            * при долгом клике - отмечаем заметку помеченной*/
            isClickable = isExpanded
            isFocusable = isExpanded

            if (isExpanded) {
                setOnClickListener {
                    onNoteClick?.invoke(note)
                }

                setOnLongClickListener {
                    onNoteLongClick?.invoke(note)
                    true
                }
            }
            else {
                setOnClickListener(null) // отключаем клик для заметок в свернутом состоянии
                setOnLongClickListener(null)
            }
        }
    }

    fun setNoteActions(
        onNoteClick : (Note) -> Unit,
        onNoteLongClick : (Note) -> Unit,
    ) {
        this.onNoteClick = onNoteClick
        this.onNoteLongClick = onNoteLongClick
    }

    /* обработка клика по стеку, если он свернут */
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return !isExpanded || isAnimating // чтобы во время анимации не было кликов
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        /* ограничения по ширине от контейнера-родителя (NoteStackView) */
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        /* размер по ширине от контейнера-родителя (NoteStackView) */
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        /* отступы */
        val horizontalPadding = paddingLeft + paddingRight
        val verticalPadding = paddingTop + paddingBottom

        /* доступная ширина для заметок внутри стека с учетом отступов */
        val availableWidth = (widthSize - horizontalPadding).coerceAtLeast(0)

        /* измеряем дочерние элементы (NoteView) и вычисляем максимальную ширину и общую высоту стека заметок */
        var maxChildWidth = 0
        var totalHeight = verticalPadding

        val childWidthSpec = when (widthMode) {
            /* если ширина известна или ограничена - то измеряем его ровно в availableWidth
            * если ширина не задана - пусть View измеряется как хочет */
            MeasureSpec.EXACTLY,
            MeasureSpec.AT_MOST -> MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.EXACTLY)
            else -> MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        }

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.isGone) continue // пропускаем невидимые элементы

            /* измеряем дочерний элемент с учетом доступной ширины и неограниченной высоты */
            val childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            child.measure(childWidthSpec, childHeightSpec)

            /* максимальная ширина среди заметок в стеке */
            maxChildWidth = maxOf(maxChildWidth, child.measuredWidth)
        }

        if (!isExpanded) {
            if (this.isNotEmpty()) {
                /* свернутый стек,
                высота стека - это высота верхней заметки + отступы */
                val frontChildHeight = getChildAt(childCount - 1).measuredHeight
                totalHeight += frontChildHeight + stackSpacingVerticallyPx * (childCount - 1) // добавляем отступы для остальных заметок в стеке
            }
        }
        else {
            /* развернуттый стек,
            высота стека - это сумма высот всех заметок (View) + отступы */
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child.isGone) continue

                totalHeight += child.measuredHeight// добавляем высоту заметки
                totalHeight = addSpacingAmongNotes(totalHeight, stackSpacingVerticallyPx, i)
            }
        }

        /* устанавливаем измеренные размеры NoteStackView с учетом отступов и ограничений от родителя */
        val measuredWidth = resolveSize(maxChildWidth + horizontalPadding, widthMeasureSpec)
        val measuredHeight = resolveSize(totalHeight, heightMeasureSpec)

        /* устанавливаем измеренные размеры NoteStackView */
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int
    ) {
        val left = paddingLeft
        val right = width - paddingRight

        if (!isExpanded) {
            /* свернутый стек,
            * элементы накладываются друг на друга*/
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child.isGone) continue

                /* для создания эффекта наложения, каждый последующий элемент смещается вниз на stackSpacingPx относительно предыдущего */
                val leftNotExpended = paddingLeft + i * stackSpacingHorizontallyPx
                val topNotExpended = paddingTop + i * stackSpacingVerticallyPx
                val rightNotExpended = right
                val bottomNotExpended = topNotExpended + child.measuredHeight
                child.layout(leftNotExpended, topNotExpended, rightNotExpended, bottomNotExpended)
            }
        }
        else {
            /* развернутый стек,
            * элементы располагаются друг под другом с отступами */
            var currentTop = paddingTop

            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child.isGone) continue

                /* размещаем заметку на экране */
                val bottom = currentTop + child.measuredHeight
                child.layout(left, currentTop, right, bottom)

                currentTop = addSpacingAmongNotes(bottom, stackSpacingVerticallyPx, i)
            }
        }
    }

    /* добавляем отступ между развернутыми заметками, кроме последней */
    private fun addSpacingAmongNotes(height: Int, spacing: Int, index: Int): Int {
        return if (index != childCount - 1) {
            height + spacing
        } else {
            height
        }
    }
}