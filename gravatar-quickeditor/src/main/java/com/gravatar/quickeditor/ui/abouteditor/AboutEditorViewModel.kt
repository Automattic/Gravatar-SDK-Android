package com.gravatar.quickeditor.ui.abouteditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.gravatar.quickeditor.QuickEditorContainer
import com.gravatar.quickeditor.data.repository.ProfileRepository
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
            val savedProfile = savedProfile?.aboutFields
            if (savedProfile != null && currentProfile != savedProfile) {
                _uiState.update { currentProfile ->
                    currentProfile.copy(
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
                            aboutFields = profile.aboutFields,
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

    private fun updateAboutField(aboutField: AboutInputField) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    aboutFields = currentState.aboutFields.update(aboutField),
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
                            aboutFields = profile.aboutFields,
                        )
                    }
                }

                is GravatarResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                        )
                    }
                }
            }
        }
    }
}

internal val Profile.aboutFields: AboutFields
    get() {
        return AboutFields(
            personal = PersonalFields(
                displayName = AboutInputField.Personal.DisplayName(value = displayName),
                aboutMe = AboutInputField.Personal.AboutMe(value = description),
                location = AboutInputField.Personal.Location(value = location),
                pronouns = AboutInputField.Personal.Pronouns(value = pronouns),
                pronunciation = AboutInputField.Personal.Pronunciation(value = pronunciation),
            ),
            professional = ProfessionalFields(
                company = AboutInputField.Professional.Company(value = company),
                jobTitle = AboutInputField.Professional.JobTitle(value = jobTitle),
            ),
        )
    }

private val AboutFields.updateProfileRequest: UpdateProfileRequest
    get() {
        return UpdateProfileRequest {
            displayName = personal.displayName.value
            description = personal.aboutMe.value
            pronouns = personal.pronouns.value
            pronunciation = personal.pronunciation.value
            location = personal.location.value
            jobTitle = professional.jobTitle.value
            company = professional.company.value
        }
    }

internal class AboutEditorViewModelFactory(
    private val gravatarQuickEditorParams: GravatarQuickEditorParams,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return AboutEditorViewModel(
            email = gravatarQuickEditorParams.email,
            profileRepository = QuickEditorContainer.getInstance().profileRepository,
        ) as T
    }
}
