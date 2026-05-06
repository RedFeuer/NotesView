package com.example.noteslist.domain.settings

import kotlin.math.roundToInt

data class StackSettings(
    val stackSpacingDp : Int = DEFAULT_STACK_SPACING_VERTICAL_DP,
    val stackMaxVisible : Int = DEFAULT_STACK_MAX_VISIBLE,
) {
    /** текущий отступ между заметками в стеке */
    val stackSpacingVerticalDp : Int
        get() = stackSpacingDp

    /** текущий горизонтальный сдвиг видимых заметок в стеке */
    val stackSpacingHorizontalDp : Int
        get() = (stackSpacingDp * STACK_HORIZONTAL_SPACING_RATIO).roundToInt()

    companion object {
        /** отступ между заметками в стеке */
        const val DEFAULT_STACK_SPACING_VERTICAL_DP = 20
        /** горизонтальный сдвиг видимых заметок в стеке */
        const val DEFAULT_STACK_SPACING_HORIZONTAL_DP = 8
        /** максимальное количество видимых заметок в стеке */
        const val DEFAULT_STACK_MAX_VISIBLE = 3
        /** минимальный и максимальный отступы между заметкаами в стеке */
        const val MIN_STACK_SPACING_DP = 0
        const val MAX_STACK_SPACING_DP = 64
        /** минимальное и максимальное максимальное количество видимых заметок в стеке */
        const val MIN_STACK_MAX_VISIBLE = 1
        const val MAX_STACK_MAX_VISIBLE = 5

        /** отношение горизонтального сдвига к вертикальному: 8dp / 20dp = 0.4 */
        private const val STACK_HORIZONTAL_SPACING_RATIO : Float =
            (DEFAULT_STACK_SPACING_HORIZONTAL_DP.toFloat())/(DEFAULT_STACK_SPACING_VERTICAL_DP.toFloat())
    }
}