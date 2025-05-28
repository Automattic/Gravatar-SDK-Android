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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.window.core.layout.WindowHeightSizeClass
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
import com.gravatar.quickeditor.ui.avatarpicker.pxToDp
import com.gravatar.quickeditor.ui.components.QEDragHandle
import com.gravatar.quickeditor.ui.editor.AuthenticationMethod
import com.gravatar.quickeditor.ui.editor.AvatarPickerContentLayout
import com.gravatar.quickeditor.ui.editor.AvatarPickerResult
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorDismissReason
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorPage
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorParams
import com.gravatar.quickeditor.ui.editor.GravatarUiMode
import com.gravatar.quickeditor.ui.editor.QuickEditorPage
import com.gravatar.quickeditor.ui.editor.QuickEditorScopeOption
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
        modalDetents = gravatarQuickEditorParams.scopeOption.modalDetents(),
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
        modalDetents = gravatarQuickEditorParams.scopeOption.modalDetents(),
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

private val peek = SheetDetent(identifier = "peek") { containerHeight, _ ->
    containerHeight * 0.6f
}

@Composable
internal fun QuickEditorScopeOption.modalDetents(): ModalDetents {
    val windowHeightSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowHeightSizeClass

    val detents = buildDetentsList()
    val initialDetent = initialDetent(windowHeightSizeClass)

    return ModalDetents(
        initialDetent = if (detents.contains(initialDetent)) initialDetent else detents.last(),
        detents = detents,
    )
}

private fun QuickEditorScopeOption.buildDetentsList(): List<SheetDetent> {
    return when (this.scope) {
        is QuickEditorScopeOption.Scope.AvatarPickerAndAboutEditor,
        is QuickEditorScopeOption.Scope.AboutEditor,
        -> buildList {
            add(Hidden)
            add(peek)
            add(FullyExpanded)
        }

        is QuickEditorScopeOption.Scope.AvatarPicker -> buildList {
            add(Hidden)
            if (avatarPickerContentLayout == AvatarPickerContentLayout.Vertical) {
                add(peek)
            }
            add(FullyExpanded)
        }
    }
}

private fun QuickEditorScopeOption.initialDetent(windowHeightSizeClass: WindowHeightSizeClass): SheetDetent {
    return if (windowHeightSizeClass == WindowHeightSizeClass.COMPACT) {
        FullyExpanded
    } else {
        when (this.scope) {
            is QuickEditorScopeOption.Scope.AboutEditor -> peek
            is QuickEditorScopeOption.Scope.AvatarPickerAndAboutEditor,
            is QuickEditorScopeOption.Scope.AvatarPicker,
            -> {
                if (
                    this.avatarPickerContentLayout == AvatarPickerContentLayout.Horizontal &&
                    this.initialPage == QuickEditorPage.AvatarPicker
                ) {
                    FullyExpanded
                } else {
                    peek
                }
            }
        }
    }
}

/**
 * The default .imePadding adds a lot of extra padding to the bottom of the screen
 * This is a workaround, see https://stackoverflow.com/questions/76014880/enormous-ime-padding-in-jetpack-compose
 */
internal fun Modifier.positionAwareImePadding(): Modifier = composed {
    var consumePadding by remember { mutableIntStateOf(0) }

    this
        .onGloballyPositioned { coordinates ->
            val root = coordinates.findRootCoordinates()
            val bottom = coordinates.positionInWindow().y + coordinates.size.height
            consumePadding = (root.size.height - bottom).toInt().coerceAtLeast(0)
        }
        .consumeWindowInsets(PaddingValues(bottom = consumePadding.pxToDp(LocalContext.current)))
        .imePadding()
}

internal data class ModalDetents(
    val initialDetent: SheetDetent,
    val detents: List<SheetDetent>,
)

internal val DEFAULT_PAGE_HEIGHT = 300.dp
