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
import com.gravatar.quickeditor.ui.editor.AvatarUpdateResult
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorDismissReason
import com.gravatar.quickeditor.ui.editor.bottomsheet.GravatarQuickEditorBottomSheet
import com.gravatar.quickeditor.ui.oauth.OAuthParams
import kotlinx.coroutines.launch

internal fun addQuickEditorToView(
    viewGroup: ViewGroup,
    appName: String,
    oAuthParams: OAuthParams,
    onAvatarUpdateResult: (AvatarUpdateResult) -> Unit,
    onDismiss: (dismissReason: GravatarQuickEditorDismissReason) -> Unit,
) {
    viewGroup.addView(
        ComposeView(viewGroup.context).apply {
            setContent {
                GravatarQuickEditorBottomSheetWrapper(
                    parent = viewGroup,
                    composeView = this,
                    appName = appName,
                    oAuthParams = oAuthParams,
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
    appName: String,
    oAuthParams: OAuthParams,
    onAvatarUpdateResult: (AvatarUpdateResult) -> Unit,
    onDismiss: (dismissReason: GravatarQuickEditorDismissReason) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var isSheetOpened by remember { mutableStateOf(false) }

    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    GravatarQuickEditorBottomSheet(
        appName = appName,
        oAuthParams = oAuthParams,
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
