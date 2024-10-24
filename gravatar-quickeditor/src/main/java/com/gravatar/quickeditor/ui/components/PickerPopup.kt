package com.gravatar.quickeditor.ui.components

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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
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
    onDismissRequest: () -> Unit,
    popupButtons: List<@Composable () -> Unit>,
) {
    PickerPopup(
        anchorAlignment = anchorAlignment,
        anchorBounds = anchorBounds,
        onDismissRequest = onDismissRequest,
        popupButtons = popupButtons,
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
    onDismissRequest: () -> Unit,
    popupButtons: List<@Composable () -> Unit>,
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
                        LazyColumn {
                            itemsIndexed(popupButtons) { index, item ->
                                item()
                                if (index < popupButtons.size - 1) {
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

@Composable
private fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

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
