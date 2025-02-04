package com.gravatar.quickeditor.ui.oauth

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.gravatar.quickeditor.QuickEditorContainer
import com.gravatar.quickeditor.data.storage.ProfileStorage
import com.gravatar.quickeditor.data.storage.TokenStorage
import com.gravatar.quickeditor.ui.time.Clock
import com.gravatar.quickeditor.ui.time.SystemClock
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
    private val savedStateHandle: SavedStateHandle,
    private val email: Email,
    private val tokenStorage: TokenStorage,
    private val profileStorage: ProfileStorage,
    private val profileService: ProfileService,
    clock: Clock,
) : ViewModel() {
    private val _uiState = MutableStateFlow(OAuthUiState(avatarCacheBuster = clock.getTimeMillis().toString()))
    val uiState: StateFlow<OAuthUiState> = _uiState.asStateFlow()

    private val _actions = Channel<OAuthAction>(Channel.BUFFERED)
    val actions = _actions.receiveAsFlow()

    init {
        fetchProfile()
        viewModelScope.launch {
            val loginIntroShown = profileStorage.getLoginIntroShown(email.hash().toString())
            val oauthStarted = savedStateHandle.get<Boolean>(OAUTH_STARTED_KEY) ?: false
            if (loginIntroShown && !oauthStarted) {
                _actions.send(OAuthAction.StartOAuth)
                savedStateHandle[OAUTH_STARTED_KEY] = true
            }
        }
    }

    fun startOAuth() {
        viewModelScope.launch {
            profileStorage.setLoginIntroShown(email.hash().toString())
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

    companion object {
        private const val OAUTH_STARTED_KEY = "oauth_started"
    }
}

internal class OAuthViewModelFactory(
    private val email: Email,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return OAuthViewModel(
            savedStateHandle = extras.createSavedStateHandle(),
            email = email,
            tokenStorage = QuickEditorContainer.getInstance().tokenStorage,
            profileStorage = QuickEditorContainer.getInstance().profileStorage,
            profileService = QuickEditorContainer.getInstance().profileService,
            clock = SystemClock(),
        ) as T
    }
}
