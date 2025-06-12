package com.gravatar.quickeditor.ui.editor

import androidx.compose.runtime.Composable
import com.composables.core.rememberModalBottomSheetState
import com.gravatar.quickeditor.ui.avatarpicker.AvatarPicker
import com.gravatar.quickeditor.ui.avatarpicker.AvatarPickerUiState
import com.gravatar.quickeditor.ui.avatarpicker.EmailAvatars
import com.gravatar.quickeditor.ui.editor.bottomsheet.GravatarModalBottomSheet
import com.gravatar.quickeditor.ui.editor.bottomsheet.modalDetents
import com.gravatar.quickeditor.ui.gravatarScreenshotTest
import com.gravatar.restapi.models.Avatar
import com.gravatar.types.Email
import com.gravatar.uitestutils.RoborazziTest
import org.junit.Test
import org.robolectric.annotation.Config
import java.net.URI

class GravatarQuickEditorBottomSheetTest : RoborazziTest() {
    @Test
    fun gravatarModalBottomSheet() = gravatarScreenshotTest {
        GravatarModalBottomSheet(
            colorScheme = GravatarUiMode.LIGHT,
            modalBottomSheetState = rememberModalBottomSheetState(modalDetents().initialDetent),
        ) {
            TestContent()
        }
    }

    @Config(qualifiers = "+night")
    @Test
    fun gravatarModalBottomSheetDark() = gravatarScreenshotTest {
        GravatarModalBottomSheet(
            colorScheme = GravatarUiMode.DARK,
            modalBottomSheetState = rememberModalBottomSheetState(modalDetents().initialDetent),
        ) {
            TestContent()
        }
    }

    @Composable
    private fun TestContent() {
        AvatarPicker(
            uiState = AvatarPickerUiState(
                email = Email("william.henry.harrison@example.com"),
                avatarPickerContentLayout = AvatarPickerContentLayout.Horizontal,
                emailAvatars = EmailAvatars(
                    avatars = listOf(
                        Avatar {
                            imageUrl = URI.create("https://gravatar.com/avatar/test")
                            imageId = "1"
                            rating = Avatar.Rating.G
                            altText = "alt"
                            updatedDate = ""
                        },
                        Avatar {
                            imageUrl = URI.create("https://gravatar.com/avatar/test2")
                            imageId = "2"
                            rating = Avatar.Rating.G
                            altText = "alt"
                            updatedDate = ""
                        },
                    ),
                    selectedAvatarId = "1",
                ),
            ),
            onEvent = { },
        )
    }
}
