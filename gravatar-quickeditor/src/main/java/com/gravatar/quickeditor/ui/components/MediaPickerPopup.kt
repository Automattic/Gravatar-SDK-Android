package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.R
import com.gravatar.ui.GravatarTheme

@Composable
internal fun MediaPickerPopup(
    anchorAlignment: Alignment.Horizontal,
    offset: DpOffset,
    onDismissRequest: () -> Unit,
    onChoosePhotoClick: () -> Unit,
    onTakePhotoClick: () -> Unit,
) {
    PickerPopup(
        anchorAlignment = anchorAlignment,
        onDismissRequest = onDismissRequest,
        offset = offset,
        popupItems = listOf(
            PickerPopupItem(
                text = R.string.gravatar_qe_avatar_picker_choose_a_photo,
                iconRes = R.drawable.gravatar_photo_library,
                contentDescription = R.string.gravatar_qe_photo_library_icon_description,
                onClick = onChoosePhotoClick,
            ),
            PickerPopupItem(
                text = R.string.gravatar_qe_avatar_picker_take_photo,
                iconRes = R.drawable.gravatar_capture_photo,
                contentDescription = R.string.gravatar_qe_capture_photo_icon_description,
                onClick = onTakePhotoClick,
            ),
        ),
    )
}

@Preview
@Composable
private fun MediaPickerPopupPreview() {
    GravatarTheme {
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(MaterialTheme.colorScheme.background),
        ) {
            MediaPickerPopup(
                anchorAlignment = Alignment.Start,
                offset = DpOffset.Zero,
                onDismissRequest = {},
                onChoosePhotoClick = {},
                onTakePhotoClick = {},
            )
        }
    }
}
