package com.gravatar.quickeditor.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties

@Composable
internal fun PickerPopup(
    anchorAlignment: Alignment.Horizontal,
    offset: DpOffset,
    onDismissRequest: () -> Unit,
    popupMenu: PickerPopupMenu,
) {
    PickerPopup(
        anchorAlignment = anchorAlignment,
        dpOffset = offset,
        onDismissRequest = onDismissRequest,
        popupMenu = popupMenu,
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
    dpOffset: DpOffset,
    onDismissRequest: () -> Unit,
    popupMenu: PickerPopupMenu,
    state: MutableTransitionState<Boolean>,
) {
    val density = LocalDensity.current
    val positionProvider = remember(density) { PickerPopupPositionProvider(density, anchorAlignment, dpOffset) }
    val cornerRadius = 8.dp
    var popupMenuState by remember { mutableStateOf(popupMenu) }

    Popup(
        onDismissRequest = onDismissRequest,
        popupPositionProvider = positionProvider,
        properties = PopupProperties(focusable = true),
    ) {
        AnimatedVisibility(
            visibleState = state,
            enter = scaleIn(animationSpec = spring(stiffness = Spring.StiffnessMedium)),
        ) {
            AnimatedContent(
                targetState = popupMenuState,
                label = "PickerPopup",
            ) { targetState ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.6f),
                    shape = RoundedCornerShape(cornerRadius),
                    tonalElevation = 3.dp,
                    shadowElevation = 2.dp,
                ) {
                    LazyColumn {
                        itemsIndexed(targetState.items) { index, item ->
                            PopupButton(
                                text = item.text,
                                iconRes = item.iconRes,
                                contentDescription = stringResource(item.contentDescription),
                                shape = popupButtonShape(index, popupMenu.items.size, cornerRadius),
                                color = item.contentColor,
                                onClick = {
                                    if (item.subMenu != null) {
                                        popupMenuState = item.subMenu
                                    } else {
                                        item.onClick?.let { it() }
                                    }
                                },
                            )
                            if (index < popupMenu.items.size - 1) {
                                HorizontalDivider()
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

internal data class PickerPopupMenu(
    val items: List<PickerPopupItem>,
)

internal data class PickerPopupItem(
    val text: String,
    @DrawableRes val iconRes: Int?,
    @StringRes val contentDescription: Int,
    val onClick: (() -> Unit)? = null,
    val contentColor: Color? = null,
    val subMenu: PickerPopupMenu? = null,
)

// Code modified from Compose-Unstyled Menu.kt to prioritize positioning above the anchor
// https://github.com/composablehorizons/compose-unstyled/blob/c62bab5babdabceb634ec66de05f5370c161b66d/core/src/commonMain/kotlin/Menu.kt
@Immutable
internal data class PickerPopupPositionProvider(
    val density: Density,
    val alignment: Alignment.Horizontal,
    val offset: DpOffset = DpOffset.Zero,
    val displayPadding: Dp = 16.dp,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val intOffset: IntOffset = with(density) {
            IntOffset(offset.x.toPx().toInt(), offset.y.toPx().toInt())
        }

        val displayPadding = with(density) { displayPadding.toPx().toInt() }

        // Compute horizontal position.
        val toRight = anchorBounds.left
        val toLeft = anchorBounds.right - popupContentSize.width

        val toDisplayRight = windowSize.width - popupContentSize.width - displayPadding
        val toDisplayLeft = 0 + displayPadding

        val x = (
            if (alignment == Alignment.Start) {
                sequenceOf(
                    toRight,
                    toLeft,
                    // If the anchor gets outside of the window on the left, we want to position
                    // toDisplayLeft for proximity to the anchor. Otherwise, toDisplayRight.
                    if (anchorBounds.left >= displayPadding) toDisplayRight else toDisplayLeft,
                )
            } else if (alignment == Alignment.End) {
                sequenceOf(
                    toLeft,
                    toRight,
                    // If the anchor gets outside of the window on the right, we want to position
                    // toDisplayRight for proximity to the anchor. Otherwise, toDisplayLeft.
                    if (anchorBounds.right <= windowSize.width - displayPadding) toDisplayLeft else toDisplayRight,
                )
            } else { // middle
                sequenceOf(anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2)
            }
        ).firstOrNull {
            it >= displayPadding && it + popupContentSize.width <= windowSize.width - displayPadding
        } ?: toLeft

        // Compute vertical position.
        val toBottom = maxOf(anchorBounds.bottom + intOffset.y, 0)
        val toTop = anchorBounds.top - intOffset.y - popupContentSize.height
        val toCenter = anchorBounds.top - intOffset.y - popupContentSize.height / 2
        val toDisplayBottom = windowSize.height - intOffset.y - popupContentSize.height
        val y = sequenceOf(toTop, toBottom, toCenter, toDisplayBottom).firstOrNull {
            it >= 0 && it + popupContentSize.height <= windowSize.height
        } ?: toTop

        return IntOffset(x, y)
    }
}
