package com.gravatar.demoapp.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.gravatar.DefaultAvatarImage
import com.gravatar.R

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
            modifier = Modifier.fillMaxWidth(),
        ) {
            TextField(
                readOnly = true,
                value = selectedOption.style,
                onValueChange = { },
                label = { Text(stringResource(R.string.default_avatar_image_label)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
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
