package com.gravatar.quickeditor.ui.avatarpicker

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
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
import com.gravatar.quickeditor.ui.components.ProfileCard
import com.gravatar.quickeditor.ui.copperlauncher.CropperLauncher
import com.gravatar.quickeditor.ui.copperlauncher.UCropCropperLauncher
import com.gravatar.quickeditor.ui.editor.AvatarUpdateResult
import com.gravatar.quickeditor.ui.editor.bottomsheet.DEFAULT_PAGE_HEIGHT
import com.gravatar.restapi.models.Avatar
import com.gravatar.types.Email
import com.gravatar.ui.GravatarTheme
import com.gravatar.ui.components.ComponentState
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

@Composable
internal fun AvatarPicker(
    email: Email,
    onAvatarSelected: (AvatarUpdateResult) -> Unit,
    viewModel: AvatarPickerViewModel = viewModel(factory = AvatarPickerViewModelFactory(email)),
    cropperLauncher: CropperLauncher = UCropCropperLauncher(),
) {
    val snackState = remember { SnackbarHostState() }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val uCropLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        it.data?.let { intentData ->
            UCrop.getOutput(intentData)?.let { croppedImageUri ->
                viewModel.uploadAvatar(croppedImageUri)
            }
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Main.immediate) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actions.collect { action ->
                    when (action) {
                        is AvatarPickerAction.AvatarSelected -> {
                            onAvatarSelected(AvatarUpdateResult(action.avatar.fullUrl.toUri()))
                            snackState.showSnackbar(
                                message = context.getString(R.string.avatar_selected_confirmation),
                                actionLabel = context.getString(R.string.avatar_selected_confirmation_action),
                                duration = SnackbarDuration.Long,
                            )
                        }

                        is AvatarPickerAction.LaunchImageCropper -> {
                            cropperLauncher.launch(uCropLauncher, action.imageUri, action.tempFile, context)
                        }
                    }
                }
            }
        }
    }

    GravatarTheme {
        Box(modifier = Modifier.wrapContentSize()) {
            AvatarPicker(
                uiState = uiState,
                onAvatarSelected = viewModel::selectAvatar,
                onLocalImageSelected = viewModel::localImageSelected,
            )
            SnackbarHost(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp),
                hostState = snackState,
            ) { snackbarData ->
                Snackbar(
                    actionColor = MaterialTheme.colorScheme.inverseOnSurface,
                    snackbarData = snackbarData,
                )
            }
        }
    }
}

@Composable
internal fun AvatarPicker(
    uiState: AvatarPickerUiState,
    onAvatarSelected: (Avatar) -> Unit,
    onLocalImageSelected: (Uri) -> Unit,
) {
    Surface(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
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
                        .height(DEFAULT_PAGE_HEIGHT)
                        .fillMaxWidth(),
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.error -> Text(text = "There was an error loading avatars", textAlign = TextAlign.Center)
                uiState.avatarsSectionUiState != null ->
                    AvatarsSection(
                        uiState.avatarsSectionUiState,
                        onAvatarSelected,
                        onLocalImageSelected,
                        Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                    )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
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
                            updatedDate = Instant.now()
                        },
                    ),
                    selectedAvatarId = "1",
                ),
            ),
            onAvatarSelected = { },
            onLocalImageSelected = { },
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
            ),
            onAvatarSelected = { },
            onLocalImageSelected = { },
        )
    }
}

internal val Avatar.fullUrl: String
    get() = "https://www.gravatar.com$imageUrl?size=200"
