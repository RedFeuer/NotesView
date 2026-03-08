package com.example.noteslist.presentation

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isGone
import com.example.noteslist.R
import com.example.noteslist.domain.domainModel.Note

class NoteStackView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ViewGroup(context, attrs, defStyleAttr) {
    private var stackSpacingPx: Int = STACK_SPACING_DP.dpToPx.toInt()
    private var stackMaxSize: Int = STACK_MAX_SIZE

    /* флаг, указывающий, развернут ли стек заметок или свернут */
    private var isExpanded: Boolean = false
    /* список заметок, отображаемых в стеке.
    Храним Note всех заметок, а отображаемые задаются через NoteView внутри NoteStackView.
    В реальной реализации это будет приходить из ViewModel, а не храниться внутри View */
    private val notes = mutableListOf<Note>()
    /* маппер Note -> NoteUi */
    private val noteMapper = NoteMapper()

    /* кнопка сворачивания заметок в развернутом состоянии */
    private val collapseView: AppCompatTextView = AppCompatTextView(context).apply {
        text = "<< Свернуть"
        textSize = 16f
        setPadding(12.dpToPx, 8.dpToPx, 12.dpToPx, 8.dpToPx)
        isClickable = true
        isFocusable = true
        setOnClickListener {
            setExpanded(false)
        }
    }

    /* константы - значения по умолчанию. По сути дублируют dimens.xml */
    companion object {
        /* отступ между заметками в стеке */
        private const val STACK_SPACING_DP = 20f
        /* максимальное количество видимых заметок в стеке */
        private const val STACK_MAX_SIZE = 3
    }

    init {
        initView()

        initAttrs(attrs, defStyleAttr)

        initListener()
    }

    private fun initListener() {
        /* при клике на NoteStackView переключаем состояние между развернутым и свернутым */
        setOnClickListener {
            if (notes.isNotEmpty() && !isExpanded) {
                setExpanded(true)
            }
        }
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
                stackSpacingPx = typedArray.getDimensionPixelSize(
                    R.styleable.NoteStackView_stackSpacing,
                    STACK_SPACING_DP.dpToPx.toInt()
                )
                stackMaxSize = typedArray.getInt(
                    R.styleable.NoteStackView_stackMaxSize,
                    STACK_MAX_SIZE
                ).coerceAtLeast(1) // гарантируем, что максимальный размер стека не меньше 1
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

    fun submitNotes(newNotes: List<Note>) {
        notes.clear()
        notes += newNotes.sortedByDescending { it.createdAtMillis } // сортируем заметки по времени создания, самые свежие сверху
        rebuildChildren() // обновляем отображение заметок в стеке
    }

    /* метод для изменения состояния стека между развернутым и свернутым
    * обновляет состояние экрана */
    private fun setExpanded(expanded: Boolean) {
        if (expanded == isExpanded) return // если состояние не изменилось, ничего не делаем
        isExpanded = expanded
        rebuildChildren() // обновляем отображение заметок в стеке при изменении состояния
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

            /* в свернутом состоянии кликабелен сам стек, а не отдельные заметки
            в развернутом состоянии кликабельность и фокус у каждой заметки,
            чтобы можно было взаимодействовать с ними индивидуально*/
            isClickable = isExpanded
            isFocusable = isExpanded
        }
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
            if (childCount > 0) {
                /* свернутый стек,
                высота стека - это высота верхней заметки + отступы */
                val frontChildHeight = getChildAt(childCount - 1).measuredHeight
                totalHeight += frontChildHeight + stackSpacingPx * (stackMaxSize - 1) // добавляем отступы для остальных заметок в стеке
            }
        }
        else {
            /* развернуттый стек,
            высота стека - это сумма высот всех заметок (View) + отступы */
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child.isGone) continue

                totalHeight += child.measuredHeight// добавляем высоту заметки
                if (i != childCount - 1) {
                    totalHeight += stackSpacingPx // добавляем отступ между заметками, кроме последней
                }
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
                val top = paddingTop + i * stackSpacingPx
                val bottom = top + child.measuredHeight
                child.layout(left, top, right, bottom)
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

                /* обновляем текущую верхнюю позицию для следующей заметки, добавляя высоту текущей заметки и отступ */
                currentTop = bottom
                if (i != childCount - 1) {
                    currentTop += stackSpacingPx // добавляем отступ между заметками, кроме последней
                }
            }
        }
    }
}