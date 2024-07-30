package com.gravatar.quickeditor.ui.splash

internal sealed class SplashAction {
    data object ShowQuickEditor : SplashAction()

    data object ShowOAuth : SplashAction()
}
