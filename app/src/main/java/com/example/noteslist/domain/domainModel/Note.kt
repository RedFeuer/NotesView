package com.example.noteslist.domain.domainModel

data class Note(
    /* уникальный идентификатор заметки */
    val id: Long = System.currentTimeMillis(), // по умолчанию id равен времени создания
    /* заголовок заметки */
    val title: String? = null, // по умолчанию создается заметка без названия
    /* важность заметки */
    val isImportant: Boolean = false, // по умолчанию все заметки неважные
    /* заметка была просмотрена */
    val isViewed: Boolean = false, // по умолчанию не была просмотрена
    /* описание заметки */
    val description: String? = null, // по умолчанию заметка с пустым описанием
    /* время создания */
    val createdAtMillis: Long = System.currentTimeMillis(), // по умолчанию - текущее время
)