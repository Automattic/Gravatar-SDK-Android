package com.gravatar.quickeditor.ui.abouteditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.gravatar.quickeditor.QuickEditorContainer
import com.gravatar.quickeditor.data.repository.ProfileRepository
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
                            aboutFields = profile.aboutFields(visibleAboutFields),
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

internal fun Profile.aboutFields(visibleAboutFields: Set<AboutInputField>): AboutFields {
    return AboutFields(
        personal = PersonalFields(
            displayName = AboutEditorField.Personal.DisplayName(
                value = displayName,
                visible = visibleAboutFields.contains(AboutInputField.DisplayName),
            ),
            aboutMe = AboutEditorField.Personal.AboutMe(
                value = description,
                visible = visibleAboutFields.contains(AboutInputField.AboutMe),
            ),
            location = AboutEditorField.Personal.Location(
                value = location,
                visible = visibleAboutFields.contains(AboutInputField.Location),
            ),
            pronouns = AboutEditorField.Personal.Pronouns(
                value = pronouns,
                visible = visibleAboutFields.contains(AboutInputField.Pronouns),
            ),
            pronunciation = AboutEditorField.Personal.Pronunciation(
                value = pronunciation,
                visible = visibleAboutFields.contains(AboutInputField.Pronunciation),
            ),
        ),
        professional = ProfessionalFields(
            company = AboutEditorField.Professional.Company(
                value = company,
                visible = visibleAboutFields.contains(AboutInputField.Company),
            ),
            jobTitle = AboutEditorField.Professional.JobTitle(
                value = jobTitle,
                visible = visibleAboutFields.contains(AboutInputField.JobTitle),
            ),
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
            visibleAboutFields = gravatarQuickEditorParams.scopeOption.aboutFields,
        ) as T
    }
}
