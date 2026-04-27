package com.example.noteslist.presentation.view

/* класс хранения информации о заметке для отображения на экране
* не лазаем в Domain + имеем другой формат хранения времени создания и данные по умолчанию */
data class NoteUi(
    val title: String = "Название заметки",
    val isImportant: Boolean = false,
    val isViewed: Boolean = false,
    val description: String = "Текст заметки",
    val createdAt: String? = null,
)
