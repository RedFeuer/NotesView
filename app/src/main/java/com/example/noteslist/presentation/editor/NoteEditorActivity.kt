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

class NoteEditorActivity : ComponentActivity() {
    private val repository = NotesRepositoryImpl.instance // Singleton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                NoteEditorScreen(
                    onAddClick = { title, description, isImportant ->
                        repository.addNote(
                            Note(
                                title = title,
                                description = description,
                                isImportant = isImportant,
                            )
                        )
                        /* завершаем NoteEditorActivity и делаем onResume() для MainActivity */
                        finish()
                    }
                )
            }
        }
    }

    companion object {
        fun createIntent(context: Context) : Intent {
            return Intent(context, NoteEditorActivity::class.java)
        }
    }
}

@Composable
private fun NoteEditorScreen(
    onAddClick: (title : String?, description : String?, isImportant : Boolean) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isImportant by remember { mutableStateOf(false) }
    var showEmptyTitleError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = "Новая заметка",
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

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (title.isBlank()) {
                    showEmptyTitleError = true
                    return@Button
                }

                onAddClick(
                    title.trim(),
                    description.takeIf { it.isNotBlank() },
                    isImportant
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить")
        }
    }
}