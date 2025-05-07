package com.gravatar.quickeditor.ui.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gravatar.quickeditor.ui.abouteditor.AboutEditor
import com.gravatar.quickeditor.ui.abouteditor.AboutEditorEvent
import com.gravatar.quickeditor.ui.abouteditor.AboutEditorViewModel
import com.gravatar.quickeditor.ui.abouteditor.AboutEditorViewModelFactory
import com.gravatar.quickeditor.ui.avatarpicker.AvatarPicker
import com.gravatar.quickeditor.ui.components.EmailLabel
import com.gravatar.quickeditor.ui.components.ProfileCard
import com.gravatar.quickeditor.ui.components.QEPageDefault
import com.gravatar.ui.GravatarTheme

@Composable
internal fun QuickEditor(
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    handleExpiredSession: Boolean,
    onAvatarSelected: () -> Unit,
    onSessionExpired: () -> Unit,
    onDoneClicked: () -> Unit,
    onAltTextTapped: (email: String, avatarId: String) -> Unit,
    viewModel: QuickEditorViewModel = viewModel(
        factory = QuickEditorViewModelFactory(gravatarQuickEditorParams),
    ),
) {
    val uiState by viewModel.uiState.collectAsState()
    val aboutEditorViewModel: AboutEditorViewModel = viewModel(
        factory = AboutEditorViewModelFactory(gravatarQuickEditorParams),
    )

    QuickEditor(
        uiState = uiState,
        onDoneClicked = {
            when (gravatarQuickEditorParams.scope) {
                QuickEditorScope.AVATAR -> onDoneClicked()
                QuickEditorScope.ABOUT ->
                    aboutEditorViewModel.onEvent(AboutEditorEvent.OnDoneClicked)
            }
        },
    ) {
        when (gravatarQuickEditorParams.scope) {
            QuickEditorScope.AVATAR -> {
                AvatarPicker(
                    gravatarQuickEditorParams = gravatarQuickEditorParams,
                    handleExpiredSession = handleExpiredSession,
                    onAvatarSelected = {
                        onAvatarSelected()
                        viewModel.onEvent(QuickEditorEvent.UpdateAvatarCache)
                    },
                    onSessionExpired = onSessionExpired,
                    onAltTextTapped = onAltTextTapped,
                    onRefresh = {
                        viewModel.onEvent(QuickEditorEvent.Refresh)
                    },
                )
            }

            QuickEditorScope.ABOUT -> {
                AboutEditor(
                    quickEditorParams = gravatarQuickEditorParams,
                    onProfileUpdated = {
                        viewModel.onEvent(QuickEditorEvent.OnProfileUpdated(it))
                    },
                    onClose = onDoneClicked,
                    viewModel = aboutEditorViewModel,
                )
            }
        }
    }
}

@Composable
internal fun QuickEditor(
    uiState: QuickEditorUiState,
    onDoneClicked: () -> Unit,
    content: @Composable () -> Unit = {},
) {
    GravatarTheme {
        QEPageDefault(
            onDoneClicked = onDoneClicked,
            content = {
                Surface {
                    Column {
                        EmailLabel(
                            email = uiState.email,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                        )
                        ProfileCard(
                            profile = uiState.profile,
                            email = uiState.email,
                            editAvatarEnabled = true,
                            editProfileEnabled = true,
                            avatarCacheBuster = uiState.avatarCacheBuster.toString(),
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        )
                        content()
                    }
                }
            },
        )
    }
}
