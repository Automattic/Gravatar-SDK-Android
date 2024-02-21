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
import com.gravatar.ImageRating
import com.gravatar.R
import com.gravatar.demoapp.theme.GravatarDemoAppTheme
import com.gravatar.demoapp.ui.model.SettingsState

@Composable
fun GravatarImageSettings(
    settingsState: SettingsState,
    onEmailChanged: (String) -> Unit,
    onSizeChange: (Int?) -> Unit,
    onLoadGravatarClicked: () -> Unit,
    onDefaultAvatarImageEnabledChanged: (Boolean) -> Unit,
    onDefaultAvatarImageChanged: (DefaultAvatarImage) -> Unit,
    onForceDefaultAvatarChanged: (Boolean) -> Unit,
    onImageRatingChanged: (ImageRating) -> Unit,
    onImageRatingEnabledChange: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GravatarEmailInput(email = settingsState.email, onValueChange = onEmailChanged, Modifier.fillMaxWidth())
        Row(modifier = Modifier.fillMaxWidth()) {
            DropdownMenuWithCheckbox(
                enabled = settingsState.defaultAvatarImageEnabled,
                selectedOption = settingsState.selectedDefaultAvatar,
                onEnabledChanged = onDefaultAvatarImageEnabledChanged,
                onSelectedOptionChange = onDefaultAvatarImageChanged,
                options = settingsState.defaultAvatarOptions,
                labelForOption = { it.queryParam() },
                inputLabel = stringResource(R.string.default_avatar_image_label),
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
            )
            DropdownMenuWithCheckbox(
                enabled = settingsState.imageRatingEnabled,
                selectedOption = settingsState.imageRating,
                onEnabledChanged = onImageRatingEnabledChange,
                onSelectedOptionChange = onImageRatingChanged,
                options = ImageRating.entries,
                labelForOption = { it.rating },
                inputLabel = stringResource(R.string.image_rating_label),
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            GravatarForceDefaultAvatarImage(
                enabled = settingsState.forceDefaultAvatar,
                onEnabledChanged = onForceDefaultAvatarChanged,
                modifier = Modifier.weight(1f),
            )
            TextField(
                value = settingsState.size?.toString() ?: "",
                onValueChange = { value -> onSizeChange(value.toIntOrNull()) },
                label = { Text(stringResource(R.string.gravatar_size_input_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .weight(1f),
            )
        }
        Button(onClick = onLoadGravatarClicked) { Text(text = stringResource(R.string.button_load_gravatar)) }
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
            settingsState = SettingsState(
                email = "gravatar@automattic.com",
                size = null,
                defaultAvatarImageEnabled = true,
                selectedDefaultAvatar = DefaultAvatarImage.Blank,
                defaultAvatarOptions = defaultAvatarImages,
                forceDefaultAvatar = false,
                imageRatingEnabled = false,
                imageRating = ImageRating.General,
            ),
            onEmailChanged = {},
            onSizeChange = {},
            onLoadGravatarClicked = {},
            onDefaultAvatarImageEnabledChanged = {},
            onDefaultAvatarImageChanged = {},
            onForceDefaultAvatarChanged = {},
            onImageRatingChanged = {},
            onImageRatingEnabledChange = {},
        )
    }
}
