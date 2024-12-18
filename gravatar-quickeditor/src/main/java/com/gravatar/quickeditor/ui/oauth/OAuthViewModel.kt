package com.gravatar.quickeditor.ui.oauth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.gravatar.quickeditor.QuickEditorContainer
import com.gravatar.quickeditor.data.storage.TokenStorage
import com.gravatar.services.ErrorType
import com.gravatar.services.GravatarResult
import com.gravatar.services.ProfileService
import com.gravatar.types.Email
import com.gravatar.ui.components.ComponentState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class OAuthViewModel(
    private val email: Email,
    private val tokenStorage: TokenStorage,
    private val profileService: ProfileService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(OAuthUiState())
    val uiState: StateFlow<OAuthUiState> = _uiState.asStateFlow()

    private val _actions = Channel<OAuthAction>(Channel.BUFFERED)
    val actions = _actions.receiveAsFlow()

    init {
        fetchProfile()
    }

    fun startOAuth() {
        viewModelScope.launch {
            _actions.send(OAuthAction.StartOAuth)
        }
    }

    fun tokenReceived(email: Email, token: String) {
        checkAuthorizedUserEmail(email, token)
    }

    fun checkAuthorizedUserEmail(email: Email, token: String) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(status = OAuthStatus.Authorizing)
            }
            when (val result = profileService.checkAssociatedEmailCatching(token, email)) {
                is GravatarResult.Success -> {
                    result.value.let {
                        if (it) {
                            tokenStorage.storeToken(email.hash().toString(), token)
                            _actions.send(OAuthAction.AuthorizationSuccess)
                        } else {
                            _uiState.update { currentState ->
                                currentState.copy(status = OAuthStatus.WrongEmailAuthorized)
                            }
                        }
                    }
                }

                is GravatarResult.Failure -> {
                    _uiState.update { currentState ->
                        currentState.copy(status = OAuthStatus.EmailAssociatedCheckError(token))
                    }
                }
            }
        }
    }

    private fun fetchProfile() {
        viewModelScope.launch {
            _uiState.update { currentState -> currentState.copy(profile = ComponentState.Loading) }
            when (val result = profileService.retrieveCatching(email)) {
                is GravatarResult.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(profile = ComponentState.Loaded(result.value))
                    }
                }

                is GravatarResult.Failure -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            profile = if (result.error is ErrorType.NotFound) {
                                ComponentState.Empty
                            } else {
                                null
                            },
                        )
                    }
                }
            }
        }
    }
}

internal class OAuthViewModelFactory(
    private val email: Email,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return OAuthViewModel(
            email = email,
            tokenStorage = QuickEditorContainer.getInstance().tokenStorage,
            profileService = QuickEditorContainer.getInstance().profileService,
        ) as T
    }
}
