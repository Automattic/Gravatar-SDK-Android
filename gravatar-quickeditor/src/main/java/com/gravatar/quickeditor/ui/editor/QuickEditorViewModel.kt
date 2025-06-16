package com.gravatar.quickeditor.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.gravatar.quickeditor.QuickEditorContainer
import com.gravatar.quickeditor.data.repository.ProfileRepository
import com.gravatar.quickeditor.ui.editor.AvatarPickerAndAboutEditorConfiguration.Page.Companion.AboutEditor
import com.gravatar.quickeditor.ui.editor.AvatarPickerAndAboutEditorConfiguration.Page.Companion.AvatarPicker
import com.gravatar.quickeditor.ui.editor.QuickEditorScopeOption.Scope
import com.gravatar.quickeditor.ui.time.Clock
import com.gravatar.quickeditor.ui.time.SystemClock
import com.gravatar.services.GravatarResult
import com.gravatar.types.Email
import com.gravatar.ui.components.ComponentState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Suppress("LongParameterList")
internal class QuickEditorViewModel(
    private val email: Email,
    private val profileRepository: ProfileRepository,
    private val clock: Clock,
    initialPage: QuickEditorPage,
    navigationEnabled: Boolean,
    compactWindowEnabled: Boolean,
    showEmail: Boolean,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        QuickEditorUiState(
            email = email,
            page = initialPage,
            pageNavigationEnabled = navigationEnabled,
            avatarCacheBuster = clock.getTimeMillis(),
            compactWindow = compactWindowEnabled,
            emailVisible = showEmail,
        ),
    )
    val uiState: StateFlow<QuickEditorUiState> = _uiState.asStateFlow()

    private val _actions = Channel<QuickEditorAction>(Channel.BUFFERED)
    val actions = _actions.receiveAsFlow()

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

            QuickEditorEvent.OnEditAboutClicked -> navigateToPage(QuickEditorPage.AboutEditor)
            QuickEditorEvent.OnEditAvatarClicked -> navigateToPage(QuickEditorPage.AvatarPicker)
            QuickEditorEvent.OnConfirmDismissal -> checkForUnsavedChanges()
            is QuickEditorEvent.OnCompactWindowEnabled -> _uiState.update { currentState ->
                currentState.copy(compactWindow = event.enabled)
            }

            QuickEditorEvent.OnUnsavedChangesKeepEditingClicked -> dismissUnsavedChangesDialog()
            QuickEditorEvent.OnUnsavedChangesExitClicked -> discardUnsavedChangesDialog()
            is QuickEditorEvent.OnAboutEditorUnsavedChangesUpdated -> _uiState.update { currentState ->
                currentState.copy(aboutEditorUnsavedChangesPresent = event.unsavedChanges)
            }
        }
    }

    private fun checkForUnsavedChanges() {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            if (uiState.value.aboutEditorUnsavedChangesPresent) {
                _uiState.update { currentState ->
                    currentState.copy(discardAboutEditorChangesDialogVisible = true)
                }
            } else {
                viewModelScope.launch {
                    _actions.send(QuickEditorAction.DismissEditor)
                }
            }
        }
    }

    private fun discardUnsavedChangesDialog() {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            _actions.send(QuickEditorAction.DismissEditor)
        }
        _uiState.update { currentState ->
            currentState.copy(discardAboutEditorChangesDialogVisible = false)
        }
    }

    private fun dismissUnsavedChangesDialog() {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            _actions.send(QuickEditorAction.NotifyDismissIgnored)
        }
        _uiState.update { currentState ->
            currentState.copy(discardAboutEditorChangesDialogVisible = false)
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
    private val compactWindowEnabled: Boolean,
    private val showEmail: Boolean,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val scopeConfig = gravatarQuickEditorParams.scopeOption
        return QuickEditorViewModel(
            email = gravatarQuickEditorParams.email,
            profileRepository = QuickEditorContainer.getInstance().profileRepository,
            navigationEnabled = scopeConfig.scope is Scope.AvatarPickerAndAboutEditor,
            initialPage = scopeConfig.initialPage,
            clock = SystemClock(),
            compactWindowEnabled = compactWindowEnabled,
            showEmail = showEmail,
        ) as T
    }
}

internal val AvatarPickerAndAboutEditorConfiguration.Page.internalType: QuickEditorPage
    get() = when (this) {
        AvatarPicker -> QuickEditorPage.AvatarPicker
        AboutEditor -> QuickEditorPage.AboutEditor
        else -> QuickEditorPage.AvatarPicker
    }
