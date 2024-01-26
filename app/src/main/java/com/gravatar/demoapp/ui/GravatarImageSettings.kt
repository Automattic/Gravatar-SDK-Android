package com.gravatar.demoapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.DefaultAvatarImage
import com.gravatar.R
import com.gravatar.demoapp.theme.GravatarDemoAppTheme

@Composable
fun GravatarImageSettings(
    email: String,
    size: Int?,
    onEmailChanged: (String) -> Unit,
    onSizeChange: (Int?) -> Unit,
    onLoadGravatarClicked: () -> Unit,
    defaultAvatarImageEnabled: Boolean,
    onDefaultAvatarImageEnabledChanged: (Boolean) -> Unit,
    selectedDefaultAvatarImage: DefaultAvatarImage,
    onDefaultAvatarImageChanged: (DefaultAvatarImage) -> Unit,
    defaultAvatarOptions: List<DefaultAvatarImage>,
    forceDefaultAvatar: Boolean,
    onForceDefaultAvatarChanged: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GravatarEmailInput(email = email, onValueChange = onEmailChanged, Modifier.fillMaxWidth())
        DefaultAvatarImageDropdown(
            enabled = defaultAvatarImageEnabled,
            selectedOption = selectedDefaultAvatarImage,
            onEnabledChanged = onDefaultAvatarImageEnabledChanged,
            onSelectedOptionChange = onDefaultAvatarImageChanged,
            defaultAvatarOptions = defaultAvatarOptions,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        )
        GravatarForceDefaultAvatarImage(
            enabled = forceDefaultAvatar,
            onEnabledChanged = onForceDefaultAvatarChanged,
            modifier = Modifier
                .fillMaxWidth(),
        )
        TextField(
            value = size?.toString() ?: "",
            onValueChange = { value -> onSizeChange(value.toIntOrNull()) },
            label = { Text(stringResource(R.string.gravatar_size_input_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.padding(vertical = 8.dp),
        )
        Button(onClick = onLoadGravatarClicked) { Text(text = "Load Gravatar") }
    }
}

@Composable
fun GravatarForceDefaultAvatarImage(
    enabled: Boolean,
    onEnabledChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = enabled,
            onCheckedChange = { onEnabledChanged(!enabled) },
        )
        Text(text = stringResource(R.string.force_default_avatar_image_label))
    }
}

@Preview
@Composable
fun GravatarImageSettingsPreview() {
    GravatarDemoAppTheme {
        GravatarImageSettings(
            email = "gravatar@automattic.com",
            size = null,
            onEmailChanged = {},
            onSizeChange = {},
            onLoadGravatarClicked = {},
            defaultAvatarImageEnabled = true,
            onDefaultAvatarImageEnabledChanged = {},
            selectedDefaultAvatarImage = DefaultAvatarImage.BLANK,
            onDefaultAvatarImageChanged = {},
            defaultAvatarOptions = DefaultAvatarImage.entries,
            forceDefaultAvatar = false,
            onForceDefaultAvatarChanged = {},
        )
    }
}
