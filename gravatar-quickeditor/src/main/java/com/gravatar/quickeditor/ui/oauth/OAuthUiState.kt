package com.gravatar.quickeditor.ui.oauth

internal data class OAuthUiState(
    val status: OAuthStatus = OAuthStatus.LoginRequired,
)

internal sealed class OAuthStatus {
    internal data object Authorizing : OAuthStatus()

    internal data object LoginRequired : OAuthStatus()

    internal data object WrongEmailAuthorized : OAuthStatus()
}

internal sealed class OAuthAction {
    internal data object StartOAuth : OAuthAction()

    internal data object AuthorizationSuccess : OAuthAction()

    internal data object AuthorizationFailure : OAuthAction()
}
