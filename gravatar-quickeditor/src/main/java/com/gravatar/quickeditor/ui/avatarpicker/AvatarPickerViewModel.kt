package com.gravatar.quickeditor.ui.avatarpicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.gravatar.quickeditor.QuickEditorContainer
import com.gravatar.quickeditor.data.storage.TokenStorage
import com.gravatar.services.AvatarService
import com.gravatar.services.Result
import com.gravatar.types.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class AvatarPickerViewModel(
    email: Email,
    private val avatarService: AvatarService,
    private val tokenStorage: TokenStorage,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AvatarPickerUiState())
    val uiState: StateFlow<AvatarPickerUiState> = _uiState.asStateFlow()

    init {
        fetchAvatars(email)
    }

    private fun fetchAvatars(email: Email) {
        viewModelScope.launch {
            val token = tokenStorage.getToken(email.hash().toString())
            // Handle token not found
            token?.let {
                when (val result = avatarService.retrieveCatching(token)) {
                    is Result.Success -> {
                        _uiState.update { currentState -> currentState.copy(avatars = result.value, error = false) }
                    }

                    is Result.Failure -> {
                        // Handle error properly
                        _uiState.update { currentState -> currentState.copy(avatars = emptyList(), error = true) }
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
            tokenStorage = QuickEditorContainer.getInstance().tokenStorage,
        ) as T
    }
}
