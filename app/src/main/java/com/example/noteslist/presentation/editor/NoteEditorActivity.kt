package com.example.noteslist.presentation.editor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.noteslist.data.repositoryImpl.NotesRepositoryImpl
import com.example.noteslist.domain.domainModel.Note
import com.example.noteslist.presentation.view.NoteMapper
import java.util.Date

class NoteEditorActivity : ComponentActivity() {
    private val repository = NotesRepositoryImpl.instance // Singleton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mode = intent.getStringExtra(EXTRA_MODE) ?: MODE_ADD
        val noteUiId = intent.getStringExtra(EXTRA_NOTE_UI_ID)
        val initialNote = noteUiId?.let(repository::getNoteById)

        setContent {
            MaterialTheme {
                NoteEditorScreen(
                    initialNote = initialNote,
                    isEditMode = mode == MODE_EDIT,
                    onSaveClick = { title, description, isImportant, isViewed ->
                        if (mode == MODE_EDIT && initialNote != null) {
                            repository.updateNote(
                                initialNote.copy(
                                    title = title,
                                    description = description,
                                    isImportant = isImportant,
                                    isViewed = isViewed,
                                )
                            )
                        } else {
                            repository.addNote(
                                Note(
                                    title = title,
                                    description = description,
                                    isImportant = isImportant,
                                )
                            )
                        }
                        finish()
                    }
                )
            }
        }
    }

    companion object {
        private const val EXTRA_MODE = "extra_mode"
        private const val EXTRA_NOTE_UI_ID = "extra_note_ui_id"
        private const val MODE_ADD = "mode_add"
        private const val MODE_EDIT = "mode_edit"

        /** открывает экран NoteEditorActivity как создание новой заметки */
        fun createAddIntent(context: Context) : Intent {
            return Intent(context, NoteEditorActivity::class.java).apply {
                putExtra(EXTRA_MODE, MODE_ADD)
            }
        }
        /** открывает экран NoteEditorActivity как редактирование существующей заметки */
        fun createEditIntent(context: Context, noteUiId : String) : Intent {
            return Intent(context, NoteEditorActivity::class.java).apply {
                putExtra(EXTRA_MODE, MODE_EDIT)
                putExtra(EXTRA_NOTE_UI_ID, noteUiId)
            }
        }
    }
}

@Composable
private fun NoteEditorScreen(
    initialNote : Note?, // заметка для редактирования
    isEditMode : Boolean,
    onSaveClick : (
        title : String?,
        description : String?,
        isImportant : Boolean,
        isViewed : Boolean,
    ) -> Unit,
) {
    /* если берем в режиме редактирования, то достаем конкретную заметку initialNote из репозитория
    * если хотим создать новую заметку, то поля пустые */
    var title by remember { mutableStateOf(initialNote?.title.orEmpty()) }
    var description by remember { mutableStateOf(initialNote?.description.orEmpty()) }
    var isImportant by remember { mutableStateOf(initialNote?.isImportant ?: false) }
    var isViewed by remember { mutableStateOf(initialNote?.isViewed ?: false) }
    var showEmptyTitleError by remember { mutableStateOf(false) }

    val noteMapper = NoteMapper()

    val createdAtText = remember(initialNote?.createdAtMillis) {
        initialNote?.createdAtMillis?.let {
//            SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(it))
            noteMapper.createdAtFormatter.format(Date(it))
        }.orEmpty()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = if (isEditMode) "Редактирование заметки" else "Новая заметка",
            style = MaterialTheme.typography.headlineSmall,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                if (it.isNotBlank()) {
                    showEmptyTitleError = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Заголовок") },
            singleLine = true,
            isError = showEmptyTitleError,
        )
        if (showEmptyTitleError) {
            Text(
                text = "Необходимо заполнить",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            label = { Text("Текст заметки") },
            singleLine = false,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Важно")
            Checkbox(
                checked = isImportant,
                onCheckedChange = { isImportant = it },
            )
        }

        if (isEditMode) {
            Spacer(modifier = Modifier.height(12.dp))

            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Прочитана")
                Switch(
                    checked = isViewed,
                    onCheckedChange = { isViewed = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Создана: $createdAtText",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (title.isBlank()) {
                    showEmptyTitleError = true
                    return@Button
                }

                onSaveClick(
                    title.trim(),
                    description.takeIf { it.isNotBlank() },
                    isImportant,
                    isViewed,
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isEditMode) "Сохранить" else "Добавить")
        }
    }
}