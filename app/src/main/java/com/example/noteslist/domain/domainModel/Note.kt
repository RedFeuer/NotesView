package com.example.noteslist.domain.domainModel

data class Note(
    /* TODO: можно как в гуглзаметках добавить что-то типо bullet-листа с задачами
    *   сделать sealed Note и разные сущности:
    * 1) обычная заметка с одной записью - как та, что сейчас есть
    * 2) заметка с списком дел */

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
    /* время создания
    * в UI это уже String - надо будет подумать над кастом*/
    val createdAtMillis: Long = System.currentTimeMillis(), // по умолчанию - текущее время
)