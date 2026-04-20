package com.example.noteslist.presentation.view

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.noteslist.R
import com.example.noteslist.domain.repository.NotesRepository
import com.example.noteslist.presentation.editor.NoteEditorFragment
import com.example.noteslist.presentation.editorhost.EditorDestination
import com.example.noteslist.presentation.list.NotesListFragmentDirections
import com.example.noteslist.presentation.viewModel.EditorHostViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

//тут будешь ваша активити
class MainActivity @Inject constructor(
    /* TODO: прокинуть зависимости */
    private val notesRepository: NotesRepository,
) : AppCompatActivity() {
    private val editorHostViewMode : EditorHostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        editorHostViewMode.destination
            .onEach { destination -> renderEditorDestination(destination) }
            .launchIn(lifecycleScope)

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    when {
                        isTwoPane() && isDetailEditorOpened() -> {
//                            closeDetailEditor()
                            editorHostViewMode.close()
                        }
                        !isTwoPane() && isEditorOpenedNavHost() -> {
//                            popEditorFromNavHost()
                            editorHostViewMode.close()
                        }
                        else -> {
                            showExitConfirmationDialog()
                        }
                    }
                }
            }
        )
    }

    private fun renderEditorDestination(destination : EditorDestination) {
        if (isTwoPane()) {
            renderTwoPaneEditor(destination)
        }
        else {
            renderSinglePaneEditor(destination)
        }
    }

    /** граф навигации внутри портретного экрана */
    private fun renderSinglePaneEditor(destination : EditorDestination) {
        val navHost = supportFragmentManager.findFragmentById(R.id.navHostFragment)
            as? NavHostFragment ?: return

        /* находим на каком мы экране сейчас, чтобы относительно этого и destination строить навигацию между этими экранами */
        val navController = navHost.navController
        val currentDestinationId = navController.currentDestination?.id

        when (destination) {
            EditorDestination.Closed -> {
                if (currentDestinationId == R.id.note_editor_fragment) {
                    navController.popBackStack()
                }
            }

            EditorDestination.Create -> {
                if (currentDestinationId != R.id.note_editor_fragment) {
                    /* создание заметки */
                    val direction = NotesListFragmentDirections.actionNotesListFragmentToNoteEditorFragment(
                        note = null,
                        isEditMode = false,
                    )
                    navController.navigate(direction)
                }
            }

            is EditorDestination.Edit -> {
                val note = notesRepository.getNoteById(destination.noteUiId) ?: return

                if (currentDestinationId != R.id.note_editor_fragment) {
                    val direction = NotesListFragmentDirections.actionNotesListFragmentToNoteEditorFragment(
                        note = note,
                        isEditMode = true,
                    )
                    navController.navigate(direction)
                }
            }
        }
    }

    /** граф навигации внутри ландшафтного экрана */
    private fun renderTwoPaneEditor(destination: EditorDestination) {
        when (destination) {
            EditorDestination.Closed -> {
                val fragment = supportFragmentManager.findFragmentById(R.id.detail_fragment_container)
                if (fragment != null) {
                    supportFragmentManager.commit { remove(fragment) }
                }
            }

            EditorDestination.Create -> {
                val args = bundleOf(
                    "note" to null,
                    "isEditMode" to false,
                )

                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    /* в контейнер detail_fragment_container кладем NoteEditorFragment  */
                    replace(R.id.detail_fragment_container, NoteEditorFragment::class.java, args)
                }
            }

            is EditorDestination.Edit -> {
                val note = notesRepository.getNoteById(destination.noteUiId) ?: return

                val args = bundleOf(
                    "note" to note,
                    "isEditMode" to true,
                )

                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace(R.id.detail_fragment_container, NoteEditorFragment::class.java, args)
                }
            }
        }
    }

    /** проверяем, есть ли в текущем layout правый контейнер detail_fragment_container
     * по сути проверка, что мы в ландшафтном режиме ориентации*/
    private fun isTwoPane() : Boolean {
        return findViewById<View?>(R.id.detail_fragment_container) != null
    }

    private fun isDetailEditorOpened() : Boolean {
        val fragment = supportFragmentManager.findFragmentById(R.id.detail_fragment_container)
        return fragment is NoteEditorFragment
    }

    private fun closeDetailEditor() {
        val fragment = supportFragmentManager.findFragmentById(R.id.detail_fragment_container)
            ?: return

        supportFragmentManager.beginTransaction()
            .remove(fragment)
            .commit()
    }

    private fun isEditorOpenedNavHost() : Boolean {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as? NavHostFragment
                ?: return false

        val currentDestinationId = navHost.navController.currentDestination?.id
        return currentDestinationId == R.id.note_editor_fragment
    }

    private fun popEditorFromNavHost() {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as? NavHostFragment
                ?: return

        navHost.navController.popBackStack()
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Подтверждение выхода")
            .setMessage("Вы точно хотите выйти?")
            .setNegativeButton("Нет", null)
            .setPositiveButton("Да") { _, _ ->
                finish()
            }
            .show()
    }
}