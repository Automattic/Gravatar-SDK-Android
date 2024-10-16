package com.gravatar.quickeditor.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gravatar.quickeditor.R

@Composable
internal fun CameraPermissionRationaleDialog(
    isVisible: Boolean,
    onConfirmation: () -> Unit,
    onDismiss: () -> Unit = {},
) {
    if (isVisible) {
        AlertDialog(
            title = {
                Text(text = stringResource(R.string.gravatar_qe_permission_required_alert_title))
            },
            text = {
                Text(
                    text = stringResource(R.string.gravatar_qe_camera_permission_rationale_message),
                )
            },
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmation()
                    },
                ) {
                    Text(stringResource(R.string.gravatar_qe_dismiss))
                }
            },
        )
    }
}
