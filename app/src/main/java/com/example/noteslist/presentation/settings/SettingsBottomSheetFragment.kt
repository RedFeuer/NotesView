package com.example.noteslist.presentation.settings

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.noteslist.domain.settings.StackSettings
import com.example.noteslist.presentation.state.SettingsUiState
import com.example.noteslist.presentation.view.MainActivity
import com.example.noteslist.presentation.viewModel.SettingsViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject
import kotlin.math.roundToInt

class SettingsBottomSheetFragment : BottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: SettingsViewModel by viewModels {
        viewModelFactory
    }

    override fun onAttach(context: Context) {
        (context as MainActivity)
            .activityComponent
            .settingsBottomSheetComponentFactory()
            .create()
            .inject(this)

        super.onAttach(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext())

        val composeView = ComposeView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )

            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {
                MaterialTheme {
                    val uiState by viewModel.uiState.collectAsState()

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .background(MaterialTheme.colorScheme.surface)
                            .navigationBarsPadding(),
                        color = MaterialTheme.colorScheme.surface,
                    ) {
                        SettingsBottomSheetContent(
                            uiState = uiState,
                            onStackSpacingChanged = viewModel::onStackSpacingChanged,
                            onStackMaxVisibleChanged = viewModel::onStackMaxVisibleChanged,
                        )
                    }
                }
            }
        }

        dialog.setContentView(composeView)

        dialog.setOnShowListener { shownDialog ->
            val bottomSheetDialog = shownDialog as BottomSheetDialog

            val bottomSheet = bottomSheetDialog
                .findViewById<FrameLayout>(
                    com.google.android.material.R.id.design_bottom_sheet
                )

            bottomSheet?.let { sheet ->
                sheet.setBackgroundColor(Color.WHITE)

                sheet.layoutParams = sheet.layoutParams.apply {
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                }

                val behavior = BottomSheetBehavior.from(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }

        return dialog
    }

    companion object {
        const val TAG = "SettingsBottomSheetFragment"
    }
}

@Composable
private fun SettingsBottomSheetContent(
    uiState: SettingsUiState,
    onStackSpacingChanged: (Int) -> Unit,
    onStackMaxVisibleChanged: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
    ) {
        Text(
            text = "Настройки стопок",
            style = MaterialTheme.typography.headlineSmall,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Расстояние между элементами: ${uiState.stackSpacingDp} dp",
            style = MaterialTheme.typography.bodyLarge,
        )

        Slider(
            value = uiState.stackSpacingDp.toFloat(),
            onValueChange = { value ->
                onStackSpacingChanged(value.roundToInt())
            },
            valueRange = StackSettings.MIN_STACK_SPACING_DP.toFloat()..
                    StackSettings.MAX_STACK_SPACING_DP.toFloat(),
            steps = StackSettings.MAX_STACK_SPACING_DP -
                    StackSettings.MIN_STACK_SPACING_DP - 1,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Максимум видимых заметок: ${uiState.stackMaxVisible}",
            style = MaterialTheme.typography.bodyLarge,
        )

        Slider(
            value = uiState.stackMaxVisible.toFloat(),
            onValueChange = { value ->
                onStackMaxVisibleChanged(value.roundToInt())
            },
            valueRange = StackSettings.MIN_STACK_MAX_VISIBLE.toFloat()..
                    StackSettings.MAX_STACK_MAX_VISIBLE.toFloat(),
            steps = StackSettings.MAX_STACK_MAX_VISIBLE -
                    StackSettings.MIN_STACK_MAX_VISIBLE - 1,
        )
    }
}