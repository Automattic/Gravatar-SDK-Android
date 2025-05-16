package com.gravatar.quickeditor.ui.abouteditor

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gravatar.quickeditor.R
import com.gravatar.quickeditor.ui.abouteditor.components.AboutSection
import com.gravatar.quickeditor.ui.abouteditor.components.DiscardChangesAlertDialog
import com.gravatar.quickeditor.ui.components.QEButton
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorParams
import com.gravatar.quickeditor.ui.extensions.QESnackbarHost
import com.gravatar.quickeditor.ui.extensions.SnackbarType
import com.gravatar.quickeditor.ui.extensions.showQESnackbar
import com.gravatar.restapi.models.Profile
import com.gravatar.ui.GravatarTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
internal fun AboutEditor(
    quickEditorParams: GravatarQuickEditorParams,
    onProfileUpdated: (Profile) -> Unit,
    onDismissIgnored: () -> Unit,
    onClose: () -> Unit,
    viewModel: AboutEditorViewModel = viewModel(
        factory = AboutEditorViewModelFactory(quickEditorParams),
    ),
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val snackState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Main.immediate) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actions.collect { action ->
                    when (action) {
                        is AboutEditorAction.ProfileUpdated -> {
                            onProfileUpdated(action.profile)
                            coroutineScope.launch {
                                snackState.showQESnackbar(
                                    message = context.getString(
                                        R.string.gravatar_qe_about_save_profile_success_message,
                                    ),
                                    withDismissAction = true,
                                )
                            }
                        }

                        AboutEditorAction.ProfileUpdateFailed -> {
                            coroutineScope.launch {
                                snackState.showQESnackbar(
                                    message = context.getString(
                                        R.string.gravatar_qe_about_save_profile_error_message,
                                    ),
                                    withDismissAction = true,
                                    snackbarType = SnackbarType.Error,
                                )
                            }
                        }

                        AboutEditorAction.CloseEditor -> {
                            onClose()
                        }

                        AboutEditorAction.NotifyDismissIgnored -> onDismissIgnored()
                    }
                }
            }
        }
    }

    Surface {
        Box(modifier = Modifier.wrapContentSize()) {
            AboutEditor(
                uiState = uiState,
                onValueChange = { aboutField ->
                    viewModel.onEvent(AboutEditorEvent.OnAboutFieldUpdated(aboutField))
                },
                onSaveClick = { viewModel.onEvent(AboutEditorEvent.OnSaveClicked) },
            )
            QESnackbarHost(
                modifier = Modifier
                    .align(Alignment.BottomStart),
                hostState = snackState,
            )
            DiscardChangesAlertDialog(
                visible = uiState.discardChangesDialogVisible,
                onKeepEditing = {
                    viewModel.onEvent(AboutEditorEvent.OnUnsavedChangesKeepEditingClicked)
                },
                onDiscardClicked = {
                    viewModel.onEvent(AboutEditorEvent.OnUnsavedChangesExitClicked)
                },
            )
        }
    }
}

@Composable
internal fun AboutEditor(
    uiState: AboutEditorUiState,
    onValueChange: (AboutInputField) -> Unit,
    onSaveClick: () -> Unit,
) {
    Surface {
        Column {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()),
            ) {
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier
                                .height(300.dp)
                                .fillMaxWidth(),
                        ) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }

                    else -> {
                        AboutSection(
                            aboutFields = uiState.aboutFields,
                            formEnabled = uiState.formEnabled,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            onValueChange = onValueChange,
                        )
                    }
                }
            }
            QEButton(
                buttonText = stringResource(R.string.gravatar_qe_avatar_alt_text_save_button),
                onClick = onSaveClick,
                enabled = uiState.saveEnabled,
                loading = uiState.savingProfile,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 500)
@Composable
internal fun AboutEditorLoadedPreview() {
    GravatarTheme {
        Box(modifier = Modifier.padding(10.dp)) {
            AboutEditor(
                uiState = AboutEditorUiState(
                    aboutFields = AboutFields(
                        personal = PersonalFields(
                            aboutMe = AboutInputField.Personal.AboutMe(value = "My description"),
                            displayName = AboutInputField.Personal.DisplayName(value = "John Doe"),
                            location = AboutInputField.Personal.Location(value = "San Francisco, CA"),
                            pronunciation = AboutInputField.Personal.Pronunciation(value = "John Doe"),
                            pronouns = AboutInputField.Personal.Pronouns(value = "he/him"),
                        ),
                        professional = ProfessionalFields(
                            company = AboutInputField.Professional.Company(value = "Automattic"),
                            jobTitle = AboutInputField.Professional.JobTitle(value = "Software Engineer"),
                        ),
                    ),
                ),
                onValueChange = { },
                onSaveClick = { },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun AboutEditorLoadingPreview() {
    GravatarTheme {
        Box(modifier = Modifier.padding(10.dp)) {
            AboutEditor(
                uiState = AboutEditorUiState(
                    aboutFields = AboutFields.EMPTY,
                    isLoading = true,
                ),
                onValueChange = { },
                onSaveClick = { },
            )
        }
    }
}
