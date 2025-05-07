package com.gravatar.quickeditor.ui.editor

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
            when (uiState.page) {
                QuickEditorPage.AVATAR_PICKER -> onDoneClicked()
                QuickEditorPage.ABOUT_EDITOR -> aboutEditorViewModel.onEvent(AboutEditorEvent.OnDoneClicked)
            }
        },
        onEditAvatarClicked = { viewModel.onEvent(QuickEditorEvent.OnEditAvatarClicked) },
        onEditAboutClicked = { viewModel.onEvent(QuickEditorEvent.OnEditAboutClicked) },
    ) {
        AnimatedContent(
            targetState = uiState.page,
            transitionSpec = {
                fadeIn(animationSpec = tween(220, delayMillis = 90))
                    .togetherWith(fadeOut(animationSpec = tween(90)))
            },
        ) { state ->
            when (state) {
                QuickEditorPage.AVATAR_PICKER -> {
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

                QuickEditorPage.ABOUT_EDITOR -> {
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
}

@Composable
internal fun QuickEditor(
    uiState: QuickEditorUiState,
    onDoneClicked: () -> Unit,
    onEditAvatarClicked: () -> Unit,
    onEditAboutClicked: () -> Unit,
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
                            editAvatarEnabled = uiState.editAvatarButtonVisible,
                            editAboutEnabled = uiState.editAboutButtonVisible,
                            avatarCacheBuster = uiState.avatarCacheBuster.toString(),
                            onEditAvatarClicked = onEditAvatarClicked,
                            onEditAboutClicked = onEditAboutClicked,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        )
                        content()
                    }
                }
            },
        )
    }
}
