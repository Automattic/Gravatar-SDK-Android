package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.R
import com.gravatar.ui.GravatarTheme

@Composable
internal fun AvatarMoreOptionsPickerPopup(
    anchorAlignment: Alignment.Horizontal,
    anchorBounds: Rect,
    onDismissRequest: () -> Unit,
    onAvatarOptionClicked: (AvatarOption) -> Unit,
) {
    PickerPopup(
        anchorAlignment = anchorAlignment,
        anchorBounds = anchorBounds,
        onDismissRequest = onDismissRequest,
        popupItems = listOf(
            PickerPopupItem(
                text = R.string.gravatar_qe_selectable_avatar_more_options_alt_text,
                iconRes = R.drawable.gravatar_avatar_more_options_alt_text,
                contentDescription = R.string.gravatar_qe_selectable_avatar_more_options_alt_text_content_description,
                onClick = {
                    onAvatarOptionClicked(AvatarOption.ALT_TEXT)
                },
            ),
            PickerPopupItem(
                text = R.string.gravatar_qe_selectable_avatar_more_options_download_image,
                iconRes = R.drawable.gravatar_avatar_more_options_download,
                contentDescription = R.string.gravatar_qe_selectable_avatar_more_options_download_image,
                onClick = {
                    onAvatarOptionClicked(AvatarOption.DOWNLOAD_IMAGE)
                },
            ),
            PickerPopupItem(
                text = R.string.gravatar_qe_selectable_avatar_more_options_delete,
                iconRes = R.drawable.gravatar_avatar_more_options_delete,
                contentDescription = R.string.gravatar_qe_selectable_avatar_more_options_delete_content_description,
                color = MaterialTheme.colorScheme.error,
                onClick = {
                    onAvatarOptionClicked(AvatarOption.DELETE)
                },
            ),
        ),
    )
}

internal enum class AvatarOption {
    ALT_TEXT,
    DELETE,
    DOWNLOAD_IMAGE,
}

@Preview
@Composable
private fun AvatarMoreOptionsPickerPopupPreview() {
    GravatarTheme {
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(MaterialTheme.colorScheme.background),
        ) {
            AvatarMoreOptionsPickerPopup(
                anchorAlignment = Alignment.Start,
                onDismissRequest = {},
                anchorBounds = Rect(Offset(0f, 300f), Size(1f, 1f)),
                onAvatarOptionClicked = {},
            )
        }
    }
}
