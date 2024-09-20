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
import com.gravatar.restapi.models.Profile
import com.gravatar.services.ErrorType
import com.gravatar.services.ProfileService
import com.gravatar.services.Result
import com.gravatar.types.Email
import com.gravatar.ui.components.ComponentState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URI

internal class AvatarPickerViewModel(
    private val email: Email,
    private val handleExpiredSession: Boolean,
    private val avatarPickerContentLayout: AvatarPickerContentLayout,
    private val profileService: ProfileService,
    private val avatarRepository: AvatarRepository,
    private val fileUtils: FileUtils,
) : ViewModel() {
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
                    is Result.Success -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                emailAvatars = currentState.emailAvatars?.copy(selectedAvatarId = avatarId),
                                selectingAvatarId = null,
                                profile = currentState.profile?.copy { copyAvatar(avatar) },
                            )
                        }
                        _actions.send(AvatarPickerAction.AvatarSelected(avatar))
                    }

                    is Result.Failure -> {
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
                currentState.copy(uploadingAvatar = uri, scrollToIndex = 0)
            }
            when (val result = avatarRepository.uploadAvatar(email, uri)) {
                is Result.Success -> {
                    fileUtils.deleteFile(uri)
                    _uiState.update { currentState ->
                        val avatar = result.value
                        currentState.copy(
                            uploadingAvatar = null,
                            emailAvatars = currentState.emailAvatars?.copy(
                                avatars = buildList {
                                    add(avatar)
                                    addAll(currentState.emailAvatars.avatars.filter { it.imageId != avatar.imageId })
                                },
                            ),
                            scrollToIndex = null,
                        )
                    }
                }

                is Result.Failure -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            uploadingAvatar = null,
                            scrollToIndex = null,
                        )
                    }
                    _actions.send(AvatarPickerAction.AvatarUploadFailed(uri))
                }
            }
        }
    }

    private fun fetchProfile() {
        viewModelScope.launch {
            _uiState.update { currentState -> currentState.copy(profile = ComponentState.Loading) }
            when (val result = profileService.retrieveCatching(email)) {
                is Result.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(profile = ComponentState.Loaded(result.value))
                    }
                }

                is Result.Failure -> {
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
            is Result.Success -> {
                _uiState.update { currentState ->
                    val emailAvatars = result.value
                    currentState.copy(
                        emailAvatars = emailAvatars,
                        scrollToIndex = if (scrollToSelected && emailAvatars.avatars.isNotEmpty()) {
                            emailAvatars.avatars.indexOfFirst {
                                it.imageId == emailAvatars.selectedAvatarId
                            }.coerceAtLeast(0)
                        } else {
                            null
                        },
                        isLoading = false,
                        error = null,
                    )
                }
            }

            is Result.Failure -> {
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
                ErrorType.SERVER -> SectionError.ServerError
                ErrorType.NETWORK -> SectionError.NoInternetConnection
                ErrorType.UNAUTHORIZED -> SectionError.InvalidToken(handleExpiredSession)
                ErrorType.NOT_FOUND,
                ErrorType.RATE_LIMIT_EXCEEDED,
                ErrorType.TIMEOUT,
                ErrorType.UNKNOWN,
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

internal fun Profile.copyAvatar(avatar: Avatar): Profile {
    return Profile {
        hash = this@copyAvatar.hash
        displayName = this@copyAvatar.displayName
        profileUrl = this@copyAvatar.profileUrl
        avatarUrl = URI.create(avatar.imageUrl)
        avatarAltText = avatar.altText
        location = this@copyAvatar.location
        description = this@copyAvatar.description
        jobTitle = this@copyAvatar.jobTitle
        description = this@copyAvatar.description
        jobTitle = this@copyAvatar.jobTitle
        company = this@copyAvatar.company
        verifiedAccounts = this@copyAvatar.verifiedAccounts
        pronunciation = this@copyAvatar.pronunciation
        pronouns = this@copyAvatar.pronouns
        timezone = this@copyAvatar.timezone
        languages = this@copyAvatar.languages
        firstName = this@copyAvatar.firstName
        lastName = this@copyAvatar.lastName
        isOrganization = this@copyAvatar.isOrganization
        links = this@copyAvatar.links
        interests = this@copyAvatar.interests
        payments = this@copyAvatar.payments
        contactInfo = this@copyAvatar.contactInfo
        gallery = this@copyAvatar.gallery
        numberVerifiedAccounts = this@copyAvatar.numberVerifiedAccounts
        lastProfileEdit = this@copyAvatar.lastProfileEdit
        registrationDate = this@copyAvatar.registrationDate
    }
}

private fun <T> ComponentState<T>.copy(transform: T.() -> T): ComponentState<T> = when (this) {
    is ComponentState.Loaded -> ComponentState.Loaded(loadedValue.transform())
    is ComponentState.Loading -> this
    is ComponentState.Empty -> this
}
