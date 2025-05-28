package com.gravatar.quickeditor.ui.abouteditor.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.gravatar.quickeditor.R
import com.gravatar.ui.GravatarTheme

@Composable
internal fun DiscardChangesAlertDialog(visible: Boolean, onDiscardClicked: () -> Unit, onKeepEditing: () -> Unit) {
    if (visible) {
        GravatarTheme {
            AlertDialog(
                onDismissRequest = onKeepEditing,
                title = {
                    Text(text = stringResource(id = R.string.gravatar_qe_about_discard_changes_dialog_title))
                },
                text = {
                    Text(text = stringResource(R.string.gravatar_qe_about_discard_changes_dialog_message))
                },
                confirmButton = {
                    TextButton(
                        onClick = onDiscardClicked,
                    ) { Text(stringResource(R.string.gravatar_qe_about_discard_changes_dialog_confirm)) }
                },
                dismissButton = {
                    TextButton(
                        onClick = onKeepEditing,
                    ) { Text(stringResource(R.string.gravatar_qe_about_discard_changes_dialog_dismiss)) }
                },
            )
        }
    }
}

@Preview
@Composable
private fun FailedAvatarUploadAlertDialogPreview() {
    DiscardChangesAlertDialog(
        visible = true,
        onDiscardClicked = {},
        onKeepEditing = {},
    )
}
