package com.gravatar.quickeditor.ui.abouteditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.gravatar.quickeditor.QuickEditorContainer
import com.gravatar.quickeditor.data.repository.ProfileRepository
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorParams
import com.gravatar.restapi.models.Profile
import com.gravatar.services.GravatarResult
import com.gravatar.types.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class AboutEditorViewModel(
    private val email: Email,
    private val profileRepository: ProfileRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AboutEditorUiState())
    val uiState: StateFlow<AboutEditorUiState> = _uiState.asStateFlow()

    init {
        fetchProfile()
    }

    fun onEvent(aboutEditorEvent: AboutEditorEvent) {
        when (aboutEditorEvent) {
            is AboutEditorEvent.OnAboutFieldUpdated -> updateAboutField(aboutEditorEvent.aboutField)
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
                        it.copy(
                            isLoading = false,
                            aboutFields = result.value.aboutFields,
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

private val Profile.aboutFields: AboutFields
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
