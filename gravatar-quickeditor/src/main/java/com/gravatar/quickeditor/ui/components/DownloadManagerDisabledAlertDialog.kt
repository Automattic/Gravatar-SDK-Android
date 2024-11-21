package com.gravatar.quickeditor.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gravatar.quickeditor.R

@Composable
internal fun DownloadManagerDisabledAlertDialog(isVisible: Boolean, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            text = { Text(text = stringResource(id = R.string.gravatar_qe_download_manager_disabled_title)) },
            confirmButton = {
                TextButton(
                    onClick = onConfirm,
                ) {
                    Text(text = stringResource(id = R.string.gravatar_qe_permission_rationale_open_settings))
                }
            },
        )
    }
}
