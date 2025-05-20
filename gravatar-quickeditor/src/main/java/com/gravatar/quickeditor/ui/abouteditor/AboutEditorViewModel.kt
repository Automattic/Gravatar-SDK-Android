package com.gravatar.quickeditor.ui.abouteditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.gravatar.quickeditor.QuickEditorContainer
import com.gravatar.quickeditor.data.repository.ProfileRepository
import com.gravatar.quickeditor.ui.avatarpicker.asSectionError
import com.gravatar.quickeditor.ui.editor.AboutInputField
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorParams
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.UpdateProfileRequest
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

    private var savedProfile: Profile? = null

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
            val currentProfile = uiState.value.aboutFields
            val savedProfile = savedProfile?.aboutFields(visibleAboutFields)
            if (savedProfile != null && currentProfile != savedProfile) {
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
                    savedProfile = profile
                    _uiState.update { currentState ->
                        currentState.copy(
                            savingProfile = false,
                            aboutFields = profile.aboutFields(visibleAboutFields),
                        )
                    }
                    _actions.send(
                        AboutEditorAction.ProfileUpdated(profile),
                    )
                }

                is GravatarResult.Failure -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            savingProfile = false,
                        )
                    }
                    _actions.send(AboutEditorAction.ProfileUpdateFailed)
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
                        savedProfile = profile
                        it.copy(
                            isLoading = false,
                            aboutFields = profile.aboutFields(visibleAboutFields),
                            error = null,
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
                    else -> ""
                },
                maxLines = when (it) {
                    AboutInputField.AboutMe -> 3
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
