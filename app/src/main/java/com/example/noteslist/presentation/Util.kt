package com.example.noteslist.presentation

import android.content.res.Resources

/* вспомогательное extension-свойство для конвертации dp в пиксели для корректного отображения на разных устройствах */
val Float.dpToPx: Float
    get() = this * Resources.getSystem().displayMetrics.density