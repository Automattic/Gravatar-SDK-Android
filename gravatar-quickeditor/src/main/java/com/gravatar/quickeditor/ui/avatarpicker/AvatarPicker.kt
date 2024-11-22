package com.gravatar.quickeditor.ui.avatarpicker

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.DisplayMetrics
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gravatar.extensions.defaultProfile
import com.gravatar.quickeditor.R
import com.gravatar.quickeditor.data.repository.EmailAvatars
import com.gravatar.quickeditor.ui.components.AvatarOption
import com.gravatar.quickeditor.ui.components.AvatarsSection
import com.gravatar.quickeditor.ui.components.DownloadManagerDisabledAlertDialog
import com.gravatar.quickeditor.ui.components.EmailLabel
import com.gravatar.quickeditor.ui.components.ErrorSection
import com.gravatar.quickeditor.ui.components.FailedAvatarUploadAlertDialog
import com.gravatar.quickeditor.ui.components.PermissionRationaleDialog
import com.gravatar.quickeditor.ui.components.ProfileCard
import com.gravatar.quickeditor.ui.cropperlauncher.CropperLauncher
import com.gravatar.quickeditor.ui.cropperlauncher.UCropCropperLauncher
import com.gravatar.quickeditor.ui.editor.AvatarPickerContentLayout
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorParams
import com.gravatar.quickeditor.ui.editor.bottomsheet.DEFAULT_PAGE_HEIGHT
import com.gravatar.quickeditor.ui.extensions.QESnackbarHost
import com.gravatar.quickeditor.ui.extensions.QESnackbarResult
import com.gravatar.quickeditor.ui.extensions.SnackbarType
import com.gravatar.quickeditor.ui.extensions.showQESnackbar
import com.gravatar.quickeditor.ui.openAppPermissionSettings
import com.gravatar.quickeditor.ui.withPermission
import com.gravatar.restapi.models.Avatar
import com.gravatar.types.Email
import com.gravatar.ui.GravatarTheme
import com.gravatar.ui.components.ComponentState
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URI

@Composable
internal fun AvatarPicker(
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    handleExpiredSession: Boolean,
    onAvatarSelected: () -> Unit,
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
    val scope = rememberCoroutineScope()

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
                        viewModel = viewModel,
                        cropperLauncher = cropperLauncher,
                        onAvatarSelected = onAvatarSelected,
                        onSessionExpired = onSessionExpired,
                        snackState = snackState,
                        context = context,
                        uCropLauncher = uCropLauncher,
                        scope = scope,
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
    var storagePermissionRationaleDialogVisible by rememberSaveable { mutableStateOf(false) }
    var avatarToDownload: Avatar? by remember { mutableStateOf(null) }

    val writeExternalStoragePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            avatarToDownload?.let { onEvent(AvatarPickerEvent.DownloadAvatarTapped(it)) }
        } else {
            storagePermissionRationaleDialogVisible = true
        }
        avatarToDownload = null
    }

    val permissionAwareDownloadImageCallback: (Avatar) -> Unit = { avatar ->
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            context.withPermission(
                permission = Manifest.permission.WRITE_EXTERNAL_STORAGE,
                onRequestPermission = {
                    avatarToDownload = avatar
                    writeExternalStoragePermissionLauncher.launch(it)
                },
                onShowRationale = { storagePermissionRationaleDialogVisible = true },
                grantedCallback = {
                    onEvent(AvatarPickerEvent.DownloadAvatarTapped(avatar))
                },
            )
        } else {
            onEvent(AvatarPickerEvent.DownloadAvatarTapped(avatar))
        }
    }

    Surface(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
            .then(
                if (uiState.avatarPickerContentLayout == AvatarPickerContentLayout.Horizontal) {
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
            key(uiState.avatarUpdates) {
                ProfileCard(
                    profile = uiState.profile,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            val sectionModifier = Modifier.padding(top = 24.dp, bottom = 10.dp)
            when {
                uiState.isLoading -> Box(
                    modifier = sectionModifier
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
                    modifier = sectionModifier
                        .padding(horizontal = 16.dp)
                        .onSizeChanged { size ->
                            loadingSectionHeight = size.height.pxToDp(context)
                        },
                )

                uiState.avatarsSectionUiState != null ->
                    AvatarsSection(
                        state = uiState.avatarsSectionUiState,
                        onAvatarSelected = { avatarUi ->
                            when (avatarUi) {
                                is AvatarUi.Local -> onEvent(AvatarPickerEvent.FailedAvatarTapped(avatarUi.uri))
                                is AvatarUi.Uploaded -> onEvent(AvatarPickerEvent.AvatarSelected(avatarUi.avatar))
                            }
                        },
                        onAvatarOptionClicked = { avatar, avatarOption ->
                            when (avatarOption) {
                                AvatarOption.ALT_TEXT -> Unit
                                AvatarOption.DELETE -> {
                                    onEvent(AvatarPickerEvent.AvatarDeleteSelected(avatar))
                                }

                                AvatarOption.DOWNLOAD_IMAGE -> {
                                    permissionAwareDownloadImageCallback(avatar)
                                }
                            }
                        },
                        onLocalImageSelected = { onEvent(AvatarPickerEvent.LocalImageSelected(it)) },
                        modifier = sectionModifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .onSizeChanged { size ->
                                loadingSectionHeight = size.height.pxToDp(context)
                            },
                    )
            }
        }
        FailedAvatarUploadAlertDialog(
            avatarUploadFailure = uiState.failedUploadDialog,
            onRemoveUploadClicked = { onEvent(AvatarPickerEvent.FailedAvatarDismissed(it)) },
            onRetryClicked = { onEvent(AvatarPickerEvent.ImageCropped(it)) },
            onDismiss = { onEvent(AvatarPickerEvent.FailedAvatarDialogDismissed) },
        )
        DownloadManagerDisabledAlertDialog(
            isVisible = uiState.downloadManagerDisabled,
            onDismiss = { onEvent(AvatarPickerEvent.DownloadManagerDisabledDialogDismissed) },
            onConfirm = {
                onEvent(AvatarPickerEvent.DownloadManagerDisabledDialogDismissed)
                openDownloadManagerSettings(context)
            },
        )
        PermissionRationaleDialog(
            isVisible = storagePermissionRationaleDialogVisible,
            message = stringResource(R.string.gravatar_qe_write_external_storage_permission_rationale_message),
            onConfirmation = {
                storagePermissionRationaleDialogVisible = false
                context.openAppPermissionSettings()
            },
            onDismiss = { storagePermissionRationaleDialogVisible = false },
        )
    }
}

@Suppress("SwallowedException")
private fun openDownloadManagerSettings(context: Context) {
    try {
        // Open the specific App Info page:
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.setData(Uri.parse("package:com.android.providers.downloads"))
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // Open the generic Apps page:
        val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
        context.startActivity(intent)
    }
}

@Suppress("LongParameterList")
private fun AvatarPickerAction.handle(
    viewModel: AvatarPickerViewModel,
    cropperLauncher: CropperLauncher,
    onAvatarSelected: () -> Unit,
    onSessionExpired: () -> Unit,
    snackState: SnackbarHostState,
    context: Context,
    uCropLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    scope: CoroutineScope,
) {
    when (this) {
        is AvatarPickerAction.AvatarSelected -> {
            onAvatarSelected()
            scope.launch {
                snackState.showQESnackbar(
                    message = context.getString(R.string.gravatar_qe_avatar_selected_confirmation_v2),
                    withDismissAction = true,
                )
            }
        }

        is AvatarPickerAction.LaunchImageCropper -> {
            cropperLauncher.launch(uCropLauncher, imageUri, tempFile, context)
        }

        AvatarPickerAction.AvatarSelectionFailed -> {
            scope.launch {
                snackState.showQESnackbar(
                    message = context.getString(R.string.gravatar_qe_avatar_selection_error),
                    withDismissAction = true,
                    snackbarType = SnackbarType.Error,
                )
            }
        }

        AvatarPickerAction.InvokeAuthFailed -> onSessionExpired()

        is AvatarPickerAction.AvatarDeletionFailed -> {
            scope.launch {
                if (snackState.showQESnackbar(
                        message = context.getString(R.string.gravatar_qe_avatar_delete_avatar_error),
                        actionLabel = context.getString(R.string.gravatar_qe_avatar_picker_error_retry_cta),
                        withDismissAction = true,
                        snackbarType = SnackbarType.Error,
                        duration = SnackbarDuration.Long,
                    ) == QESnackbarResult.ActionPerformed
                ) {
                    viewModel.onEvent(AvatarPickerEvent.AvatarDeleteSelected(avatar))
                }
            }
        }

        AvatarPickerAction.AvatarDownloadStarted -> {
            scope.launch {
                snackState.showQESnackbar(
                    message = context.getString(R.string.gravatar_qe_image_download_queued),
                    withDismissAction = true,
                    snackbarType = SnackbarType.Info,
                )
            }
        }

        AvatarPickerAction.DownloadManagerNotAvailable -> {
            scope.launch {
                snackState.showQESnackbar(
                    message = context.getString(R.string.gravatar_qe_download_manager_not_available),
                    withDismissAction = true,
                    snackbarType = SnackbarType.Error,
                )
            }
        }
    }
}

private fun Int.pxToDp(context: Context): Dp =
    (this / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).dp

private val SectionError.titleRes: Int
    @StringRes get() = when (this) {
        is SectionError.InvalidToken -> R.string.gravatar_qe_avatar_picker_session_error_title
        SectionError.NoInternetConnection -> R.string.gravatar_qe_avatar_picker_network_error_title
        SectionError.ServerError,
        SectionError.Unknown,
        -> R.string.gravatar_qe_avatar_picker_server_error_title
    }

private val SectionError.messageRes: Int
    @StringRes get() = when (this) {
        is SectionError.InvalidToken -> if (showLogin) {
            R.string.gravatar_qe_avatar_picker_session_error_message
        } else {
            R.string.gravatar_qe_avatar_picker_session_error_no_login_message
        }

        SectionError.NoInternetConnection -> R.string.gravatar_qe_avatar_picker_network_error_message
        SectionError.ServerError -> R.string.gravatar_qe_avatar_picker_server_error_message
        SectionError.Unknown -> R.string.gravatar_qe_avatar_picker_unknown_error_message
    }

private val SectionError.buttonTextRes: Int
    @StringRes get() = when (this) {
        is SectionError.InvalidToken -> if (showLogin) {
            R.string.gravatar_qe_avatar_picker_session_error_cta
        } else {
            R.string.gravatar_qe_avatar_picker_session_error_close_cta
        }

        SectionError.NoInternetConnection,
        SectionError.ServerError,
        SectionError.Unknown,
        -> R.string.gravatar_qe_avatar_picker_error_retry_cta
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
                emailAvatars = EmailAvatars(
                    avatars = listOf(
                        Avatar {
                            imageUrl = URI.create("https://gravatar.com/avatar/test")
                            imageId = "1"
                            rating = Avatar.Rating.G
                            altText = "alt"
                            updatedDate = ""
                        },
                    ),
                    selectedAvatarId = "1",
                ),
                avatarPickerContentLayout = AvatarPickerContentLayout.Horizontal,
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
                avatarPickerContentLayout = AvatarPickerContentLayout.Horizontal,
                emailAvatars = null,
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
                emailAvatars = null,
                error = SectionError.ServerError,
                avatarPickerContentLayout = AvatarPickerContentLayout.Horizontal,
            ),
            onEvent = { },
        )
    }
}
