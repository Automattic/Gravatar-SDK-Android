package com.gravatar.quickeditor.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.gravatar.quickeditor.QuickEditorContainer
import com.gravatar.quickeditor.data.repository.ProfileRepository
import com.gravatar.quickeditor.ui.time.Clock
import com.gravatar.quickeditor.ui.time.SystemClock
import com.gravatar.services.GravatarResult
import com.gravatar.types.Email
import com.gravatar.ui.components.ComponentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class QuickEditorViewModel(
    private val email: Email,
    private val profileRepository: ProfileRepository,
    private val clock: Clock,
    initialPage: QuickEditorPage,
    navigationEnabled: Boolean,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        QuickEditorUiState(
            email = email,
            page = initialPage,
            pageNavigationEnabled = navigationEnabled,
            avatarCacheBuster = clock.getTimeMillis(),
        ),
    )
    val uiState: StateFlow<QuickEditorUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun onEvent(event: QuickEditorEvent) {
        when (event) {
            QuickEditorEvent.Refresh -> refresh()
            QuickEditorEvent.UpdateAvatarCache -> _uiState.update { currentState ->
                currentState.copy(avatarCacheBuster = clock.getTimeMillis())
            }

            is QuickEditorEvent.OnProfileUpdated -> _uiState.update { currentState ->
                currentState.copy(profile = ComponentState.Loaded(event.profile))
            }

            QuickEditorEvent.OnEditAboutClicked -> navigateToPage(QuickEditorPage.ABOUT_EDITOR)
            QuickEditorEvent.OnEditAvatarClicked -> navigateToPage(QuickEditorPage.AVATAR_PICKER)
        }
    }

    private fun navigateToPage(page: QuickEditorPage) {
        if (uiState.value.pageNavigationEnabled) {
            _uiState.update { currentState ->
                currentState.copy(page = page)
            }
        }
    }

    private fun refresh() {
        if (uiState.value.profile !is ComponentState.Loaded) {
            fetchProfile()
        }
    }

    private fun fetchProfile() {
        viewModelScope.launch {
            _uiState.update { currentState -> currentState.copy(profile = ComponentState.Loading) }
            when (val result = profileRepository.getProfile(email)) {
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
}

internal class QuickEditorViewModelFactory(
    private val gravatarQuickEditorParams: GravatarQuickEditorParams,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return QuickEditorViewModel(
            email = gravatarQuickEditorParams.email,
            profileRepository = QuickEditorContainer.getInstance().profileRepository,
            navigationEnabled = gravatarQuickEditorParams.scope == QuickEditorScope.AVATAR_AND_ABOUT,
            initialPage = gravatarQuickEditorParams.scope.initialPage,
            clock = SystemClock(),
        ) as T
    }
}

private val QuickEditorScope.initialPage: QuickEditorPage
    get() {
        return when (this) {
            QuickEditorScope.AVATAR -> QuickEditorPage.AVATAR_PICKER
            QuickEditorScope.ABOUT -> QuickEditorPage.ABOUT_EDITOR
            QuickEditorScope.AVATAR_AND_ABOUT -> QuickEditorPage.ABOUT_EDITOR
        }
    }
