package com.example.noteslist.presentation.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.noteslist.domain.settings.StackSettings
import com.example.noteslist.presentation.state.SettingsUiState
import com.example.noteslist.presentation.view.MainActivity
import com.example.noteslist.presentation.viewModel.SettingsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject
import kotlin.math.roundToInt

class SettingsBottomSheetFragment : BottomSheetDialogFragment() {
    @Inject
    lateinit var viewModelFactory : ViewModelProvider.Factory

    private val viewModel : SettingsViewModel by viewModels {
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {
                MaterialTheme {
                    val uiState by viewModel.uiState.collectAsState()

                    SettingsBottomSheetContent(
                        uiState = uiState,
                        onStackSpacingChanged = { newSpacing -> viewModel.onStackSpacingChanged(newSpacing) },
                        onStackMaxVisibleChanged = { newCount -> viewModel.onStackMaxVisibleChanged(newCount) }
                    )
                }
            }
        }
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