package com.gravatar.quickeditor.ui.avatarpicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.gravatar.quickeditor.QuickEditorContainer
import com.gravatar.quickeditor.data.storage.TokenStorage
import com.gravatar.services.AvatarService
import com.gravatar.services.ProfileService
import com.gravatar.services.Result
import com.gravatar.types.Email
import com.gravatar.ui.components.ComponentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class AvatarPickerViewModel(
    email: Email,
    private val avatarService: AvatarService,
    private val profileService: ProfileService,
    private val tokenStorage: TokenStorage,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AvatarPickerUiState(email = email))
    val uiState: StateFlow<AvatarPickerUiState> = _uiState.asStateFlow()

    init {
        fetchAvatars(email)
        fetchProfile(email)
    }

    private fun fetchProfile(email: Email) {
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

    private fun fetchAvatars(email: Email) {
        viewModelScope.launch {
            val token = tokenStorage.getToken(email.hash().toString())
            // Handle token not found
            token?.let {
                _uiState.update { it.copy(isLoading = true) }
                when (val result = avatarService.retrieveCatching(token)) {
                    is Result.Success -> {
                        _uiState.update { currentState ->
                            currentState.copy(avatars = result.value, error = false, isLoading = false)
                        }
                    }

                    is Result.Failure -> {
                        // Handle error properly
                        _uiState.update { currentState ->
                            currentState.copy(avatars = null, error = true, isLoading = false)
                        }
                    }
                }
            }
        }
    }
}

internal class AvatarPickerViewModelFactory(
    private val email: Email,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return AvatarPickerViewModel(
            email = email,
            avatarService = QuickEditorContainer.getInstance().avatarService,
            profileService = QuickEditorContainer.getInstance().profileService,
            tokenStorage = QuickEditorContainer.getInstance().tokenStorage,
        ) as T
    }
}
