package com.gravatar.demoapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gravatar.demoapp.ui.theme.GravatarTheme
import com.gravatar.quickeditor.GravatarQuickEditor
import com.gravatar.quickeditor.QuickEditorContainer
import com.gravatar.quickeditor.ui.editor.AvatarPickerContentLayout
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorPage
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorParams
import com.gravatar.quickeditor.ui.editor.GravatarUiMode
import com.gravatar.quickeditor.ui.oauth.OAuthParams
import com.gravatar.types.Email

class InstantQEActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        QuickEditorContainer.init(applicationContext)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            GravatarQuickEditorPage(
                gravatarQuickEditorParams = GravatarQuickEditorParams {
                    email = Email("agrzybkowski@outlook.com")
                    avatarPickerContentLayout = AvatarPickerContentLayout.Vertical
                    uiMode = GravatarUiMode.SYSTEM
                },
                oAuthParams = OAuthParams {
                    clientId = BuildConfig.DEMO_OAUTH_CLIENT_ID
                    redirectUri = BuildConfig.DEMO_OAUTH_REDIRECT_URI
                },
                modifier = Modifier.statusBarsPadding(),
                onAvatarSelected = {  },
                onDismiss = remember {
                    { finish() }
                },
                onDoneClicked = remember {
                    { finish() }
                },
            )
        }
    }
}
