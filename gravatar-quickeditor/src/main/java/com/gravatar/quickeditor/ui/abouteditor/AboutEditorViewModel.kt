package com.gravatar.quickeditor.ui.abouteditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.gravatar.quickeditor.QuickEditorContainer
import com.gravatar.quickeditor.data.models.QuickEditorError
import com.gravatar.quickeditor.data.repository.ProfileRepository
import com.gravatar.quickeditor.ui.avatarpicker.SectionError
import com.gravatar.quickeditor.ui.avatarpicker.asSectionError
import com.gravatar.quickeditor.ui.editor.AboutInputField
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorParams
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.UpdateProfileRequest
import com.gravatar.services.ErrorType
import com.gravatar.services.GravatarResult
import com.gravatar.types.Email
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class AboutEditorViewModel(
    private val email: Email,
    private val handleExpiredSession: Boolean,
    private val visibleAboutFields: Set<AboutInputField>,
    private val profileRepository: ProfileRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AboutEditorUiState())
    val uiState: StateFlow<AboutEditorUiState> = _uiState.asStateFlow()

    private val _actions = Channel<AboutEditorAction>(Channel.BUFFERED)
    val actions = _actions.receiveAsFlow()

    init {
        fetchProfile()
    }

    fun onEvent(aboutEditorEvent: AboutEditorEvent) {
        when (aboutEditorEvent) {
            is AboutEditorEvent.OnAboutFieldUpdated -> updateAboutField(aboutEditorEvent.aboutField)
            AboutEditorEvent.OnSaveClicked -> saveProfile()
            AboutEditorEvent.OnDoneClicked -> checkForUnsavedChanges()
            AboutEditorEvent.OnUnsavedChangesKeepEditingClicked -> dismissUnsavedChangesDialog()
            AboutEditorEvent.OnUnsavedChangesExitClicked -> discardUnsavedChangesDialog()
            AboutEditorEvent.HandleAuthFailureTapped -> {
                viewModelScope.launch {
                    _actions.send(AboutEditorAction.InvokeAuthFailed)
                }
            }

            AboutEditorEvent.Refresh -> fetchProfile()
            is AboutEditorEvent.OnCompactWindowEnabled -> _uiState.update { currentState ->
                currentState.copy(compactWindow = aboutEditorEvent.enabled)
            }
        }
    }

    private fun discardUnsavedChangesDialog() {
        viewModelScope.launch {
            _actions.send(AboutEditorAction.CloseEditor)
        }
        _uiState.update { currentState ->
            currentState.copy(discardChangesDialogVisible = false)
        }
    }

    private fun dismissUnsavedChangesDialog() {
        viewModelScope.launch {
            _actions.send(AboutEditorAction.NotifyDismissIgnored)
        }
        _uiState.update { currentState ->
            currentState.copy(discardChangesDialogVisible = false)
        }
    }

    private fun checkForUnsavedChanges() {
        viewModelScope.launch {
            if (uiState.value.unsavedChanges) {
                _uiState.update { currentState ->
                    currentState.copy(
                        discardChangesDialogVisible = true,
                    )
                }
            } else {
                _actions.send(AboutEditorAction.CloseEditor)
            }
        }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(savingProfile = true)
            }
            val updateProfileRequest = uiState.value.aboutFields.updateProfileRequest
            when (val result = profileRepository.updateProfile(email, updateProfileRequest)) {
                is GravatarResult.Success -> {
                    val profile = result.value
                    val aboutFields = profile.aboutFields(visibleAboutFields)
                    _uiState.update { currentState ->
                        currentState.copy(
                            savingProfile = false,
                            aboutFields = aboutFields,
                            savedAboutFields = aboutFields,
                        )
                    }
                    _actions.send(
                        AboutEditorAction.ProfileUpdated(profile),
                    )
                }

                is GravatarResult.Failure -> {
                    var sectionError: SectionError? = null
                    if ((result.error as? QuickEditorError.Request)?.type is ErrorType.Unauthorized) {
                        sectionError = SectionError.InvalidToken(showLogin = handleExpiredSession)
                    } else {
                        _actions.send(AboutEditorAction.ProfileUpdateFailed)
                    }
                    _uiState.update { currentState ->
                        currentState.copy(
                            savingProfile = false,
                            error = sectionError,
                        )
                    }
                }
            }
        }
    }

    private fun updateAboutField(aboutField: AboutEditorField) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    aboutFields = currentState.aboutFields.map {
                        if (it.type == aboutField.type) {
                            it.copy(value = aboutField.value)
                        } else {
                            it
                        }
                    }.toSet(),
                )
            }
        }
    }

    private fun fetchProfile() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                )
            }
            when (val result = profileRepository.getProfile(email)) {
                is GravatarResult.Success -> {
                    _uiState.update {
                        val profile = result.value
                        val aboutFields = profile.aboutFields(visibleAboutFields)
                        it.copy(
                            isLoading = false,
                            aboutFields = aboutFields,
                            error = null,
                            savedAboutFields = aboutFields,
                        )
                    }
                }

                is GravatarResult.Failure -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            error = result.error.asSectionError(handleExpiredSession),
                        )
                    }
                }
            }
        }
    }
}

internal fun Profile.aboutFields(visibleAboutFields: Set<AboutInputField>): Set<AboutEditorField> {
    return visibleAboutFields
        .map {
            AboutEditorField(
                type = it,
                value = when (it) {
                    AboutInputField.DisplayName -> displayName
                    AboutInputField.AboutMe -> description
                    AboutInputField.Pronouns -> pronouns
                    AboutInputField.Pronunciation -> pronunciation
                    AboutInputField.Location -> location
                    AboutInputField.JobTitle -> jobTitle
                    AboutInputField.Company -> company
                    AboutInputField.FirstName -> firstName.orEmpty()
                    AboutInputField.LastName -> lastName.orEmpty()
                    else -> ""
                },
                maxLines = when (it) {
                    AboutInputField.AboutMe -> 4
                    else -> 1
                },
            )
        }
        .sortedBy { it.type.order }
        .toSet()
}

private val Set<AboutEditorField>.updateProfileRequest: UpdateProfileRequest
    get() {
        return UpdateProfileRequest {
            firstName = this@updateProfileRequest.find { it.type == AboutInputField.FirstName }?.value
            lastName = this@updateProfileRequest.find { it.type == AboutInputField.LastName }?.value
            displayName = this@updateProfileRequest.find { it.type == AboutInputField.DisplayName }?.value
            description = this@updateProfileRequest.find { it.type == AboutInputField.AboutMe }?.value
            pronouns = this@updateProfileRequest.find { it.type == AboutInputField.Pronouns }?.value
            pronunciation = this@updateProfileRequest.find { it.type == AboutInputField.Pronunciation }?.value
            location = this@updateProfileRequest.find { it.type == AboutInputField.Location }?.value
            jobTitle = this@updateProfileRequest.find { it.type == AboutInputField.JobTitle }?.value
            company = this@updateProfileRequest.find { it.type == AboutInputField.Company }?.value
        }
    }

internal class AboutEditorViewModelFactory(
    private val gravatarQuickEditorParams: GravatarQuickEditorParams,
    private val handleExpiredSession: Boolean,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return AboutEditorViewModel(
            email = gravatarQuickEditorParams.email,
            profileRepository = QuickEditorContainer.getInstance().profileRepository,
            visibleAboutFields = gravatarQuickEditorParams.scopeOption.aboutFields,
            handleExpiredSession = handleExpiredSession,
        ) as T
    }
}
