package com.gravatar.demoapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gravatar.DefaultAvatarImage
import com.gravatar.R
import com.gravatar.demoapp.theme.GravatarDemoAppTheme
import com.gravatar.emailAddressToGravatarUrl

@Composable
fun DemoGravatarApp() {
    var email by remember { mutableStateOf("gravatarMailHere@gravatar.com") }
    var gravatarUrl by remember { mutableStateOf("") }
    var avatarSize by remember { mutableStateOf<Int?>(null) }
    var defaultAvatarImageEnabled by remember { mutableStateOf(false) }
    var selectedDefaultAvatar by remember { mutableStateOf(DefaultAvatarImage.MONSTER) }
    val defaultAvatarOptions = DefaultAvatarImage.entries

    GravatarDemoAppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            GravatarImageSettings(
                email = email,
                size = avatarSize,
                onEmailChanged = { email = it },
                onSizeChange = { avatarSize = it },
                onLoadGravatarClicked = {
                    gravatarUrl = emailAddressToGravatarUrl(
                        email = email,
                        size = avatarSize,
                        defaultAvatarImage = if (defaultAvatarImageEnabled) selectedDefaultAvatar else null,
                    )
                },
                onDefaultAvatarImageEnabledChanged = {
                    defaultAvatarImageEnabled = it
                },
                defaultAvatarImageEnabled = defaultAvatarImageEnabled,
                selectedDefaultAvatarImage = selectedDefaultAvatar,
                onDefaultAvatarImageChanged = { selectedDefaultAvatar = it },
                defaultAvatarOptions = defaultAvatarOptions,
            )

            if (gravatarUrl.isNotEmpty()) {
                GravatarDivider()

                GravatarGeneratedUrl(gravatarUrl = gravatarUrl)

                GravatarDivider()

                GravatarImage(gravatarUrl = gravatarUrl)
            }
        }
    }
}

@Composable
fun GravatarDivider() = Divider(thickness = 1.dp, color = Color.Black, modifier = Modifier.padding(vertical = 8.dp))

@Composable
fun GravatarGeneratedUrl(gravatarUrl: String) {
    Text(
        text = stringResource(R.string.gravatar_generated_url_label),
        fontWeight = FontWeight.Bold,
    )
    Text(text = gravatarUrl)
}

@Composable
fun GravatarImage(gravatarUrl: String) = AsyncImage(model = gravatarUrl, contentDescription = null)

@Composable
fun GravatarImageSettings(
    email: String,
    size: Int? = null,
    onEmailChanged: (String) -> Unit,
    onSizeChange: (Int?) -> Unit,
    onLoadGravatarClicked: () -> Unit,
    defaultAvatarImageEnabled: Boolean,
    onDefaultAvatarImageEnabledChanged: (Boolean) -> Unit,
    selectedDefaultAvatarImage: DefaultAvatarImage,
    onDefaultAvatarImageChanged: (DefaultAvatarImage) -> Unit,
    defaultAvatarOptions: List<DefaultAvatarImage>,
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
    TextField(
        value = size?.toString() ?: "",
        onValueChange = { value -> onSizeChange(value.toIntOrNull()) },
        label = { Text(stringResource(R.string.gravatar_size_input_label)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.padding(vertical = 8.dp),
    )
    Button(onClick = onLoadGravatarClicked) { Text(text = "Load Gravatar") }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultAvatarImageDropdown(
    enabled: Boolean,
    onEnabledChanged: (Boolean) -> Unit,
    selectedOption: DefaultAvatarImage,
    onSelectedOptionChange: (DefaultAvatarImage) -> Unit,
    defaultAvatarOptions: List<DefaultAvatarImage>,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Row(modifier = modifier) {
        Checkbox(
            checked = enabled,
            onCheckedChange = {
                if (enabled) {
                    expanded = false
                }
                onEnabledChanged(!enabled)
            },
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            TextField(
                readOnly = true,
                value = selectedOption.style,
                onValueChange = { },
                label = { Text(stringResource(R.string.default_avatar_image_label)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(),
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                defaultAvatarOptions.forEach { selectionOption ->
                    DropdownMenuItem(text = { Text(text = selectionOption.style) }, onClick = {
                        onSelectedOptionChange.invoke(selectionOption)
                        expanded = false
                    })
                }
            }
        }
    }
}

@Composable
fun GravatarEmailInput(email: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    TextField(
        value = email,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.gravatar_email_input_label)) },
        maxLines = 1,
        modifier = modifier,
    )
}
