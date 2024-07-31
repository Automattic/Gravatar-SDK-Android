package com.gravatar.quickeditor.ui.oauth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.gravatar.quickeditor.QuickEditorContainer
import com.gravatar.quickeditor.data.service.WordPressOAuthService
import com.gravatar.quickeditor.data.storage.TokenStorage
import com.gravatar.services.Result
import com.gravatar.types.Email
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class OAuthViewModel(
    private val wordPressOAuthService: WordPressOAuthService,
    private val tokenStorage: TokenStorage,
) : ViewModel() {
    private val _uiState = MutableStateFlow(OAuthUiState())
    val uiState: StateFlow<OAuthUiState> = _uiState.asStateFlow()

    private val _actions = Channel<OAuthAction>(Channel.BUFFERED)
    val actions = _actions.receiveAsFlow()

    init {
        viewModelScope.launch {
            _actions.send(OAuthAction.StartOAuth)
        }
    }

    fun fetchAccessToken(code: String, oAuthParams: OAuthParams, email: Email) {
        viewModelScope.launch {
            _uiState.update { currentState -> currentState.copy(isAuthorizing = true) }
            val result = wordPressOAuthService.getAccessToken(
                code = code,
                clientId = oAuthParams.clientId,
                clientSecret = oAuthParams.clientSecret,
                redirectUri = oAuthParams.redirectUri,
            )

            when (result) {
                is Result.Success -> {
                    tokenStorage.storeToken(email.toString(), result.value)
                    _uiState.update { currentState -> currentState.copy(isAuthorizing = false) }
                    _actions.send(OAuthAction.AuthorizationSuccess)
                }

                is Result.Failure -> {
                    _uiState.update { currentState -> currentState.copy(isAuthorizing = false) }
                    _actions.send(OAuthAction.AuthorizationFailure)
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return OAuthViewModel(
                    wordPressOAuthService = QuickEditorContainer.getInstance().wordPressOAuthService,
                    tokenStorage = QuickEditorContainer.getInstance().tokenStorage,
                ) as T
            }
        }
    }
}
