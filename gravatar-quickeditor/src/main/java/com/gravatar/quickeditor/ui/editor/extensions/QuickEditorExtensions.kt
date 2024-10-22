package com.gravatar.quickeditor.ui.editor.extensions

import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ComposeView
import com.composables.core.SheetDetent.Companion.Hidden
import com.gravatar.quickeditor.ui.editor.AuthenticationMethod
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorDismissReason
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorParams
import com.gravatar.quickeditor.ui.editor.bottomsheet.GravatarQuickEditorBottomSheet
import com.gravatar.quickeditor.ui.editor.bottomsheet.rememberGravatarModalBottomSheetState
import kotlinx.coroutines.launch

internal fun addQuickEditorToView(
    viewGroup: ViewGroup,
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    authenticationMethod: AuthenticationMethod,
    onAvatarUpdate: () -> Unit,
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
                    onAvatarUpdate = onAvatarUpdate,
                    onDismiss = onDismiss,
                )
            }
        },
    )
}

@Composable
private fun GravatarQuickEditorBottomSheetWrapper(
    parent: ViewGroup,
    composeView: ComposeView,
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    authenticationMethod: AuthenticationMethod,
    onAvatarUpdate: () -> Unit,
    onDismiss: (dismissReason: GravatarQuickEditorDismissReason) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val modalBottomSheetState = rememberGravatarModalBottomSheetState(
        avatarPickerContentLayout = gravatarQuickEditorParams.avatarPickerContentLayout,
    )

    GravatarQuickEditorBottomSheet(
        gravatarQuickEditorParams = gravatarQuickEditorParams,
        authenticationMethod = authenticationMethod,
        onAvatarSelected = onAvatarUpdate,
        onDismiss = onDismiss,
        modalBottomSheetState = modalBottomSheetState,
    )

    BackHandler {
        coroutineScope.launch {
            modalBottomSheetState.currentDetent = Hidden
        }
    }

    LaunchedEffect(modalBottomSheetState.currentDetent) {
        when (modalBottomSheetState.currentDetent) {
            Hidden -> {
                parent.removeView(composeView)
            }
        }
    }
}
