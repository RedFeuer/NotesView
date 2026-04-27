package com.example.noteslist.presentation.view.animation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.PathInterpolator

class NoteStackViewAnimation(
    private val stackInterpolator: PathInterpolator,
) {
    /** расчет длительности анимации всех элементов и кнопки
     * [itemCount] - количество элементов в стеке, под которое расчитываем длительность анимации*/
    fun calculateMoveDuration(itemCount: Int): Long {
        return (NoteStackViewAnimationSpec.BASE_DURATION_MS + itemCount * NoteStackViewAnimationSpec.STEP_DURATION_MS) // 200 + n*40
            .coerceAtMost(NoteStackViewAnimationSpec.MAX_DURATION_MS) // сверху ограничили 800ms
    }

    /** анимация кнопки Свернуть */
    fun buildCollapseButtonAnimator(
        startDelay: Long,
        view : View,
    ): AnimatorSet {
        val alphaAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f)
        val scaleXAnimator = ObjectAnimator.ofFloat(
            view, // объект, свойство которого меняем
            View.SCALE_X, // свойство объекта
            NoteStackViewAnimationSpec.COLLAPSE_BUTTON_START_SCALE, // ОТ
            NoteStackViewAnimationSpec.COLLAPSE_BUTTON_END_SCALE // ДО
        )
        val scaleYAnimator = ObjectAnimator.ofFloat(
            view, // объект, свойство которого меняем
            View.SCALE_Y, // свойство объекта
            NoteStackViewAnimationSpec.COLLAPSE_BUTTON_START_SCALE, // ОТ
            NoteStackViewAnimationSpec.COLLAPSE_BUTTON_END_SCALE // ДО
        )

        /* анимация кнопки: aplha от 0 до 1, scaleX/scaleY от 0.7 до 1.0, время анимиации 200мс. */
        return AnimatorSet().apply {
            playTogether(alphaAnimator, scaleXAnimator, scaleYAnimator) // комбинируем движение
            this.startDelay = startDelay // задержка перед стартом для кнопки
            duration =
                NoteStackViewAnimationSpec.COLLAPSE_BUTTON_ANIMATION_MS // продолжительность движения
            interpolator = stackInterpolator // неравномерность анимации
        }
    }
}