package com.gravatar.quickeditor.ui.editor.extensions

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.composables.core.SheetDetent.Companion.Hidden
import com.gravatar.quickeditor.ui.editor.AuthenticationMethod
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorDismissReason
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorParams
import com.gravatar.quickeditor.ui.editor.UpdateHandler
import com.gravatar.quickeditor.ui.editor.bottomsheet.GravatarQuickEditorBottomSheet
import com.gravatar.quickeditor.ui.editor.bottomsheet.modalDetents

internal fun addQuickEditorToView(
    viewGroup: ViewGroup,
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    authenticationMethod: AuthenticationMethod,
    updateHandler: UpdateHandler,
    onDismiss: (dismissReason: GravatarQuickEditorDismissReason) -> Unit,
) {
    viewGroup.addView(
        ComposeView(viewGroup.context).apply {
            setContent {
                GravatarQuickEditorBottomSheet(
                    gravatarQuickEditorParams = gravatarQuickEditorParams,
                    authenticationMethod = authenticationMethod,
                    updateHandler = updateHandler,
                    onDismiss = onDismiss,
                    modalDetents = gravatarQuickEditorParams.scopeOption.modalDetents(),
                    onCurrentDetentChanged = {
                        if (it == Hidden) {
                            viewGroup.removeView(this)
                        }
                    },
                )
            }
        },
    )
}
