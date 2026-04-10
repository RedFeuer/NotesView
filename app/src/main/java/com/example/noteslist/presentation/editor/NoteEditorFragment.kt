package com.example.noteslist.presentation.editor

import android.os.Bundle
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.noteslist.R
import com.example.noteslist.data.repositoryImpl.NotesRepositoryImpl
import com.example.noteslist.domain.domainModel.Note
import com.example.noteslist.presentation.view.NoteMapper
import java.util.Date

class NoteEditorFragment : Fragment(R.layout.fragment_note_editor) {
    private val args : NoteEditorFragmentArgs by navArgs()
    /** Singleton репозитория для актуальности заметок */
    private val repository = NotesRepositoryImpl.instance

    companion object {
        private const val NOTE_EDITOR_RESULT_KEY = "note_editor_result"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val composeView = view.findViewById<ComposeView>(R.id.compose_note_editor)
        composeView.setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )

        composeView.setContent {
            MaterialTheme {
                NoteEditorScreen (
                    initialNote = args.note,
                    isEditMode = args.isEditMode,
                    onSaveClick = { title, description, isImportant, isViewed ->
                        if (args.isEditMode && args.note != null) {
                            repository.updateNote(
                                args.note!!.copy(
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
                        closeEditor()
                    },
                    onCloseRequest = { closeEditor() }
                )
            }
        }
    }

    /** закрываем экран редактирования или создания заметки в ландшафтном режиме, либо
     * уходим с этого экрана назад в портретном режиме */
    private fun closeEditor() {
        /* обновляем UI */
        requireActivity().supportFragmentManager.setFragmentResult(
            NOTE_EDITOR_RESULT_KEY,
            Bundle.EMPTY
        )

        if (isTwoPane()) {
            /* закрываем экран редактирования */
            parentFragmentManager.commit {
                remove(this@NoteEditorFragment)
            }
        } else {
            /* уходим на предыдущий экран (список заметок) */
            findNavController().popBackStack()
        }
    }

    /** проверяем, есть ли в текущем layout правый контейнер detail_fragment_container
     * по сути проверка, что мы в ландшафтном режиме ориентации*/
    private fun isTwoPane() : Boolean {
        return requireActivity().findViewById<View?>(R.id.detail_fragment_container) != null
    }
}

@Composable
private fun NoteEditorScreen(
    initialNote : Note?,
    isEditMode : Boolean,
    onSaveClick: (
        title : String,
        description : String?,
        isImportant : Boolean,
        isViewed : Boolean,
    ) -> Unit,
    onCloseRequest : () -> Unit,
) {
    var showOnDiscardChangesDialog by remember { mutableStateOf(false) }
    /* если берем в режиме редактирования, то достаем конкретную заметку initialNote из репозитория
    * если хотим создать новую заметку, то поля пустые */
    var title by remember { mutableStateOf(initialNote?.title.orEmpty()) }
    var description by remember { mutableStateOf(initialNote?.description.orEmpty()) }
    var isImportant by remember { mutableStateOf(initialNote?.isImportant ?: false) }
    var isViewed by remember { mutableStateOf(initialNote?.isViewed ?: false) }
    var showEmptyTitleError by remember { mutableStateOf(false) }

    val initialTitle = remember(initialNote) { initialNote?.title.orEmpty() }
    val initialDescription = remember(initialNote) { initialNote?.description.orEmpty() }
    val initialIsImportant = remember(initialNote) { initialNote?.isImportant ?: false }
    val initialIsViewed = remember(initialNote) { initialNote?.isViewed ?: false }

    val isChanged = (title != initialTitle ||
            description != initialDescription ||
            isImportant != initialIsImportant ||
            isViewed != initialIsViewed)

    BackHandler {
        if (isChanged) {
            showOnDiscardChangesDialog = true
        } else {
            onCloseRequest()
        }
    }

    if (showOnDiscardChangesDialog) {
        AlertDialog(
            onDismissRequest = { showOnDiscardChangesDialog = false },
            title = { Text("Закрыть без сохранения?") },
            text = { Text("Изменения будут потеряны") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showOnDiscardChangesDialog = false
                        onCloseRequest()
                    }
                ) {
                    Text("Закрыть")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showOnDiscardChangesDialog = false }
                ) {
                    Text("Остаться")
                }
            }
        )
    }

    val noteMapper = NoteMapper()

    val createdAtText = remember(initialNote?.createdAtMillis) {
        initialNote?.createdAtMillis?.let {
//            SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(it))
            noteMapper.createdAtFormatter.format(Date(it))
        }.orEmpty()
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(scrollState)
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
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Создана: $createdAtText",
                style = MaterialTheme.typography.bodyMedium
            )
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