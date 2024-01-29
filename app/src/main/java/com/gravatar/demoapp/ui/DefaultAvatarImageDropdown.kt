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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> GravatarSettingDropdown(
    enabled: Boolean,
    onEnabledChanged: (Boolean) -> Unit,
    selectedOption: T,
    onSelectedOptionChange: (T) -> Unit,
    options: List<T>,
    labelForOption: (T) -> String,
    inputLabel: String,
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
                value = labelForOption(selectedOption),
                onValueChange = { },
                label = { Text(inputLabel) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(text = { Text(text = labelForOption(selectionOption)) }, onClick = {
                        onSelectedOptionChange.invoke(selectionOption)
                        expanded = false
                    })
                }
            }
        }
    }
}
