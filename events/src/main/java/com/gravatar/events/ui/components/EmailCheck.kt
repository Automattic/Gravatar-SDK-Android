package com.gravatar.events.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import com.gravatar.sha256Hash

// Jetpack UI component to check that the hash we got is the same as the hashed email that the user had input
@Composable
public fun EmailCheck(hash: String, onEmailValidated: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("") }
    var wrongEmail by remember { mutableStateOf(false) }

    Column {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail address") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email, capitalization = KeyboardCapitalization.None),
            modifier = modifier,
            isError = wrongEmail,
            supportingText = {
                if (wrongEmail) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Wrong e-mail address. Please verify.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.align(Alignment.End),
            onClick = {
                if(hash == email.lowercase().sha256Hash()){
                    onEmailValidated(true)
                } else {
                    wrongEmail = true
                }
        }) {
            Text(text = "Check e-mail address")
        }
    }

}
