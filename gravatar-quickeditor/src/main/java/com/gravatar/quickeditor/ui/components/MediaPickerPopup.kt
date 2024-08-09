package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.gravatar.quickeditor.R
import com.gravatar.ui.GravatarTheme

@Composable
internal fun MediaPickerPopup(
    alignment: Alignment,
    offset: IntOffset,
    onDismissRequest: () -> Unit,
    onChoosePhotoClick: () -> Unit,
    onTakePhotoClick: () -> Unit,
) {
    val cornerRadius = 8.dp
    Popup(
        alignment = alignment,
        onDismissRequest = onDismissRequest,
        offset = offset,
    ) {
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(cornerRadius))
                .fillMaxWidth(0.6f),
        ) {
            Column {
                PopupButton(
                    text = stringResource(R.string.avatar_picker_choose_a_photo),
                    iconRes = R.drawable.photo_library,
                    contentDescription = stringResource(R.string.photo_library_icon_description),
                    shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius),
                    onClick = onChoosePhotoClick,
                )
                HorizontalDivider()
                PopupButton(
                    text = stringResource(R.string.avatar_picker_take_photo),
                    iconRes = R.drawable.capture_photo,
                    contentDescription = stringResource(R.string.capture_photo_icon_description),
                    shape = RoundedCornerShape(bottomStart = cornerRadius, bottomEnd = cornerRadius),
                    onClick = onTakePhotoClick,
                )
            }
        }
    }
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
                alignment = Alignment.BottomCenter,
                onDismissRequest = {},
                offset = IntOffset(0, -100),
                onChoosePhotoClick = {},
                onTakePhotoClick = {},
            )
        }
    }
}
