package com.gravatar.quickeditor.ui.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.composables.core.ModalBottomSheetState
import com.composables.core.SheetDetent
import com.composables.core.SheetDetent.Companion.FullyExpanded
import com.composables.core.SheetDetent.Companion.Hidden
import com.composables.core.rememberModalBottomSheetState
import com.gravatar.quickeditor.ui.editor.bottomsheet.GravatarModalBottomSheet
import com.gravatar.quickeditor.ui.editor.bottomsheet.modalDetents
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GravatarModalBottomSheetTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `sheet stays expanded when its content is empty for a frame`() {
        var showContent by mutableStateOf(true)
        val requestedDetents = mutableListOf<SheetDetent>()
        var dismissReason: GravatarQuickEditorDismissReason? = null
        lateinit var state: ModalBottomSheetState

        composeRule.setContent {
            state = rememberModalBottomSheetState(
                initialDetent = modalDetents().initialDetent,
                detents = modalDetents().detents,
                confirmDetentChange = { detent ->
                    requestedDetents += detent
                    true
                },
            )
            GravatarModalBottomSheet(
                colorScheme = GravatarUiMode.LIGHT,
                modalBottomSheetState = state,
                onDismiss = { dismissReason = it },
            ) {
                if (showContent) {
                    Box(modifier = Modifier.fillMaxWidth().height(300.dp))
                }
            }
        }
        composeRule.waitForIdle()

        // Navigating between Quick Editor pages can leave the sheet with no content for a frame
        showContent = false
        composeRule.waitForIdle()
        showContent = true
        composeRule.waitForIdle()

        assertFalse(
            "Sheet was asked to hide: ${requestedDetents.map { it.identifier }}",
            requestedDetents.contains(Hidden),
        )
        assertEquals(FullyExpanded, state.currentDetent)
        assertNull(dismissReason)
    }
}
