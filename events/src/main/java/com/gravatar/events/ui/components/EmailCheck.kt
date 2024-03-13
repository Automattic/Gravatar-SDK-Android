package com.gravatar.events.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.gravatar.sha256Hash

// Jetpack UI component to check that the hash we got is the same as the hashed email that the user had input
@Composable
public fun EmailCheck(hash: String, onEmailValidated: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("") }
    TextField(
        value = email,
        onValueChange = {
            email = it
            onEmailValidated(hash == it.sha256Hash())
        },
        label = { Text("Email") },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
        modifier = modifier,
    )
}
