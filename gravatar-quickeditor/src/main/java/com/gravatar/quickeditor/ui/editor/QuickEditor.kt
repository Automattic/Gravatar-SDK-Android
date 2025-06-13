package com.gravatar.quickeditor.ui.editor

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowHeightSizeClass
import com.gravatar.quickeditor.ui.abouteditor.AboutEditor
import com.gravatar.quickeditor.ui.abouteditor.AboutEditorViewModel
import com.gravatar.quickeditor.ui.abouteditor.AboutEditorViewModelFactory
import com.gravatar.quickeditor.ui.abouteditor.components.DiscardChangesAlertDialog
import com.gravatar.quickeditor.ui.avatarpicker.AvatarPicker
import com.gravatar.quickeditor.ui.components.EmailLabel
import com.gravatar.quickeditor.ui.components.ProfileCard
import com.gravatar.quickeditor.ui.components.QEPageDefault
import com.gravatar.ui.GravatarTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

@Composable
internal fun QuickEditor(
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    showEmail: Boolean,
    handleExpiredSession: Boolean,
    updateHandler: UpdateHandler,
    confirmDismissal: Boolean,
    onDismissIgnored: () -> Unit,
    onSessionExpired: () -> Unit,
    onDoneClicked: () -> Unit,
    onAltTextTapped: (email: String, avatarId: String) -> Unit,
    windowSizeClass: WindowHeightSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowHeightSizeClass,
    viewModel: QuickEditorViewModel = viewModel(
        factory = QuickEditorViewModelFactory(
            gravatarQuickEditorParams = gravatarQuickEditorParams,
            compactWindowEnabled = windowSizeClass == WindowHeightSizeClass.COMPACT,
            showEmail = showEmail,
        ),
    ),
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val uiState by viewModel.uiState.collectAsState()
    val aboutEditorViewModel: AboutEditorViewModel = viewModel(
        factory = AboutEditorViewModelFactory(gravatarQuickEditorParams, handleExpiredSession),
    )

    LaunchedEffect(windowSizeClass) {
        viewModel.onEvent(QuickEditorEvent.OnCompactWindowEnabled(windowSizeClass == WindowHeightSizeClass.COMPACT))
    }

    LaunchedEffect(confirmDismissal) {
        if (confirmDismissal) {
            viewModel.onEvent(QuickEditorEvent.OnConfirmDismissal)
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Main.immediate) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actions.collectLatest { action ->
                    when (action) {
                        QuickEditorAction.DismissEditor -> onDoneClicked()
                        QuickEditorAction.NotifyDismissIgnored -> onDismissIgnored()
                    }
                }
            }
        }
    }

    QuickEditor(
        uiState = uiState,
        onDoneClicked = { viewModel.onEvent(QuickEditorEvent.OnConfirmDismissal) },
        onEditAvatarClicked = { viewModel.onEvent(QuickEditorEvent.OnEditAvatarClicked) },
        onEditAboutClicked = { viewModel.onEvent(QuickEditorEvent.OnEditAboutClicked) },
    ) {
        Box {
            AnimatedContent(
                targetState = uiState.page,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220, delayMillis = 90))
                        .togetherWith(fadeOut(animationSpec = tween(90)))
                },
            ) { state ->
                when (state) {
                    QuickEditorPage.AvatarPicker -> {
                        AvatarPicker(
                            gravatarQuickEditorParams = gravatarQuickEditorParams,
                            handleExpiredSession = handleExpiredSession,
                            onAvatarSelected = {
                                updateHandler(AvatarPickerResult)
                                viewModel.onEvent(QuickEditorEvent.UpdateAvatarCache)
                            },
                            onSessionExpired = onSessionExpired,
                            onAltTextTapped = onAltTextTapped,
                            onRefresh = {
                                viewModel.onEvent(QuickEditorEvent.Refresh)
                            },
                        )
                    }

                    QuickEditorPage.AboutEditor -> {
                        AboutEditor(
                            onProfileUpdated = { profile ->
                                updateHandler(AboutEditorResult(profile))
                                viewModel.onEvent(QuickEditorEvent.OnProfileUpdated(profile))
                            },
                            onUnsavedChangesUpdated = { unsavedChanges ->
                                viewModel.onEvent(QuickEditorEvent.OnAboutEditorUnsavedChangesUpdated(unsavedChanges))
                            },
                            onRefresh = { viewModel.onEvent(QuickEditorEvent.Refresh) },
                            onSessionExpired = onSessionExpired,
                            viewModel = aboutEditorViewModel,
                        )
                    }
                }
            }
            DiscardChangesAlertDialog(
                visible = uiState.discardAboutEditorChangesDialogVisible,
                onKeepEditing = {
                    viewModel.onEvent(QuickEditorEvent.OnUnsavedChangesKeepEditingClicked)
                },
                onDiscardClicked = {
                    viewModel.onEvent(QuickEditorEvent.OnUnsavedChangesExitClicked)
                },
            )
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
                        if (!uiState.compactWindow) {
                            if (uiState.emailVisible) {
                                EmailLabel(
                                    email = uiState.email,
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                )
                            }
                            Spacer(Modifier.height(10.dp))
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
                        }
                        content()
                    }
                }
            },
        )
    }
}
