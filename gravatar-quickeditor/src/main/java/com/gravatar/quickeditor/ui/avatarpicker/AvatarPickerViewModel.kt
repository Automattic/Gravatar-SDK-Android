package com.gravatar.quickeditor.ui.avatarpicker

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.gravatar.quickeditor.QuickEditorContainer
import com.gravatar.quickeditor.data.FileUtils
import com.gravatar.quickeditor.data.models.QuickEditorError
import com.gravatar.quickeditor.data.repository.AvatarRepository
import com.gravatar.quickeditor.ui.editor.AvatarPickerContentLayout
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorParams
import com.gravatar.restapi.models.Avatar
import com.gravatar.services.ErrorType
import com.gravatar.services.GravatarResult
import com.gravatar.services.ProfileService
import com.gravatar.types.Email
import com.gravatar.ui.components.ComponentState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class AvatarPickerViewModel(
    private val email: Email,
    private val handleExpiredSession: Boolean,
    private val avatarPickerContentLayout: AvatarPickerContentLayout,
    private val profileService: ProfileService,
    private val avatarRepository: AvatarRepository,
    private val fileUtils: FileUtils,
) : ViewModel() {
    private companion object {
        const val AVATAR_SWITCH_DELAY = 800L
    }

    private val _uiState =
        MutableStateFlow(AvatarPickerUiState(email = email, avatarPickerContentLayout = avatarPickerContentLayout))
    val uiState: StateFlow<AvatarPickerUiState> = _uiState.asStateFlow()
    private val _actions = Channel<AvatarPickerAction>(Channel.BUFFERED)
    val actions = _actions.receiveAsFlow()

    init {
        refresh()
    }

    fun onEvent(event: AvatarPickerEvent) {
        when (event) {
            is AvatarPickerEvent.LocalImageSelected -> localImageSelected(event.uri)
            AvatarPickerEvent.Refresh -> refresh()
            is AvatarPickerEvent.AvatarSelected -> selectAvatar(event.avatar)
            is AvatarPickerEvent.ImageCropped -> uploadAvatar(event.uri)
            AvatarPickerEvent.HandleAuthFailureTapped -> {
                viewModelScope.launch {
                    _actions.send(AvatarPickerAction.InvokeAuthFailed)
                }
            }

            AvatarPickerEvent.FailedAvatarDialogDismissed -> dismissFailedUploadDialog()
            is AvatarPickerEvent.FailedAvatarTapped -> showFailedUploadDialog(event.uri)
            is AvatarPickerEvent.FailedAvatarDismissed -> removedFailedUpload(event.uri)
        }
    }

    private fun showFailedUploadDialog(uri: Uri) {
        _uiState.update { currentState ->
            currentState.copy(failedUploadDialog = currentState.failedUploads.firstOrNull { it.uri == uri })
        }
    }

    private fun dismissFailedUploadDialog() {
        _uiState.update { currentState ->
            currentState.copy(failedUploadDialog = null)
        }
    }

    private fun removedFailedUpload(uri: Uri) {
        _uiState.update { currentState ->
            currentState.copy(
                failedUploads = currentState.failedUploads.filter { it.uri != uri }.toSet(),
                failedUploadDialog = null,
            )
        }
    }

    private fun refresh() {
        fetchAvatars()
        if (uiState.value.profile !is ComponentState.Loaded) {
            fetchProfile()
        }
    }

    private fun selectAvatar(avatar: Avatar) {
        viewModelScope.launch {
            val avatarId = avatar.imageId
            if (_uiState.value.emailAvatars?.selectedAvatarId != avatarId) {
                _uiState.update { currentState ->
                    currentState.copy(selectingAvatarId = avatarId)
                }
                when (avatarRepository.selectAvatar(email, avatarId)) {
                    is GravatarResult.Success -> {
                        // Delay to wait until the server has updated the selected avatar before updating the UI
                        // Hopefully, we can remove this delay soon
                        delay(AVATAR_SWITCH_DELAY)
                        _uiState.update { currentState ->
                            currentState.copy(
                                emailAvatars = currentState.emailAvatars?.copy(selectedAvatarId = avatarId),
                                selectingAvatarId = null,
                                avatarUpdates = currentState.avatarUpdates.inc(),
                            )
                        }
                        _actions.send(AvatarPickerAction.AvatarSelected)
                    }

                    is GravatarResult.Failure -> {
                        _uiState.update { currentState ->
                            currentState.copy(selectingAvatarId = null)
                        }
                        _actions.send(AvatarPickerAction.AvatarSelectionFailed)
                    }
                }
            }
        }
    }

    private fun localImageSelected(imageUri: Uri) {
        viewModelScope.launch {
            _actions.send(AvatarPickerAction.LaunchImageCropper(imageUri, fileUtils.createCroppedAvatarFile()))
        }
    }

    private fun uploadAvatar(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    uploadingAvatar = uri,
                    failedUploads = currentState.failedUploads.filter { it.uri != uri }.toSet(),
                    scrollToIndex = 0,
                    failedUploadDialog = null,
                )
            }
            when (val result = avatarRepository.uploadAvatar(email, uri)) {
                is GravatarResult.Success -> {
                    fileUtils.deleteFile(uri)
                    val isAutoSelected = _uiState.value.emailAvatars?.selectedAvatarId == null
                    if (isAutoSelected) {
                        fetchAvatars(showLoading = false, scrollToSelected = true)
                        _uiState.update { currentState ->
                            currentState.copy(
                                uploadingAvatar = null,
                                avatarUpdates = currentState.avatarUpdates.inc(),
                            )
                        }
                        if (_uiState.value.emailAvatars?.selectedAvatarId != null) {
                            _actions.send(AvatarPickerAction.AvatarSelected)
                        }
                    } else {
                        _uiState.update { currentState ->
                            val avatar = result.value
                            currentState.copy(
                                uploadingAvatar = null,
                                emailAvatars = currentState.emailAvatars?.copy(
                                    avatars = buildList {
                                        add(avatar)
                                        addAll(
                                            currentState.emailAvatars.avatars.filter { it.imageId != avatar.imageId },
                                        )
                                    },
                                ),
                                scrollToIndex = null,
                            )
                        }
                    }
                }

                is GravatarResult.Failure -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            uploadingAvatar = null,
                            scrollToIndex = null,
                            failedUploads = currentState.failedUploads + AvatarUploadFailure(
                                uri,
                                error = ((result.error as? QuickEditorError.Request)?.type as? ErrorType.InvalidRequest)
                                    ?.error?.error,
                            ),
                        )
                    }
                }
            }
        }
    }

    private fun fetchProfile() {
        viewModelScope.launch {
            _uiState.update { currentState -> currentState.copy(profile = ComponentState.Loading) }
            when (val result = profileService.retrieveCatching(email)) {
                is GravatarResult.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(profile = ComponentState.Loaded(result.value))
                    }
                }

                is GravatarResult.Failure -> {
                    _uiState.update { currentState ->
                        currentState.copy(profile = null)
                    }
                }
            }
        }
    }

    private fun fetchAvatars() {
        viewModelScope.launch {
            fetchAvatars(
                showLoading = true,
                scrollToSelected = avatarPickerContentLayout == AvatarPickerContentLayout.Horizontal,
            )
        }
    }

    private suspend fun fetchAvatars(showLoading: Boolean = true, scrollToSelected: Boolean = true) {
        if (showLoading) {
            _uiState.update { currentState -> currentState.copy(isLoading = true) }
        }
        when (val result = avatarRepository.getAvatars(email)) {
            is GravatarResult.Success -> {
                _uiState.update { currentState ->
                    val emailAvatars = result.value
                    currentState.copy(
                        emailAvatars = emailAvatars,
                        scrollToIndex = if (scrollToSelected && emailAvatars.avatars.isNotEmpty()) {
                            emailAvatars.avatars.indexOfFirstOrNull {
                                it.imageId == emailAvatars.selectedAvatarId
                            }
                        } else {
                            null
                        },
                        isLoading = false,
                        error = null,
                    )
                }
            }

            is GravatarResult.Failure -> {
                _uiState.update { currentState ->
                    currentState.copy(emailAvatars = null, isLoading = false, error = result.error.asSectionError)
                }
            }
        }
    }

    private val QuickEditorError.asSectionError: SectionError
        get() = when (this) {
            QuickEditorError.TokenNotFound -> SectionError.InvalidToken(handleExpiredSession)
            QuickEditorError.Unknown -> SectionError.Unknown
            is QuickEditorError.Request -> when (type) {
                ErrorType.Server -> SectionError.ServerError
                ErrorType.Network -> SectionError.NoInternetConnection
                ErrorType.Unauthorized -> SectionError.InvalidToken(handleExpiredSession)
                ErrorType.NotFound,
                ErrorType.RateLimitExceeded,
                ErrorType.Timeout,
                is ErrorType.Unknown,
                is ErrorType.InvalidRequest,
                -> SectionError.Unknown
            }
        }
}

internal class AvatarPickerViewModelFactory(
    private val gravatarQuickEditorParams: GravatarQuickEditorParams,
    private val handleExpiredSession: Boolean,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return AvatarPickerViewModel(
            handleExpiredSession = handleExpiredSession,
            email = gravatarQuickEditorParams.email,
            avatarPickerContentLayout = gravatarQuickEditorParams.avatarPickerContentLayout,
            profileService = QuickEditorContainer.getInstance().profileService,
            avatarRepository = QuickEditorContainer.getInstance().avatarRepository,
            fileUtils = QuickEditorContainer.getInstance().fileUtils,
        ) as T
    }
}

private inline fun <T> List<T>.indexOfFirstOrNull(predicate: (T) -> Boolean): Int? {
    val index = indexOfFirst { predicate(it) }
    return if (index == -1) null else index
}
