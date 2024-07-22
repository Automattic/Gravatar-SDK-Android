package com.gravatar.quickeditor.ui.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.gravatar.quickeditor.ui.oauth.OAuthPage
import com.gravatar.quickeditor.ui.oauth.OAuthParams

@Composable
internal fun GravatarQuickEditorPage(
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    onAuthError: () -> Unit = {},
) {
    val isAuthorized = rememberSaveable { mutableStateOf(false) }

    Surface {
        if (!isAuthorized.value) {
            OAuthPage(
                appName = gravatarQuickEditorParams.appName,
                oauthParams = gravatarQuickEditorParams.oAuthParams,
                onAuthSuccess = { isAuthorized.value = true },
                onAuthError = onAuthError,
            )
        } else {
            when (gravatarQuickEditorParams.scope) {
                GravatarQuickEditorScope.AVATARS ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center,
                            text = "Insert the real avatar picker page here",
                        )
                    }
            }
        }
    }
}

@Preview
@Composable
private fun ProfileQuickEditorPagePreview() {
    val gravatarQuickEditorParams = GravatarQuickEditorParams {
        appName = "FancyMobileApp"
        oAuthParams = OAuthParams {
            clientSecret = "clientSecret"
            clientId = "clientId"
            redirectUri = "redirectUri"
        }
        scope = GravatarQuickEditorScope.AVATARS
    }
    GravatarQuickEditorPage(gravatarQuickEditorParams = gravatarQuickEditorParams)
}
