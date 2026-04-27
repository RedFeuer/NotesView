package com.example.noteslist.presentation

import android.content.res.Resources
import android.util.TypedValue

/* вспомогательное extension-свойство для конвертации dp в пиксели для корректного отображения на разных устройствах */
val Float.dpToPx: Float
    get() = this * Resources.getSystem().displayMetrics.density

val Int.dpToPx: Int
    get() = this * Resources.getSystem().displayMetrics.density.toInt()

val Float.spToPx: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        Resources.getSystem().displayMetrics
    )

val Int.spToPx: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()