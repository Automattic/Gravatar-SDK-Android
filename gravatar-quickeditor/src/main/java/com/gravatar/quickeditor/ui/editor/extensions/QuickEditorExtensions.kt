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
import com.gravatar.quickeditor.ui.editor.AuthenticationMethod
import com.gravatar.quickeditor.ui.editor.AvatarUpdateResult
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorDismissReason
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorParams
import com.gravatar.quickeditor.ui.editor.bottomsheet.GravatarQuickEditorBottomSheet
import kotlinx.coroutines.launch

internal fun addQuickEditorToView(
    viewGroup: ViewGroup,
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    authenticationMethod: AuthenticationMethod,
    onAvatarUpdateResult: (AvatarUpdateResult) -> Unit,
    onDismiss: (dismissReason: GravatarQuickEditorDismissReason) -> Unit,
) {
    viewGroup.addView(
        ComposeView(viewGroup.context).apply {
            setContent {
                GravatarQuickEditorBottomSheetWrapper(
                    parent = viewGroup,
                    composeView = this,
                    gravatarQuickEditorParams = gravatarQuickEditorParams,
                    authenticationMethod = authenticationMethod,
                    onAvatarUpdateResult = onAvatarUpdateResult,
                    onDismiss = onDismiss,
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GravatarQuickEditorBottomSheetWrapper(
    parent: ViewGroup,
    composeView: ComposeView,
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    authenticationMethod: AuthenticationMethod,
    onAvatarUpdateResult: (AvatarUpdateResult) -> Unit,
    onDismiss: (dismissReason: GravatarQuickEditorDismissReason) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var isSheetOpened by remember { mutableStateOf(false) }

    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    GravatarQuickEditorBottomSheet(
        gravatarQuickEditorParams = gravatarQuickEditorParams,
        authenticationMethod = authenticationMethod,
        onAvatarSelected = onAvatarUpdateResult,
        onDismiss = onDismiss,
        modalBottomSheetState = modalBottomSheetState,
    )

    BackHandler {
        coroutineScope.launch {
            modalBottomSheetState.hide()
        }
        onDismiss(GravatarQuickEditorDismissReason.Finished)
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
