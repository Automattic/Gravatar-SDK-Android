package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.R
import com.gravatar.ui.GravatarTheme

@Composable
internal fun AvatarMoreOptionsPickerPopup(
    anchorAlignment: Alignment.Horizontal,
    anchorBounds: Rect,
    onDismissRequest: () -> Unit,
    onAltTextClick: () -> Unit,
) {
    PickerPopup(
        anchorAlignment = anchorAlignment,
        anchorBounds = anchorBounds,
        onDismissRequest = onDismissRequest,
        popupButtons = listOf {
            PopupButton(
                text = stringResource(R.string.gravatar_qe_selectable_avatar_more_options_alt_text),
                iconRes = R.drawable.gravatar_avatar_more_options_alt_text,
                contentDescription = stringResource(
                    R.string.gravatar_qe_selectable_avatar_more_options_alt_text,
                ),
                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                onClick = onAltTextClick,
            )
        },
    )
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
                onAltTextClick = {},
            )
        }
    }
}
