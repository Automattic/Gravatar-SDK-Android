package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.R
import com.gravatar.ui.GravatarTheme

@Composable
internal fun UploadImageGridButton(
    onChoosePhotoClick: () -> Unit,
    onTakePhotoClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    var popupVisible by remember { androidx.compose.runtime.mutableStateOf(false) }
    Box(modifier = modifier) {
        IconButton(
            onClick = { popupVisible = true },
            enabled = enabled,
            modifier = Modifier
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    RoundedCornerShape(8.dp),
                )
                .aspectRatio(1f),
            colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.gravatar_qe_upload_image_content_description),
            )
        }
        if (popupVisible) {
            MediaPickerPopup(
                anchorAlignment = Alignment.Start,
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

@PreviewLightDark
@Composable
private fun UploadImageGridButtonPreview() {
    GravatarTheme {
        Surface(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(10.dp),
        ) {
            UploadImageGridButton(
                onTakePhotoClick = {},
                onChoosePhotoClick = {},
                enabled = true,
                modifier = Modifier.size(avatarSize),
            )
        }
    }
}
