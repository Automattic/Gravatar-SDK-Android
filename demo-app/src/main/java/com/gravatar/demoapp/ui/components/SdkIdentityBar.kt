package com.gravatar.demoapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gravatar.Gravatar
import com.gravatar.demoapp.BuildConfig
import com.gravatar.demoapp.R

/**
 * Shows the User-Agent the SDK will put on every request, and lets you name the app both ways so
 * you can watch the header change.
 *
 * Pressing `context()` after `appIdentifier()` deliberately does nothing: an explicit identifier
 * wins whatever the call order, so that button going quiet is the behaviour rather than a bug.
 */
@Composable
internal fun SdkIdentityBar(modifier: Modifier = Modifier) {
    var userAgent by remember { mutableStateOf(Gravatar.userAgent) }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Text(
            text = stringResource(R.string.sdk_user_agent_label, userAgent),
            style = MaterialTheme.typography.labelSmall,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(
                onClick = {
                    Gravatar.appIdentifier("gravatar-demo", BuildConfig.VERSION_NAME)
                    userAgent = Gravatar.userAgent
                },
            ) {
                Text(stringResource(R.string.sdk_identity_app_identifier))
            }
            TextButton(
                onClick = {
                    Gravatar.context(context.applicationContext)
                    userAgent = Gravatar.userAgent
                },
            ) {
                Text(stringResource(R.string.sdk_identity_context))
            }
        }
    }
}
