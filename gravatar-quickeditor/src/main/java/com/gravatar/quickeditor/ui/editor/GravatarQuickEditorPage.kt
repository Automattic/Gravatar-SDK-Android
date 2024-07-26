package com.gravatar.quickeditor.ui.editor

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.gravatar.quickeditor.ui.oauth.OAuthPage
import com.gravatar.quickeditor.ui.oauth.OAuthParams

/**
 * Raw composable component for the Quick Editor.
 * This can be used to show the Quick Editor in Activity, Fragment or BottomSheet.
 *
 * @param appName Name of the app that is launching the Quick Editor
 * @param oAuthParams The OAuth parameters.
 * @param onAvatarSelected The callback for the avatar update result, check [AvatarUpdateResult].
 *                       Can be invoked multiple times while the Quick Editor is open
 * @param onDismiss The callback for the dismiss action.
 *                  [GravatarQuickEditorError] will be non-null if the dismiss was caused by an error.
 */
@Composable
internal fun GravatarQuickEditorPage(
    appName: String,
    oAuthParams: OAuthParams,
    onAvatarSelected: (AvatarUpdateResult) -> Unit,
    onDismiss: (dismissReason: GravatarQuickEditorDismissReason) -> Unit = {},
) {
    val isAuthorized = rememberSaveable { mutableStateOf(false) }

    Surface {
        if (!isAuthorized.value) {
            OAuthPage(
                appName = appName,
                oauthParams = oAuthParams,
                onAuthSuccess = { isAuthorized.value = true },
                onAuthError = { onDismiss(GravatarQuickEditorDismissReason.OauthFailed) },
            )
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                TextButton(
                    modifier = Modifier.align(Alignment.Center),
                    onClick = {
                        onAvatarSelected(AvatarUpdateResult(Uri.EMPTY))
                    },
                ) {
                    Text(
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
    val appName = "FancyMobileApp"
    val oAuthParams = OAuthParams {
        clientSecret = "clientSecret"
        clientId = "clientId"
        redirectUri = "redirectUri"
    }
    GravatarQuickEditorPage(
        appName = appName,
        oAuthParams = oAuthParams,
        onAvatarSelected = {},
        onDismiss = {},
    )
}
