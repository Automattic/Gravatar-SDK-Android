package com.gravatar.quickeditor.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.gravatar.quickeditor.QuickEditorContainer
import com.gravatar.quickeditor.data.storage.DataStoreTokenStorage
import com.gravatar.quickeditor.data.storage.InMemoryTokenStorage
import com.gravatar.types.Email
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class SplashViewModel(
    private val email: Email,
    token: String?,
    private val inMemoryTokenStorage: InMemoryTokenStorage,
    private val dataStoreTokenStorage: DataStoreTokenStorage,
) : ViewModel() {
    private val _actions = Channel<SplashAction>(Channel.BUFFERED)
    val actions = _actions.receiveAsFlow()

    init {
        if (token != null) {
            initUserSession(token)
        } else {
            checkUserSession()
        }
    }

    private fun initUserSession(token: String) {
        viewModelScope.launch {
            inMemoryTokenStorage.storeToken(email.hash().toString(), token)
            _actions.send(SplashAction.ShowQuickEditor)
        }
    }

    private fun checkUserSession() {
        viewModelScope.launch {
            if (dataStoreTokenStorage.getToken(email.hash().toString()) != null) {
                _actions.send(SplashAction.ShowQuickEditor)
            } else {
                _actions.send(SplashAction.ShowOAuth)
            }
        }
    }
}

internal class SplashViewModelFactory(
    private val email: Email,
    private val token: String?,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return SplashViewModel(
            email = email,
            token = token,
            inMemoryTokenStorage = QuickEditorContainer.getInstance().inMemoryTokenStorage,
            dataStoreTokenStorage = QuickEditorContainer.getInstance().dataStoreTokenStorage,
        ) as T
    }
}
