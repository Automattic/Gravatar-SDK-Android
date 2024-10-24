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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.composables.core.Dialog
import com.composables.core.DialogPanel
import com.composables.core.Scrim
import com.composables.core.rememberDialogState
import com.gravatar.quickeditor.R
import com.gravatar.ui.GravatarTheme

@Composable
internal fun MediaPickerPopup(
    anchorAlignment: Alignment.Horizontal,
    anchorBounds: Rect,
    onDismissRequest: () -> Unit,
    onChoosePhotoClick: () -> Unit,
    onTakePhotoClick: () -> Unit,
) {
    MediaPickerPopup(
        anchorAlignment = anchorAlignment,
        anchorBounds = anchorBounds,
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
    anchorAlignment: Alignment.Horizontal,
    anchorBounds: Rect,
    onDismissRequest: () -> Unit,
    onChoosePhotoClick: () -> Unit,
    onTakePhotoClick: () -> Unit,
    state: MutableTransitionState<Boolean>,
) {
    val cornerRadius = 8.dp
    // full screen background
    Dialog(state = rememberDialogState(initiallyVisible = true)) {
        Scrim(scrimColor = Color.Black.copy(alpha = 0.2f))
        DialogPanel(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp.dp.dpToPx()
            var popupSize by remember { mutableStateOf(IntSize.Zero) }

            Popup(
                alignment = Alignment.TopStart,
                onDismissRequest = onDismissRequest,
                offset = IntOffset(
                    calculatePopupXOffset(anchorAlignment, anchorBounds, popupSize, screenWidth),
                    (anchorBounds.top - popupSize.height - 10.dp.dpToPx()).toInt(),
                ),
                properties = PopupProperties(focusable = true),
            ) {
                AnimatedVisibility(
                    visibleState = state,
                    enter = scaleIn(animationSpec = spring(stiffness = Spring.StiffnessMedium)),
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .onGloballyPositioned {
                                popupSize = it.size
                            },
                        shape = RoundedCornerShape(cornerRadius),
                        tonalElevation = 3.dp,
                        shadowElevation = 2.dp,
                    ) {
                        Column {
                            PopupButton(
                                text = stringResource(R.string.gravatar_qe_avatar_picker_choose_a_photo),
                                iconRes = R.drawable.gravatar_photo_library,
                                contentDescription = stringResource(
                                    R.string.gravatar_qe_photo_library_icon_description,
                                ),
                                shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius),
                                onClick = onChoosePhotoClick,
                            )
                            HorizontalDivider()
                            PopupButton(
                                text = stringResource(R.string.gravatar_qe_avatar_picker_take_photo),
                                iconRes = R.drawable.gravatar_capture_photo,
                                contentDescription = stringResource(
                                    R.string.gravatar_qe_capture_photo_icon_description,
                                ),
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

private fun calculatePopupXOffset(
    anchorAlignment: Alignment.Horizontal,
    anchorBounds: Rect,
    popupSize: IntSize,
    screenWidth: Float,
): Int {
    return when (anchorAlignment) {
        Alignment.Start -> {
            anchorBounds.left.toInt()
        }
        // Default to Alignment.CenterHorizontally
        else -> {
            ((screenWidth - popupSize.width) / 2).toInt()
        }
    }
}

@Composable
private fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

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
                onDismissRequest = {},
                anchorBounds = Rect(Offset(0f, 300f), Size(1f, 1f)),
                onChoosePhotoClick = {},
                onTakePhotoClick = {},
                state = remember { MutableTransitionState(true) },
            )
        }
    }
}
