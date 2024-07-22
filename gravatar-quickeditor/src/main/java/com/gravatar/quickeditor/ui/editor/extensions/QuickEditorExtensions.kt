package com.gravatar.quickeditor.ui.editor.extensions

import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import com.gravatar.quickeditor.ui.bottomsheet.ProfileQuickEditorBottomSheet
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorParams
import kotlinx.coroutines.launch

internal fun addQuickEditorToView(
    viewGroup: ViewGroup,
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    onDismiss: () -> Unit,
) {
    viewGroup.addView(
        ComposeView(viewGroup.context).apply {
            setContent {
                ProfileQuickEditorBottomSheetWrapper(
                    parent = viewGroup,
                    composeView = this,
                    gravatarQuickEditorParams = gravatarQuickEditorParams,
                    onDismiss = onDismiss,
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileQuickEditorBottomSheetWrapper(
    parent: ViewGroup,
    composeView: ComposeView,
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    onDismiss: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var isSheetOpened by remember { mutableStateOf(false) }

    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ProfileQuickEditorBottomSheet(
        gravatarQuickEditorParams = gravatarQuickEditorParams,
        onDismiss = onDismiss,
        modalBottomSheetState = modalBottomSheetState,
    )

    BackHandler {
        coroutineScope.launch {
            modalBottomSheetState.hide()
            onDismiss()
        }
    }

    LaunchedEffect(modalBottomSheetState.currentValue) {
        when (modalBottomSheetState.currentValue) {
            SheetValue.Hidden -> {
                if (isSheetOpened) {
                    parent.removeView(composeView)
                } else {
                    isSheetOpened = true
                    modalBottomSheetState.show()
                }
            }

            else -> Unit
        }
    }
}
