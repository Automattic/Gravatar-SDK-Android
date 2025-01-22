package com.gravatar.quickeditor.ui.oauth

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.util.Consumer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gravatar.quickeditor.R
import com.gravatar.quickeditor.data.appName
import com.gravatar.quickeditor.ui.components.CtaSection
import com.gravatar.quickeditor.ui.components.ProfileCard
import com.gravatar.quickeditor.ui.components.QEPageDefault
import com.gravatar.quickeditor.ui.components.QESectionMessage
import com.gravatar.types.Email
import com.gravatar.ui.GravatarTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLDecoder

@Composable
internal fun OAuthPage(
    email: Email,
    oAuthParams: OAuthParams,
    onAuthSuccess: () -> Unit,
    onAuthError: () -> Unit,
    onDoneClicked: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OAuthViewModel = viewModel(factory = OAuthViewModelFactory(email)),
) {
    val context = LocalContext.current
    val activity = context.findComponentActivity()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val uiState by viewModel.uiState.collectAsState()

    val oAuthLauncher = rememberLauncherForActivityResult(GravatarOAuthResultContract()) { result ->
        when (result) {
            GravatarOAuthResult.DISMISSED -> Unit
            is GravatarOAuthResult.TOKEN -> viewModel.tokenReceived(email, result.token)
            GravatarOAuthResult.ERROR -> onAuthError()
        }
    }

    // Kept for backwards-compatibility.
    // This will be removed in the future and the GravatarOAuthActivity should be used instead.
    if (activity != null) {
        DisposableEffect(Unit) {
            val listener = Consumer<Intent> { newIntent ->
                Log.w(
                    "QuickEditor",
                    "GRAVATAR SDK WARNING: You're using a deprecated version of the Gravatar QuickEditor OAuth " +
                        "flow. Set up GravatarOAuthActivity in your AndroidManifest.xml to handle the OAuth flow and " +
                        "to remove this warning message.",
                )
                val token = newIntent.data
                    ?.encodedFragment
                    ?.split("&")
                    ?.associate {
                        val split = it.split("=")
                        split.first() to split.last()
                    }
                    ?.get("access_token")
                    ?.let { URLDecoder.decode(it, "UTF-8") }

                if (token != null) {
                    viewModel.tokenReceived(email, token)
                } else {
                    onAuthError()
                }
            }
            activity.addOnNewIntentListener(listener)
            onDispose {
                activity.removeOnNewIntentListener(listener)
            }
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Main.immediate) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actions.collect { action ->
                    when (action) {
                        OAuthAction.AuthorizationSuccess -> onAuthSuccess()
                        OAuthAction.AuthorizationFailure -> onAuthError()
                        OAuthAction.StartOAuth -> {
                            oAuthLauncher.launch(
                                GravatarOAuthActivityParams(
                                    oAuthParams = oAuthParams,
                                    email = email.toString(),
                                ),
                            )
                        }
                    }
                }
            }
        }
    }

    OauthPage(
        uiState = uiState,
        email = email,
        onStartOAuthClicked = viewModel::startOAuth,
        onDoneClicked = onDoneClicked,
        onEmailAssociationCheckClicked = remember { { viewModel.checkAuthorizedUserEmail(email, it) } },
        modifier = modifier,
    )
}

@Composable
internal fun OauthPage(
    uiState: OAuthUiState,
    email: Email,
    onStartOAuthClicked: () -> Unit,
    onDoneClicked: () -> Unit,
    modifier: Modifier = Modifier,
    onEmailAssociationCheckClicked: (String) -> Unit = {},
) {
    val context = LocalContext.current
    GravatarTheme {
        QEPageDefault(
            onDoneClicked = onDoneClicked,
            content = {
                Surface {
                    Column(
                        modifier = modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                    ) {
                        Text(
                            text = stringResource(R.string.gravatar_qe_oauth_page_title),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 32.sp,
                                letterSpacing = 0.4.sp,
                            ),
                            modifier = Modifier.padding(top = 10.dp),
                        )
                        QESectionMessage(
                            message = stringResource(R.string.gravatar_qe_oauth_page_message, context.appName),
                            modifier = Modifier.padding(top = 4.dp),
                        )
                        uiState.profile?.let {
                            ProfileCard(
                                profile = it,
                                modifier = Modifier.padding(top = 16.dp),
                            )
                        }
                        val sectionModifier = Modifier.padding(top = 24.dp, bottom = 10.dp)
                        when (val status = uiState.status) {
                            OAuthStatus.Authorizing -> Box(
                                modifier = sectionModifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                        shape = RoundedCornerShape(8.dp),
                                    ),
                            ) {
                                CircularProgressIndicator(modifier = Modifier.align(Center))
                            }

                            OAuthStatus.LoginRequired -> {
                                CtaSection(
                                    message = stringResource(R.string.gravatar_qe_login_required_message_v2),
                                    buttonText = stringResource(id = R.string.gravatar_qe_login_required_cta),
                                    onButtonClick = onStartOAuthClicked,
                                    modifier = sectionModifier,
                                )
                            }

                            OAuthStatus.WrongEmailAuthorized -> {
                                CtaSection(
                                    title = stringResource(R.string.gravatar_qe_avatar_picker_server_error_title),
                                    message = stringResource(
                                        R.string.gravatar_qe_oauth_wrong_email_authenticated_error_message,
                                        email.toString(),
                                    ),
                                    buttonText = stringResource(
                                        id = R.string.gravatar_qe_avatar_picker_session_error_cta,
                                    ),
                                    onButtonClick = onStartOAuthClicked,
                                    modifier = sectionModifier,
                                )
                            }

                            is OAuthStatus.EmailAssociatedCheckError -> CtaSection(
                                title = stringResource(R.string.gravatar_qe_avatar_picker_server_error_title),
                                message = stringResource(
                                    R.string.gravatar_qe_oauth_email_associated_error_message,
                                    email.toString(),
                                ),
                                buttonText = stringResource(id = R.string.gravatar_qe_avatar_picker_error_retry_cta),
                                onButtonClick = { onEmailAssociationCheckClicked(status.token) },
                                modifier = sectionModifier,
                            )
                        }
                    }
                }
            },
        )
    }
}

internal fun Context.findComponentActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findComponentActivity()
    else -> null
}

@Preview
@Composable
private fun OAuthPagePreview() {
    GravatarTheme {
        OauthPage(
            uiState = OAuthUiState(),
            email = Email("email"),
            onStartOAuthClicked = { },
            onDoneClicked = { },
        )
    }
}

@Preview
@Composable
private fun OAuthPageLoadingPreview() {
    GravatarTheme {
        OauthPage(
            uiState = OAuthUiState(
                status = OAuthStatus.Authorizing,
            ),
            email = Email("email"),
            onStartOAuthClicked = { },
            onDoneClicked = { },
        )
    }
}
