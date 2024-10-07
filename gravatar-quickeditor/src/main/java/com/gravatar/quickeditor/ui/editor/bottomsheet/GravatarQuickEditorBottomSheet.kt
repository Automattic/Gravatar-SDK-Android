package com.gravatar.quickeditor.ui.editor.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowHeightSizeClass
import com.gravatar.quickeditor.ui.components.QEDragHandle
import com.gravatar.quickeditor.ui.components.QETopBar
import com.gravatar.quickeditor.ui.editor.AuthenticationMethod
import com.gravatar.quickeditor.ui.editor.AvatarPickerContentLayout
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorDismissReason
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorPage
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorParams
import com.gravatar.ui.GravatarTheme
import kotlinx.coroutines.launch

/**
 * ModalBottomSheet component for the Gravatar Quick Editor that enables the user to
 * modify their Avatar.
 *
 * The bottom sheet is configured to take 70% of the screen height and skips the partially expanded state.
 *
 * @param gravatarQuickEditorParams The Quick Editor parameters.
 * @param authenticationMethod The method used for authentication with the Gravatar REST API.
 * @param onAvatarSelected The callback for the avatar update.
 *                       Can be invoked multiple times while the Quick Editor is open.
 * @param onDismiss The callback for the dismiss action.
 *                  [GravatarQuickEditorError] will be non-null if the dismiss was caused by an error.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun GravatarQuickEditorBottomSheet(
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    authenticationMethod: AuthenticationMethod,
    onAvatarSelected: () -> Unit,
    onDismiss: (dismissReason: GravatarQuickEditorDismissReason) -> Unit = {},
) {
    val windowHeightSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowHeightSizeClass
    GravatarQuickEditorBottomSheet(
        gravatarQuickEditorParams = gravatarQuickEditorParams,
        authenticationMethod = authenticationMethod,
        onAvatarSelected = onAvatarSelected,
        onDismiss = onDismiss,
        modalBottomSheetState = if (windowHeightSizeClass == WindowHeightSizeClass.COMPACT) {
            rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
            )
        } else {
            rememberModalBottomSheetState(
                skipPartiallyExpanded =
                    gravatarQuickEditorParams.avatarPickerContentLayout == AvatarPickerContentLayout.Horizontal,
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GravatarQuickEditorBottomSheet(
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    authenticationMethod: AuthenticationMethod,
    onAvatarSelected: () -> Unit,
    onDismiss: (dismissReason: GravatarQuickEditorDismissReason) -> Unit = {},
    modalBottomSheetState: SheetState,
) {
    GravatarModalBottomSheet(
        onDismiss = onDismiss,
        modalBottomSheetState = modalBottomSheetState,
    ) {
        when (authenticationMethod) {
            is AuthenticationMethod.Bearer -> {
                GravatarQuickEditorPage(
                    gravatarQuickEditorParams = gravatarQuickEditorParams,
                    authToken = authenticationMethod.token,
                    onDismiss = onDismiss,
                    onAvatarSelected = onAvatarSelected,
                )
            }

            is AuthenticationMethod.OAuth -> {
                GravatarQuickEditorPage(
                    gravatarQuickEditorParams = gravatarQuickEditorParams,
                    oAuthParams = authenticationMethod.oAuthParams,
                    onDismiss = onDismiss,
                    onAvatarSelected = onAvatarSelected,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GravatarModalBottomSheet(
    onDismiss: (dismissReason: GravatarQuickEditorDismissReason) -> Unit = {},
    modalBottomSheetState: SheetState,
    content: @Composable () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    GravatarTheme {
        ModalBottomSheet(
            onDismissRequest = { onDismiss(GravatarQuickEditorDismissReason.Finished) },
            sheetState = modalBottomSheetState,
            dragHandle = { QEDragHandle() },
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
        ) {
            Surface(
                modifier = Modifier.navigationBarsPadding(),
            ) {
                Column {
                    QETopBar(
                        onDoneClick = {
                            coroutineScope.launch {
                                modalBottomSheetState.hide()
                                onDismiss(GravatarQuickEditorDismissReason.Finished)
                            }
                        },
                    )
                    content()
                }
            }
        }
    }
}

internal val DEFAULT_PAGE_HEIGHT = 250.dp
