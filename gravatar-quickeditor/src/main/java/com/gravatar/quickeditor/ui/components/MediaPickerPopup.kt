package com.gravatar.quickeditor.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
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
    MediaPickerPopup(
        alignment = alignment,
        offset = offset,
        onDismissRequest = onDismissRequest,
        onChoosePhotoClick = onChoosePhotoClick,
        onTakePhotoClick = onTakePhotoClick,
        state = remember {
            MutableTransitionState(false).apply {
                // Start the animation immediately.
                targetState = true
            }
        },
    )
}

@Composable
private fun MediaPickerPopup(
    alignment: Alignment,
    offset: IntOffset,
    onDismissRequest: () -> Unit,
    onChoosePhotoClick: () -> Unit,
    onTakePhotoClick: () -> Unit,
    state: MutableTransitionState<Boolean>,
) {
    val cornerRadius = 8.dp
    // full screen background
    Dialog(
        onDismissRequest = {},
        DialogProperties(
            usePlatformDefaultWidth = false,
        ),
    ) {
        (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(0.2f)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(10F),
            contentAlignment = Alignment.Center,
        ) {
            Popup(
                alignment = alignment,
                onDismissRequest = onDismissRequest,
                offset = offset,
                properties = PopupProperties(focusable = true),
            ) {
                AnimatedVisibility(
                    visibleState = state,
                    enter = scaleIn(animationSpec = spring(stiffness = Spring.StiffnessMedium)),
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.6f),
                        shape = RoundedCornerShape(cornerRadius),
                        tonalElevation = 3.dp,
                        shadowElevation = 2.dp,
                    ) {
                        Column {
                            PopupButton(
                                text = stringResource(R.string.gravatar_avatar_picker_choose_a_photo),
                                iconRes = R.drawable.gravatar_photo_library,
                                contentDescription = stringResource(R.string.gravatar_photo_library_icon_description),
                                shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius),
                                onClick = onChoosePhotoClick,
                            )
                            HorizontalDivider()
                            PopupButton(
                                text = stringResource(R.string.gravatar_avatar_picker_take_photo),
                                iconRes = R.drawable.gravatar_capture_photo,
                                contentDescription = stringResource(R.string.gravatar_capture_photo_icon_description),
                                shape = RoundedCornerShape(bottomStart = cornerRadius, bottomEnd = cornerRadius),
                                onClick = onTakePhotoClick,
                            )
                        }
                    }
                }
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
                alignment = Alignment.TopStart,
                onDismissRequest = {},
                offset = IntOffset(0, 0),
                onChoosePhotoClick = {},
                onTakePhotoClick = {},
                state = remember { MutableTransitionState(true) },
            )
        }
    }
}
