package com.gravatar.events.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
public fun EmailCheckingView(hash: String, onEmailValidated: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
    ) {
        // TODO: check for invalid hash (not supported by gravatar)
        if (hash.isEmpty()) {
            Text("Please scan your own badge first", modifier = Modifier.padding(16.dp))
        } else {
            EmailChecking(hash, onEmailValidated, modifier)
        }
    }
}

@Composable
private fun EmailChecking(hash: String, onEmailValidated: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Enter the email address that matches your badge")
        Spacer(modifier = Modifier.height(8.dp))
        EmailCheck(
            hash,
            modifier = Modifier.fillMaxWidth(),
            onEmailValidated = {
                if (it) {
                    Toast.makeText(
                        context,
                        "Thank you for validating your email address!",
                        Toast.LENGTH_LONG,
                    ).show()
                }
                onEmailValidated(it)
            },
        )
    }
}
