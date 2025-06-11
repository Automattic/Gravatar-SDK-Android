package com.gravatar.quickeditor.ui.editor.bottomsheet

import android.content.res.Configuration
import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import com.composables.core.ModalBottomSheet
import com.composables.core.ModalBottomSheetState
import com.composables.core.ModalSheetProperties
import com.composables.core.Scrim
import com.composables.core.Sheet
import com.composables.core.SheetDetent
import com.composables.core.SheetDetent.Companion.FullyExpanded
import com.composables.core.SheetDetent.Companion.Hidden
import com.composables.core.rememberModalBottomSheetState
import com.composeunstyled.LocalModalWindow
import com.gravatar.quickeditor.QuickEditorContainer
import com.gravatar.quickeditor.ui.editor.AuthenticationMethod
import com.gravatar.quickeditor.ui.editor.AvatarPickerResult
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorDismissReason
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorPage
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorParams
import com.gravatar.quickeditor.ui.editor.GravatarUiMode
import com.gravatar.quickeditor.ui.editor.UpdateHandler
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
 * @param updateHandler The callback for the Quick Editor updates.
 *                       Can be invoked multiple times while the Quick Editor is open.
 * @param onDismiss The callback for the dismiss action containing [GravatarQuickEditorDismissReason]
 */
@Composable
public fun GravatarQuickEditorBottomSheet(
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    authenticationMethod: AuthenticationMethod,
    updateHandler: UpdateHandler,
    onDismiss: (dismissReason: GravatarQuickEditorDismissReason) -> Unit = {},
) {
    GravatarQuickEditorBottomSheet(
        gravatarQuickEditorParams = gravatarQuickEditorParams,
        authenticationMethod = authenticationMethod,
        updateHandler = updateHandler,
        onDismiss = onDismiss,
        modalDetents = modalDetents(),
    )
}

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
@Deprecated(
    message = "Use the new GravatarQuickEditorBottomSheet with UpdateHandler param instead.",
    replaceWith = ReplaceWith(expression = "GravatarQuickEditorBottomSheet()"),
)
@Composable
public fun GravatarQuickEditorBottomSheet(
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    authenticationMethod: AuthenticationMethod,
    onAvatarSelected: () -> Unit,
    onDismiss: (dismissReason: GravatarQuickEditorDismissReason) -> Unit = {},
) {
    GravatarQuickEditorBottomSheet(
        gravatarQuickEditorParams = gravatarQuickEditorParams,
        authenticationMethod = authenticationMethod,
        updateHandler = { quickEditorUpdateType ->
            if (quickEditorUpdateType is AvatarPickerResult) {
                onAvatarSelected()
            }
        },
        onDismiss = onDismiss,
        modalDetents = modalDetents(),
    )
}

internal enum class DismissConfirmationState {
    Delegate,
    Confirm,
    Delegated,
}

@Composable
internal fun GravatarQuickEditorBottomSheet(
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    authenticationMethod: AuthenticationMethod,
    updateHandler: UpdateHandler,
    modalDetents: ModalDetents,
    onDismiss: (dismissReason: GravatarQuickEditorDismissReason) -> Unit = {},
    onCurrentDetentChanged: (sheetDetent: SheetDetent) -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    var dismissState: DismissConfirmationState by remember { mutableStateOf(DismissConfirmationState.Delegate) }

    val onDismissIgnored = {
        dismissState = DismissConfirmationState.Delegate
    }

    val modalBottomSheetState: ModalBottomSheetState = rememberModalBottomSheetState(
        initialDetent = modalDetents.initialDetent,
        detents = modalDetents.detents,
        confirmDetentChange = { sheetDetent ->
            // We only care about the Hidden state
            if (sheetDetent == Hidden) {
                when (dismissState) {
                    DismissConfirmationState.Confirm -> true
                    DismissConfirmationState.Delegated -> false
                    DismissConfirmationState.Delegate -> {
                        dismissState = DismissConfirmationState.Delegated
                        false
                    }
                }
            } else {
                true
            }
        },
    )

    val onDoneClicked: () -> Unit = {
        dismissState = DismissConfirmationState.Confirm
        coroutineScope.launch {
            modalBottomSheetState.animateTo(Hidden)
        }
    }

    val internalOnDismiss: (GravatarQuickEditorDismissReason) -> Unit = { dismissReason ->
        dismissState = DismissConfirmationState.Confirm
        coroutineScope.launch {
            modalBottomSheetState.animateTo(Hidden)
            onDismiss(dismissReason)
        }
    }

    LaunchedEffect(modalBottomSheetState.currentDetent) {
        onCurrentDetentChanged(modalBottomSheetState.currentDetent)
    }

    DisposableEffect(Unit) {
        if (authenticationMethod is AuthenticationMethod.Bearer) {
            QuickEditorContainer.getInstance().useInMemoryTokenStorage()
        }

        onDispose {
            QuickEditorContainer.getInstance().resetUseInMemoryTokenStorage()
        }
    }

    CompositionLocalProvider(LocalGravatarTheme provides mainGravatarTheme) {
        GravatarModalBottomSheet(
            onDismiss = onDismiss,
            modalBottomSheetState = modalBottomSheetState,
            colorScheme = gravatarQuickEditorParams.uiMode,
        ) {
            when (authenticationMethod) {
                is AuthenticationMethod.Bearer -> {
                    GravatarQuickEditorPage(
                        gravatarQuickEditorParams = gravatarQuickEditorParams,
                        authToken = authenticationMethod.token,
                        onDismiss = internalOnDismiss,
                        updateHandler = updateHandler,
                        confirmDismissal = dismissState == DismissConfirmationState.Delegated,
                        onDismissIgnored = onDismissIgnored,
                        onDoneClicked = onDoneClicked,
                    )
                }

                is AuthenticationMethod.OAuth -> {
                    GravatarQuickEditorPage(
                        gravatarQuickEditorParams = gravatarQuickEditorParams,
                        oAuthParams = authenticationMethod.oAuthParams,
                        onDismiss = internalOnDismiss,
                        updateHandler = updateHandler,
                        confirmDismissal = dismissState == DismissConfirmationState.Delegated,
                        onDismissIgnored = onDismissIgnored,
                        onDoneClicked = onDoneClicked,
                    )
                }
            }
        }
    }
}

@Composable
private fun GravatarModalBottomSheet(
    onDismiss: (dismissReason: GravatarQuickEditorDismissReason) -> Unit = {},
    colorScheme: GravatarUiMode,
    modalBottomSheetState: ModalBottomSheetState,
    content: @Composable () -> Unit,
) {
    val configuration = Configuration(LocalConfiguration.current).apply {
        uiMode = when (colorScheme) {
            GravatarUiMode.LIGHT -> Configuration.UI_MODE_NIGHT_NO
            GravatarUiMode.DARK -> Configuration.UI_MODE_NIGHT_YES
            GravatarUiMode.SYSTEM -> uiMode
        }
    }
    CompositionLocalProvider(
        LocalConfiguration provides configuration,
    ) {
        GravatarTheme {
            ModalBottomSheet(
                state = modalBottomSheetState,
                onDismiss = { onDismiss(GravatarQuickEditorDismissReason.Finished) },
                properties = ModalSheetProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = true,
                ),
            ) {
                // Modal content must be taking the uiMode from Activity and doesn't respect
                // the above set CompositionLocalProvider
                CompositionLocalProvider(
                    LocalConfiguration provides configuration,
                ) {
                    Scrim(
                        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f),
                    )
                    Box(
                        modifier = Modifier
                            .padding(
                                paddingValues = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                    PaddingValues(0.dp)
                                } else {
                                    WindowInsets.statusBars
                                        .only(WindowInsetsSides.Top)
                                        .asPaddingValues()
                                },
                            ),
                    ) {
                        val paddingValues = WindowInsets.navigationBars
                            .only(WindowInsetsSides.Vertical)
                            .asPaddingValues()
                        Sheet(
                            modifier = Modifier
                                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                                .widthIn(max = 640.dp)
                                .fillMaxWidth()
                                .padding(paddingValues)
                                .consumeWindowInsets(paddingValues),
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
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    content()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun modalDetents(): ModalDetents = ModalDetents(
    initialDetent = FullyExpanded,
    detents = buildList {
        add(Hidden)
        add(FullyExpanded)
    },
)

internal data class ModalDetents(
    val initialDetent: SheetDetent,
    val detents: List<SheetDetent>,
)

internal val DEFAULT_PAGE_HEIGHT = 300.dp
