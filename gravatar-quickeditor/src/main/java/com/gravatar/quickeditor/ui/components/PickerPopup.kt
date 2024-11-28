package com.gravatar.quickeditor.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
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

@Composable
internal fun PickerPopup(
    anchorAlignment: Alignment.Horizontal,
    anchorBounds: Rect,
    popupDrawArea: Rect? = null,
    onDismissRequest: () -> Unit,
    popupItems: List<PickerPopupItem>,
) {
    PickerPopup(
        anchorAlignment = anchorAlignment,
        anchorBounds = anchorBounds,
        popupDrawArea = popupDrawArea,
        onDismissRequest = onDismissRequest,
        popupItems = popupItems,
        state = remember {
            MutableTransitionState(false).apply {
                // Start the animation immediately.
                targetState = true
            }
        },
    )
}

@Composable
private fun PickerPopup(
    anchorAlignment: Alignment.Horizontal,
    anchorBounds: Rect,
    popupDrawArea: Rect? = null,
    onDismissRequest: () -> Unit,
    popupItems: List<PickerPopupItem>,
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
            var popupSize by remember { mutableStateOf(IntSize.Zero) }

            Popup(
                alignment = Alignment.TopStart,
                onDismissRequest = onDismissRequest,
                offset = IntOffset(
                    calculatePopupXOffset(anchorAlignment, anchorBounds, popupDrawArea, popupSize),
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
                        LazyColumn {
                            itemsIndexed(popupItems) { index, item ->
                                PopupButton(
                                    text = stringResource(item.text),
                                    iconRes = item.iconRes,
                                    contentDescription = stringResource(item.contentDescription),
                                    shape = popupButtonShape(index, popupItems.size, cornerRadius),
                                    color = item.color,
                                    onClick = item.onClick,
                                )
                                if (index < popupItems.size - 1) {
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun popupButtonShape(index: Int, totalItems: Int, cornerRadius: Dp): RoundedCornerShape {
    return when (index) {
        0 -> if (totalItems == 1) {
            RoundedCornerShape(cornerRadius)
        } else {
            RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)
        }

        totalItems - 1 -> RoundedCornerShape(bottomStart = cornerRadius, bottomEnd = cornerRadius)
        else -> RoundedCornerShape(0.dp)
    }
}

@Composable
private fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

internal data class PickerPopupItem(
    @StringRes val text: Int,
    @DrawableRes val iconRes: Int,
    @StringRes val contentDescription: Int,
    val color: Color? = null,
    val onClick: () -> Unit,
)

private fun calculatePopupXOffset(
    anchorAlignment: Alignment.Horizontal,
    anchorBounds: Rect,
    maxBounds: Rect? = null,
    popupSize: IntSize,
): Int {
    val offset = when (anchorAlignment) {
        Alignment.Start -> anchorBounds.left.toInt()
        Alignment.End -> (anchorBounds.right - popupSize.width).toInt()
        else -> (anchorBounds.left.toInt() + anchorBounds.width.toInt() / 2) - (popupSize.width / 2)
    }

    return maxBounds?.let { bounds ->
        val minimumValue = bounds.left
        val maximumValue = (bounds.right - popupSize.width).coerceAtLeast(minimumValue)
        offset.coerceIn(minimumValue.toInt(), maximumValue.toInt())
    } ?: offset
}
