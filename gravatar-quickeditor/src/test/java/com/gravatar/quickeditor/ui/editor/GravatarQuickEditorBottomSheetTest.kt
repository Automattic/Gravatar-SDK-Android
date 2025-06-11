package com.gravatar.quickeditor.ui.editor

import androidx.compose.ui.graphics.Color
import com.composables.core.rememberModalBottomSheetState
import com.composeunstyled.Text
import com.gravatar.quickeditor.ui.editor.bottomsheet.GravatarModalBottomSheet
import com.gravatar.quickeditor.ui.editor.bottomsheet.modalDetents
import com.gravatar.quickeditor.ui.gravatarScreenshotTest
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test
import org.robolectric.annotation.Config

class GravatarQuickEditorBottomSheetTest : RoborazziTest() {
    @Test
    fun gravatarModalBottomSheet() = gravatarScreenshotTest {
        GravatarModalBottomSheet(
            colorScheme = GravatarUiMode.LIGHT,
            modalBottomSheetState = rememberModalBottomSheetState(modalDetents().initialDetent),
        ) {
            Text("Gravatar Quick Editor Bottom Sheet")
        }
    }

    @Config(qualifiers = "+night")
    @Test
    fun gravatarModalBottomSheetDark() = gravatarScreenshotTest {
        GravatarModalBottomSheet(
            colorScheme = GravatarUiMode.DARK,
            modalBottomSheetState = rememberModalBottomSheetState(modalDetents().initialDetent),
        ) {
            Text(color = Color.White, text = "Gravatar Quick Editor Bottom Sheet")
        }
    }
}
