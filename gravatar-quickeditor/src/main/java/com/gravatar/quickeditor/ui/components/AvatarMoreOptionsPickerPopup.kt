package com.gravatar.quickeditor.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.R
import com.gravatar.quickeditor.ui.avatarpicker.AvatarRating
import com.gravatar.restapi.models.Avatar
import com.gravatar.ui.GravatarTheme

@Composable
internal fun AvatarMoreOptionsPickerPopup(
    avatarRating: List<AvatarRating>,
    anchorAlignment: Alignment.Horizontal,
    offset: DpOffset,
    onDismissRequest: () -> Unit,
    onAvatarOptionClicked: (AvatarOption) -> Unit,
) {
    PickerPopup(
        anchorAlignment = anchorAlignment,
        offset = offset,
        onDismissRequest = onDismissRequest,
        popupMenu = PickerPopupMenu(
            items = listOf(
                PickerPopupItem(
                    text = stringResource(R.string.gravatar_qe_selectable_avatar_more_options_alt_text),
                    iconRes = R.drawable.gravatar_avatar_more_options_alt_text,
                    contentDescription =
                        R.string.gravatar_qe_selectable_avatar_more_options_alt_text_content_description,
                    onClick = {
                        onAvatarOptionClicked(AvatarOption.AltText)
                    },
                ),
                PickerPopupItem(
                    text = stringResource(R.string.gravatar_qe_selectable_avatar_more_options_rating),
                    iconRes = R.drawable.gravatar_avatar_more_options_rating,
                    contentDescription = R.string.gravatar_qe_selectable_avatar_more_options_rating,
                    subMenu = PickerPopupMenu(
                        items = avatarRating.map { (rating, selected) ->
                            PickerPopupItem(
                                text = stringResource(rating.fullNameRes),
                                iconRes = if (selected) R.drawable.ic_checkmark else null,
                                contentDescription = R.string.gravatar_qe_avatar_rating_selected_content_description,
                                onClick = {
                                    onAvatarOptionClicked(AvatarOption.Rating(rating))
                                },
                            )
                        },
                    ),
                ),
                PickerPopupItem(
                    text = stringResource(R.string.gravatar_qe_selectable_avatar_more_options_download_image),
                    iconRes = R.drawable.gravatar_avatar_more_options_download,
                    contentDescription = R.string.gravatar_qe_selectable_avatar_more_options_download_image,
                    onClick = {
                        onAvatarOptionClicked(AvatarOption.DownloadImage)
                    },
                ),
                PickerPopupItem(
                    text = stringResource(R.string.gravatar_qe_selectable_avatar_more_options_delete),
                    iconRes = R.drawable.gravatar_avatar_more_options_delete,
                    contentDescription = R.string.gravatar_qe_selectable_avatar_more_options_delete_content_description,
                    contentColor = MaterialTheme.colorScheme.error,
                    onClick = {
                        onAvatarOptionClicked(AvatarOption.Delete)
                    },
                ),
            ),
        ),
    )
}

internal sealed class AvatarOption {
    data object AltText : AvatarOption()

    data class Rating(val rating: Avatar.Rating) : AvatarOption()

    data object Delete : AvatarOption()

    data object DownloadImage : AvatarOption()
}

private val Avatar.Rating.fullNameRes: Int
    @StringRes get() = when (this) {
        Avatar.Rating.G -> R.string.gravatar_qe_avatar_rating_g
        Avatar.Rating.PG -> R.string.gravatar_qe_avatar_rating_pg
        Avatar.Rating.R -> R.string.gravatar_qe_avatar_rating_r
        Avatar.Rating.X -> R.string.gravatar_qe_avatar_rating_x
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
                avatarRating = emptyList(),
                anchorAlignment = Alignment.Start,
                offset = DpOffset.Zero,
                onDismissRequest = {},
                onAvatarOptionClicked = {},
            )
        }
    }
}
