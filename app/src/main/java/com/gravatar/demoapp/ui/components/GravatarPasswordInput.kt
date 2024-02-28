package com.gravatar.demoapp.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun GravatarPasswordInput(
    password: String,
    passwordIsVisible: Boolean,
    onValueChange: (String) -> Unit,
    onVisibilityChange: (Boolean) -> Unit,
    label: @Composable (() -> Unit),
    modifier: Modifier = Modifier,
) {
    TextField(
        value = password,
        onValueChange = onValueChange,
        label = label,
        maxLines = 1,
        modifier = modifier,
        visualTransformation = if (passwordIsVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (passwordIsVisible) {
                Icons.Filled.Visibility
            } else {
                Icons.Filled.VisibilityOff
            }

            IconButton(onClick = { onVisibilityChange(!passwordIsVisible) }) {
                Icon(imageVector = image, "")
            }
        },
    )
}
