package com.gravatar.quickeditor.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.gravatar.ui.GravatarTheme

@Composable
internal fun GravatarQuickEditorSplashPage(onAuthorized: (Boolean) -> Unit) {
    val isAuthorized by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        onAuthorized(isAuthorized)
    }

    GravatarTheme {
        Surface(modifier = Modifier.fillMaxSize()) {}
    }
}
