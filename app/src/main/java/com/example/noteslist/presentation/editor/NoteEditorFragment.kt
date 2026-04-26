package com.example.noteslist.presentation.editor

import android.content.Context
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
import androidx.compose.runtime.collectAsState
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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.noteslist.R
import com.example.noteslist.presentation.state.NoteEditorUiState
import com.example.noteslist.presentation.view.MainActivity
import com.example.noteslist.presentation.viewModel.EditorHostViewModel
import com.example.noteslist.presentation.viewModel.NoteEditorViewModel
import javax.inject.Inject

class NoteEditorFragment : Fragment(R.layout.fragment_note_editor) {
    @Inject
    lateinit var viewModelFactory : ViewModelProvider.Factory
    /** ViewModel для навигации (список - редактор заметки) */
    private val editorHostViewModel : EditorHostViewModel by activityViewModels {
        viewModelFactory
    }
    /** ViewModel для хранения состояния UI */
    private val noteEditorViewModel : NoteEditorViewModel by activityViewModels {
        viewModelFactory
    }
    private val args : NoteEditorFragmentArgs by navArgs()

    companion object {
        private const val NOTE_EDITOR_RESULT_KEY = "note_editor_result"
    }

    /** Прицепляем фрагмент к MainActivity */
    override fun onAttach(context: Context) {
        (context as MainActivity)
            .activityComponent
            .noteEditorFragmentComponentFactory()
            .create()
            .inject(this)

        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val composeView = view.findViewById<ComposeView>(R.id.compose_note_editor)
        composeView.setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )

        noteEditorViewModel.init(
            note = args.note,
            isEditMode = args.isEditMode
        )

        composeView.setContent {
            MaterialTheme {
                val uiState by noteEditorViewModel.uiState.collectAsState()

                NoteEditorScreen (
                    uiState = uiState,
                    onTitleChanged = { title ->
                        noteEditorViewModel.onTitleChanged(title)
                    },
                    onDescriptionChanged = { description ->
                        noteEditorViewModel.onDescriptionChanged(description)
                    },
                    onIsImportantChanged = { isImportant ->
                        noteEditorViewModel.onIsImportantChanged(isImportant)
                    },
                    onIsViewedChanged = { isViewed ->
                        noteEditorViewModel.onIsViewedChanged(isViewed)
                    },
                    onSaveClick = {
                        if (noteEditorViewModel.saveNote()) {
                            editorHostViewModel.close()
//                            closeEditor()
                        }
                    },
                    onCloseRequest = { editorHostViewModel.close() }
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
    uiState : NoteEditorUiState,
    onTitleChanged : (String) -> Unit,
    onDescriptionChanged : (String) -> Unit,
    onIsImportantChanged : (Boolean) -> Unit,
    onIsViewedChanged : (Boolean) -> Unit,
    onSaveClick: () -> Unit,
    onCloseRequest : () -> Unit,
) {
    var showOnDiscardChangesDialog by remember { mutableStateOf(false) }

    BackHandler {
        if (uiState.isChanged) {
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
            text = if (uiState.isEditMode) "Редактирование заметки" else "Новая заметка",
            style = MaterialTheme.typography.headlineSmall,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.title,
            onValueChange = onTitleChanged,
            isError = uiState.showEmptyTitleError,
        )
        if (uiState.showEmptyTitleError) {
            Text(
                text = "Необходимо заполнить",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.description,
            onValueChange = onDescriptionChanged,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Важно")
            Checkbox(
                checked = uiState.isImportant,
                onCheckedChange = onIsImportantChanged,
            )
        }

        if (uiState.isEditMode) {
            Spacer(modifier = Modifier.height(12.dp))

            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Прочитана")
                Switch(
                    checked = uiState.isViewed,
                    onCheckedChange = onIsViewedChanged
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Создана: ${uiState.createdAtText}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (uiState.isEditMode) "Сохранить" else "Добавить")
        }
    }
}