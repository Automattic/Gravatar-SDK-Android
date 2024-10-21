package com.gravatar.quickeditor.ui.editor.bottomsheet

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.window.core.layout.WindowHeightSizeClass
import com.composables.core.LocalModalWindow
import com.composables.core.ModalBottomSheet
import com.composables.core.ModalBottomSheetState
import com.composables.core.Scrim
import com.composables.core.Sheet
import com.composables.core.SheetDetent
import com.composables.core.SheetDetent.Companion.FullyExpanded
import com.composables.core.SheetDetent.Companion.Hidden
import com.composables.core.rememberModalBottomSheetState
import com.gravatar.GravatarConstants
import com.gravatar.quickeditor.ui.components.QEDragHandle
import com.gravatar.quickeditor.ui.components.QETopBar
import com.gravatar.quickeditor.ui.editor.AuthenticationMethod
import com.gravatar.quickeditor.ui.editor.AvatarPickerContentLayout
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorDismissReason
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorPage
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorParams
import com.gravatar.ui.GravatarTheme
import com.gravatar.ui.LocalGravatarTheme
import com.gravatar.ui.mainGravatarTheme
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
 * @param onDismiss The callback for the dismiss action containing [GravatarQuickEditorDismissReason]
 */
@Composable
public fun GravatarQuickEditorBottomSheet(
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    authenticationMethod: AuthenticationMethod,
    onAvatarSelected: () -> Unit,
    onDismiss: (dismissReason: GravatarQuickEditorDismissReason) -> Unit = {},
) {
    CompositionLocalProvider(LocalGravatarTheme provides mainGravatarTheme) {
        GravatarQuickEditorBottomSheet(
            gravatarQuickEditorParams = gravatarQuickEditorParams,
            authenticationMethod = authenticationMethod,
            onAvatarSelected = onAvatarSelected,
            onDismiss = onDismiss,
            modalBottomSheetState = rememberGravatarModalBottomSheetState(
                avatarPickerContentLayout = gravatarQuickEditorParams.avatarPickerContentLayout,
            ),
        )
    }
}

@Composable
internal fun GravatarQuickEditorBottomSheet(
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    authenticationMethod: AuthenticationMethod,
    onAvatarSelected: () -> Unit,
    onDismiss: (dismissReason: GravatarQuickEditorDismissReason) -> Unit = {},
    modalBottomSheetState: ModalBottomSheetState,
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

@Composable
private fun GravatarModalBottomSheet(
    onDismiss: (dismissReason: GravatarQuickEditorDismissReason) -> Unit = {},
    modalBottomSheetState: ModalBottomSheetState,
    content: @Composable () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(modalBottomSheetState.currentDetent) {
        if (modalBottomSheetState.currentDetent == Hidden) {
            onDismiss(GravatarQuickEditorDismissReason.Finished)
        }
    }

    GravatarTheme {
        ModalBottomSheet(
            state = modalBottomSheetState,
        ) {
            Scrim(
                modifier = Modifier.clickable { modalBottomSheetState.currentDetent = Hidden },
                scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f),
            )
            Sheet(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                    .widthIn(max = 640.dp)
                    .fillMaxWidth()
                    .padding(
                        WindowInsets.navigationBars
                            .only(WindowInsetsSides.Vertical)
                            .asPaddingValues(),
                    ),
            ) {
                val window = LocalModalWindow.current
                val isDarkTheme = isSystemInDarkTheme()
                LaunchedEffect(Unit) {
                    window.navigationBarColor = Color.TRANSPARENT
                    WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars =
                        !isDarkTheme
                }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    tonalElevation = 1.dp,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        QEDragHandle()
                        QETopBar(
                            onDoneClick = {
                                coroutineScope.launch {
                                    modalBottomSheetState.currentDetent = Hidden
                                }
                            },
                            onGravatarIconClick = {
                                uriHandler.openUri(GravatarConstants.GRAVATAR_SIGN_IN_URL)
                            },
                        )
                        content()
                    }
                }
            }
        }
    }
}

@Composable
internal fun rememberGravatarModalBottomSheetState(
    avatarPickerContentLayout: AvatarPickerContentLayout,
): ModalBottomSheetState {
    val windowHeightSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowHeightSizeClass
    val peek = SheetDetent(identifier = "peek") { containerHeight, _ ->
        containerHeight * 0.6f
    }

    val initialDetent =
        if (windowHeightSizeClass == WindowHeightSizeClass.COMPACT) {
            FullyExpanded
        } else {
            when (avatarPickerContentLayout) {
                AvatarPickerContentLayout.Horizontal -> FullyExpanded
                AvatarPickerContentLayout.Vertical -> peek
            }
        }

    val detents = buildList {
        add(Hidden)
        if (avatarPickerContentLayout == AvatarPickerContentLayout.Horizontal) {
            add(FullyExpanded)
        } else {
            add(peek)
            add(FullyExpanded)
        }
    }
    return rememberModalBottomSheetState(
        initialDetent = initialDetent,
        detents = detents,
    )
}

internal val DEFAULT_PAGE_HEIGHT = 250.dp
