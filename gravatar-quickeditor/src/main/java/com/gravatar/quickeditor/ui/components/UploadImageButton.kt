package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.R
import com.gravatar.ui.GravatarTheme

@Composable
internal fun UploadImageButton(
    onChoosePhotoClick: () -> Unit,
    onTakePhotoClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    var popupVisible by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        QEButton(
            buttonText = stringResource(id = R.string.gravatar_qe_avatar_picker_upload_image),
            onClick = { popupVisible = true },
            enabled = enabled,
            modifier = modifier,
        )
        if (popupVisible) {
            MediaPickerPopup(
                anchorAlignment = Alignment.CenterHorizontally,
                offset = DpOffset(0.dp, 10.dp),
                onDismissRequest = { popupVisible = false },
                onChoosePhotoClick = {
                    popupVisible = false
                    onChoosePhotoClick()
                },
                onTakePhotoClick = {
                    popupVisible = false
                    onTakePhotoClick()
                },
            )
        }
    }
}

@Preview(widthDp = 360, heightDp = 200)
@Composable
private fun UploadImageButtonPreview() {
    GravatarTheme {
        UploadImageButton(
            onChoosePhotoClick = {},
            onTakePhotoClick = {},
            enabled = true,
        )
    }
}
