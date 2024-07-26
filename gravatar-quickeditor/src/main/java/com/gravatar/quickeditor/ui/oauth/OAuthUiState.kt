package com.gravatar.quickeditor.ui.oauth

internal data class OAuthUiState(
    val isAuthorizing: Boolean = false,
)

internal sealed class OAuthAction {
    internal data object StartOAuth : OAuthAction()

    internal data object AuthorizationSuccess : OAuthAction()

    internal data object AuthorizationFailure : OAuthAction()
}
