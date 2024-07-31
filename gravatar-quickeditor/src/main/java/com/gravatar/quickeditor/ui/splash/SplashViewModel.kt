package com.gravatar.quickeditor.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.gravatar.quickeditor.QuickEditorContainer
import com.gravatar.quickeditor.data.storage.TokenStorage
import com.gravatar.types.Email
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class SplashViewModel(
    email: Email,
    private val tokenStorage: TokenStorage,
) : ViewModel() {
    private val _actions = Channel<SplashAction>(Channel.BUFFERED)
    val actions = _actions.receiveAsFlow()

    init {
        viewModelScope.launch {
            if (tokenStorage.getToken(email.hash().toString()) != null) {
                _actions.send(SplashAction.ShowQuickEditor)
            } else {
                _actions.send(SplashAction.ShowOAuth)
            }
        }
    }
}

internal class SplashViewModelFactory(
    private val email: Email,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return SplashViewModel(
            email = email,
            tokenStorage = QuickEditorContainer.getInstance().tokenStorage,
        ) as T
    }
}
