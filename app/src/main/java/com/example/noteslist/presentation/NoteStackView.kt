package com.example.noteslist.presentation

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.example.noteslist.domain.domainModel.Note
import androidx.core.view.isGone

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

    /* константы - значения по умолчанию. По сути дублируют dimens.xml */
    companion object {
        /* отступ между заметками в стеке */
        private const val STACK_SPACING_DP = 20f
        /* максимальное количество видимых заметок в стеке */
        private const val STACK_MAX_SIZE = 3
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