package com.example.noteslist.presentation.view

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.noteslist.R
import com.example.noteslist.presentation.editor.NoteEditorFragment

//тут будешь ваша активити
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    when {
                        isTwoPane() && isDetailEditorOpened() -> {
                            closeDetailEditor()
                        }
                        !isTwoPane() && isEditorOpenedNavHost() -> {
                            popEditorFromNavHost()
                        }
                        else -> {
                            showExitConfirmationDialog()
                        }
                    }
                }
            }
        )
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