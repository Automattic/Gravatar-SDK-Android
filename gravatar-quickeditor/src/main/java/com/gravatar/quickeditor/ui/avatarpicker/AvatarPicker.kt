package com.gravatar.quickeditor.ui.avatarpicker

import android.content.Context
import android.content.Intent
import android.util.DisplayMetrics
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gravatar.extensions.defaultProfile
import com.gravatar.quickeditor.R
import com.gravatar.quickeditor.data.repository.IdentityAvatars
import com.gravatar.quickeditor.ui.components.AvatarsSection
import com.gravatar.quickeditor.ui.components.EmailLabel
import com.gravatar.quickeditor.ui.components.ErrorSection
import com.gravatar.quickeditor.ui.components.ProfileCard
import com.gravatar.quickeditor.ui.copperlauncher.CropperLauncher
import com.gravatar.quickeditor.ui.copperlauncher.UCropCropperLauncher
import com.gravatar.quickeditor.ui.editor.AvatarUpdateResult
import com.gravatar.quickeditor.ui.editor.ContentLayout
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorParams
import com.gravatar.quickeditor.ui.editor.bottomsheet.DEFAULT_PAGE_HEIGHT
import com.gravatar.quickeditor.ui.extensions.QESnackbarHost
import com.gravatar.quickeditor.ui.extensions.SnackbarType
import com.gravatar.quickeditor.ui.extensions.showQESnackbar
import com.gravatar.restapi.models.Avatar
import com.gravatar.types.Email
import com.gravatar.ui.GravatarTheme
import com.gravatar.ui.components.ComponentState
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun AvatarPicker(
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    handleExpiredSession: Boolean,
    onAvatarSelected: (AvatarUpdateResult) -> Unit,
    onSessionExpired: () -> Unit,
    viewModel: AvatarPickerViewModel = viewModel(
        factory = AvatarPickerViewModelFactory(gravatarQuickEditorParams, handleExpiredSession),
    ),
    cropperLauncher: CropperLauncher = UCropCropperLauncher(),
) {
    val snackState = remember { SnackbarHostState() }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val uCropLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        it.data?.let { intentData ->
            UCrop.getOutput(intentData)?.let { croppedImageUri ->
                viewModel.onEvent(AvatarPickerEvent.ImageCropped(croppedImageUri))
            }
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Main.immediate) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actions.collect { action ->
                    action.handle(
                        cropperLauncher = cropperLauncher,
                        onAvatarSelected = onAvatarSelected,
                        onSessionExpired = onSessionExpired,
                        snackState = snackState,
                        context = context,
                        uCropLauncher = uCropLauncher,
                        viewModel = viewModel,
                    )
                }
            }
        }
    }

    GravatarTheme {
        Box(modifier = Modifier.wrapContentSize()) {
            AvatarPicker(
                uiState = uiState,
                onEvent = viewModel::onEvent,
            )
            QESnackbarHost(
                modifier = Modifier
                    .align(Alignment.BottomStart),
                hostState = snackState,
            )
        }
    }
}

@Composable
internal fun AvatarPicker(uiState: AvatarPickerUiState, onEvent: (AvatarPickerEvent) -> Unit) {
    val context = LocalContext.current
    var loadingSectionHeight by remember { mutableStateOf(DEFAULT_PAGE_HEIGHT) }
    Surface(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
            .then(
                if (uiState.contentLayout == ContentLayout.Horizontal) {
                    Modifier.verticalScroll(rememberScrollState())
                } else {
                    Modifier
                },
            ),
    ) {
        Column {
            EmailLabel(
                email = uiState.email,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
            )
            ProfileCard(
                profile = uiState.profile,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(24.dp))
            when {
                uiState.isLoading -> Box(
                    modifier = Modifier
                        .height(loadingSectionHeight)
                        .fillMaxWidth(),
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.error != null -> ErrorSection(
                    title = stringResource(id = uiState.error.titleRes),
                    message = stringResource(id = uiState.error.messageRes),
                    buttonText = stringResource(id = uiState.error.buttonTextRes),
                    onButtonClick = { onEvent(uiState.error.event) },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .onSizeChanged { size ->
                            loadingSectionHeight = size.height.pxToDp(context)
                        },
                )

                uiState.avatarsSectionUiState != null ->
                    AvatarsSection(
                        state = uiState.avatarsSectionUiState,
                        onAvatarSelected = { onEvent(AvatarPickerEvent.AvatarSelected(it)) },
                        onLocalImageSelected = { onEvent(AvatarPickerEvent.LocalImageSelected(it)) },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .onSizeChanged { size ->
                                loadingSectionHeight = size.height.pxToDp(context)
                            },
                    )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Suppress("LongParameterList")
private suspend fun AvatarPickerAction.handle(
    cropperLauncher: CropperLauncher,
    onAvatarSelected: (AvatarUpdateResult) -> Unit,
    onSessionExpired: () -> Unit,
    snackState: SnackbarHostState,
    context: Context,
    uCropLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    viewModel: AvatarPickerViewModel,
) {
    when (this) {
        is AvatarPickerAction.AvatarSelected -> {
            onAvatarSelected(AvatarUpdateResult(avatar.imageUrl.toUri()))
            snackState.showQESnackbar(
                message = context.getString(R.string.avatar_selected_confirmation),
                withDismissAction = true,
            )
        }

        is AvatarPickerAction.LaunchImageCropper -> {
            cropperLauncher.launch(uCropLauncher, imageUri, tempFile, context)
        }

        is AvatarPickerAction.AvatarUploadFailed -> {
            val result = snackState.showQESnackbar(
                message = context.getString(R.string.avatar_upload_error),
                actionLabel = context.getString(R.string.avatar_upload_error_action),
                snackbarType = SnackbarType.Error,
                withDismissAction = true,
            )
            when (result) {
                SnackbarResult.Dismissed -> Unit
                SnackbarResult.ActionPerformed -> viewModel.onEvent(AvatarPickerEvent.ImageCropped(imageUri))
            }
        }

        AvatarPickerAction.AvatarSelectionFailed -> {
            snackState.showQESnackbar(
                message = context.getString(R.string.avatar_selection_error),
                withDismissAction = true,
                snackbarType = SnackbarType.Error,
            )
        }

        AvatarPickerAction.InvokeAuthFailed -> onSessionExpired()
    }
}

private fun Int.pxToDp(context: Context): Dp =
    (this / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).dp

private val SectionError.titleRes: Int
    @StringRes get() = when (this) {
        is SectionError.InvalidToken -> R.string.avatar_picker_session_error_title
        SectionError.NoInternetConnection -> R.string.avatar_picker_network_error_title
        SectionError.ServerError,
        SectionError.Unknown,
        -> R.string.avatar_picker_server_error_title
    }

private val SectionError.messageRes: Int
    @StringRes get() = when (this) {
        is SectionError.InvalidToken -> if (showLogin) {
            R.string.avatar_picker_session_error_message
        } else {
            R.string.avatar_picker_session_error_no_login_message
        }

        SectionError.NoInternetConnection -> R.string.avatar_picker_network_error_message
        SectionError.ServerError -> R.string.avatar_picker_server_error_message
        SectionError.Unknown -> R.string.avatar_picker_unknown_error_message
    }

private val SectionError.buttonTextRes: Int
    @StringRes get() = when (this) {
        is SectionError.InvalidToken -> if (showLogin) {
            R.string.avatar_picker_session_error_cta
        } else {
            R.string.avatar_picker_session_error_close_cta
        }

        SectionError.NoInternetConnection,
        SectionError.ServerError,
        SectionError.Unknown,
        -> R.string.avatar_picker_error_retry_cta
    }

private val SectionError.event: AvatarPickerEvent
    get() = when (this) {
        is SectionError.InvalidToken -> AvatarPickerEvent.HandleAuthFailureTapped
        SectionError.ServerError,
        SectionError.Unknown,
        SectionError.NoInternetConnection,
        -> AvatarPickerEvent.Refresh
    }

@Composable
@PreviewLightDark
private fun AvatarPickerPreview() {
    GravatarTheme {
        AvatarPicker(
            uiState = AvatarPickerUiState(
                email = Email("henry.a.wallace@example.com"),
                profile = ComponentState.Loaded(
                    defaultProfile(
                        hash = "tetet",
                        displayName = "Henry Wallace",
                        location = "London, UK",
                    ),
                ),
                identityAvatars = IdentityAvatars(
                    avatars = listOf(
                        Avatar {
                            imageUrl = "/image/url"
                            format = 0
                            imageId = "1"
                            rating = "G"
                            altText = "alt"
                            isCropped = true
                            updatedDate = null
                        },
                    ),
                    selectedAvatarId = "1",
                ),
                contentLayout = ContentLayout.Horizontal,
            ),
            onEvent = { },
        )
    }
}

@Composable
@PreviewLightDark
private fun AvatarPickerLoadingPreview() {
    GravatarTheme {
        AvatarPicker(
            uiState = AvatarPickerUiState(
                email = Email("henry.a.wallace@example.com"),
                profile = ComponentState.Loading,
                isLoading = true,
                identityAvatars = null,
                contentLayout = ContentLayout.Horizontal,
            ),
            onEvent = { },
        )
    }
}

@Composable
@Preview
private fun AvatarPickerErrorPreview() {
    GravatarTheme {
        AvatarPicker(
            uiState = AvatarPickerUiState(
                email = Email("henry.a.wallace@example.com"),
                profile = null,
                isLoading = false,
                identityAvatars = null,
                error = SectionError.ServerError,
                contentLayout = ContentLayout.Horizontal,
            ),
            onEvent = { },
        )
    }
}
