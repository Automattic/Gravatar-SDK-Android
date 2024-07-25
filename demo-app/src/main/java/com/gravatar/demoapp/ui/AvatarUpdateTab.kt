package com.gravatar.demoapp.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.demoapp.BuildConfig
import com.gravatar.demoapp.R
import com.gravatar.demoapp.ui.components.GravatarEmailInput
import com.gravatar.quickeditor.ui.editor.bottomsheet.GravatarQuickEditorBottomSheet
import com.gravatar.quickeditor.ui.oauth.OAuthParams

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarUpdateTab(modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf(BuildConfig.DEMO_EMAIL) }
    val context = LocalContext.current
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GravatarEmailInput(email = email, onValueChange = { email = it }, Modifier.fillMaxWidth())
        UpdateAvatarComposable(
            modifier = Modifier.clickable {
                showBottomSheet = true
            },
            isUploading = false,
        )
    }
    if (showBottomSheet) {
        val applicationName = stringResource(id = R.string.app_name)
        GravatarQuickEditorBottomSheet(
            appName = applicationName,
            oAuthParams = OAuthParams {
                clientId = BuildConfig.DEMO_WORDPRESS_CLIENT_ID
                clientSecret = BuildConfig.DEMO_WORDPRESS_CLIENT_SECRET
                redirectUri = BuildConfig.DEMO_WORDPRESS_REDIRECT_URI
            },
            onAvatarSelected = {
                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            },
            onDismiss = {
                Toast.makeText(context, it?.toString() ?: "Dismissed", Toast.LENGTH_SHORT).show()
                showBottomSheet = false
            },
        )
    }
}

@Composable
private fun UpdateAvatarComposable(isUploading: Boolean, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (isUploading) {
            CircularProgressIndicator()
        } else {
            Icon(
                Icons.Rounded.AccountCircle,
                contentDescription = "",
                modifier = Modifier.size(128.dp),
            )
            Text(text = stringResource(R.string.update_avatar_button_label))
        }
    }
}

@Preview
@Composable
private fun UpdateAvatarComposablePreview() = UpdateAvatarComposable(false)

@Preview
@Composable
private fun UpdateAvatarLoadingComposablePreview() = UpdateAvatarComposable(true)

@Preview
@Composable
private fun AvatarUpdateTabPreview() = AvatarUpdateTab()
