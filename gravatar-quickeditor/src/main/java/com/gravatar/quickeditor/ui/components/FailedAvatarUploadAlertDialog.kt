package com.gravatar.quickeditor.ui.components

import android.net.Uri
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.gravatar.quickeditor.R
import com.gravatar.quickeditor.ui.avatarpicker.AvatarUploadFailure
import com.gravatar.services.ErrorType
import com.gravatar.ui.GravatarTheme

@Composable
internal fun FailedAvatarUploadAlertDialog(
    avatarUploadFailure: AvatarUploadFailure?,
    onRemoveUploadClicked: (Uri) -> Unit,
    onRetryClicked: (Uri) -> Unit,
    onDismiss: () -> Unit,
) {
    if (avatarUploadFailure != null) {
        GravatarTheme {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = {
                    Text(text = stringResource(id = R.string.gravatar_qe_avatar_upload_failure_dialog_title))
                },
                text = {
                    avatarUploadFailure.error?.let {
                        when (it) {
                            is ErrorType.InvalidRequest -> it.error?.error
                            is ErrorType.ContentLengthExceeded -> stringResource(
                                id = R.string.gravatar_qe_avatar_upload_failure_image_too_big,
                            )
                            else -> null
                        }?.let { errorString -> Text(text = errorString) }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { onRetryClicked(avatarUploadFailure.uri) },
                    ) { Text(stringResource(R.string.gravatar_qe_avatar_upload_error_action)) }
                },
                dismissButton = {
                    TextButton(
                        onClick = { onRemoveUploadClicked(avatarUploadFailure.uri) },
                    ) { Text(stringResource(R.string.gravatar_qe_avatar_upload_failure_dialog_remove_upload)) }
                },
            )
        }
    }
}

@Preview
@Composable
private fun FailedAvatarUploadAlertDialogPreview() {
    FailedAvatarUploadAlertDialog(
        avatarUploadFailure = AvatarUploadFailure(Uri.EMPTY, ErrorType.ContentLengthExceeded),
        onRemoveUploadClicked = {},
        onRetryClicked = {},
        onDismiss = {},
    )
}
