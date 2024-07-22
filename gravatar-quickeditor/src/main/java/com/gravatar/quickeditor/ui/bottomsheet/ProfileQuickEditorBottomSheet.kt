package com.gravatar.quickeditor.ui.bottomsheet

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.gravatar.quickeditor.R
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorParams
import com.gravatar.quickeditor.ui.editor.ProfileQuickEditorPage
import com.gravatar.ui.GravatarTheme
import kotlinx.coroutines.launch

/**
 * ModalBottomSheet component for the Profile Quick Editor.
 *
 * The bottom sheet is configured to take 70% of the screen height and skips the partially expanded state.
 *
 * @param gravatarQuickEditorParams The Gravatar Quick Editor parameters.
 * @param onDismiss The callback for when the bottom sheet is dismissed.
 * @param modalBottomSheetState The state of the bottom sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun ProfileQuickEditorBottomSheet(
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    onDismiss: () -> Unit,
    modalBottomSheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        modifier = Modifier
            .fillMaxHeight(0.7f)
            .fillMaxWidth(),
        onDismissRequest = onDismiss,
        sheetState = modalBottomSheetState,
        dragHandle = null,
    ) {
        GravatarTheme {
            Column {
                CenterAlignedTopAppBar(
                    windowInsets = WindowInsets(0, 0, 0, 0),
                    title = {
                        Text(
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            text = stringResource(id = R.string.gravatar),
                        )
                    },
                    navigationIcon = {
                        TextButton(
                            onClick = {
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                    onDismiss()
                                }
                            },
                        ) {
                            Text(
                                style = MaterialTheme.typography.labelLarge,
                                text = stringResource(R.string.bottom_sheet_done),
                            )
                        }
                    },
                )
                ProfileQuickEditorPage(
                    gravatarQuickEditorParams = gravatarQuickEditorParams,
                    onAuthError = {
                        Log.d("QuickEditor", "Auth error")
                        onDismiss()
                    },
                )
            }
        }
    }
}
