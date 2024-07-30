package com.gravatar.quickeditor.ui.splash

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gravatar.ui.GravatarTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun SplashPage(
    email: String,
    viewModel: SplashViewModel = viewModel(factory = SplashViewModelFactory(email)),
    onAuthorized: (Boolean) -> Unit,
) {
    val currentOnAuthorized by rememberUpdatedState(onAuthorized)
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Main.immediate) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actions.collect { action ->
                    when (action) {
                        SplashAction.ShowQuickEditor -> currentOnAuthorized(true)
                        SplashAction.ShowOAuth -> currentOnAuthorized(false)
                    }
                }
            }
        }
    }

    GravatarTheme {
        Surface(modifier = Modifier.fillMaxSize()) {}
    }
}
