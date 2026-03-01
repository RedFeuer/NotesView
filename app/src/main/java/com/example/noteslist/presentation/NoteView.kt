package com.example.noteslist.presentation

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.example.noteslist.R

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
    init {
        initAttrs(attrs, defStyleAttr, defStyleRes)
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
}