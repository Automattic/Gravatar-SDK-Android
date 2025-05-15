package com.gravatar.quickeditor.ui.editor

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gravatar.quickeditor.ui.alttext.AltTextPage
import com.gravatar.quickeditor.ui.navigation.EditorNavDestinations
import com.gravatar.quickeditor.ui.navigation.QuickEditorPage
import com.gravatar.quickeditor.ui.oauth.OAuthPage
import com.gravatar.quickeditor.ui.oauth.OAuthParams
import com.gravatar.quickeditor.ui.splash.SplashPage

/**
 * Raw composable component for the Quick Editor.
 * This can be used to show the Quick Editor in Activity, Fragment or BottomSheet.
 *
 * @param gravatarQuickEditorParams The Quick Editor parameters.
 * @param oAuthParams The OAuth parameters.
 * @param updateHandler The callback for the Quick Editor updates.
 *                       Can be invoked multiple times while the Quick Editor is open
 * @param onDoneClicked The callback for the done action.
 * @param onDismiss The callback for the dismiss action.
 *                  [GravatarQuickEditorError] will be non-null if the dismiss was caused by an error.
 */
@Composable
internal fun GravatarQuickEditorPage(
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    oAuthParams: OAuthParams,
    updateHandler: UpdateHandler,
    onDoneClicked: () -> Unit,
    onDismiss: (dismissReason: GravatarQuickEditorDismissReason) -> Unit = {},
) {
    val navController = rememberNavController()

    NavHost(
        navController,
        startDestination = QuickEditorPage.SPLASH.name,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
    ) {
        composable(route = QuickEditorPage.SPLASH.name) {
            SplashPage(
                email = gravatarQuickEditorParams.email,
                onDoneClicked = onDoneClicked,
            ) { isAuthorized ->
                if (isAuthorized) {
                    navController.navigateAndPopupTo(QuickEditorPage.EDITOR.name, QuickEditorPage.SPLASH.name)
                } else {
                    navController.navigateAndPopupTo(QuickEditorPage.OAUTH.name, QuickEditorPage.SPLASH.name)
                }
            }
        }
        composable(route = QuickEditorPage.OAUTH.name, enterTransition = { EnterTransition.None }) {
            OAuthPage(
                oAuthParams = oAuthParams,
                email = gravatarQuickEditorParams.email,
                onAuthError = { onDismiss(GravatarQuickEditorDismissReason.OauthFailed) },
                onAuthSuccess = {
                    navController.navigateAndPopupTo(QuickEditorPage.EDITOR.name, QuickEditorPage.OAUTH.name)
                },
                onDoneClicked = onDoneClicked,
            )
        }
        addAvatarPickerGraph(
            gravatarQuickEditorParams = gravatarQuickEditorParams,
            handleExpiredSession = true,
            navController = navController,
            updateHandler = updateHandler,
            onSessionExpired = {
                navController.navigateAndPopupTo(QuickEditorPage.OAUTH.name, QuickEditorPage.EDITOR.name)
            },
            onDoneClicked = onDoneClicked,
        )
    }
}

/**
 * Raw composable component for the Quick Editor.
 * This can be used to show the Quick Editor in Activity, Fragment or BottomSheet.
 *
 * @param gravatarQuickEditorParams The Quick Editor parameters.
 * @param authToken The authentication token.
 * @param updateHandler The callback for the Quick Editor updates.
 *                       Can be invoked multiple times while the Quick Editor is open
 * @param onDoneClicked The callback for the done action.
 * @param onDismiss The callback for the dismiss action.
 *                  [GravatarQuickEditorError] will be non-null if the dismiss was caused by an error.
 */
@Composable
internal fun GravatarQuickEditorPage(
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    authToken: String,
    updateHandler: UpdateHandler,
    onDoneClicked: () -> Unit,
    onDismiss: (dismissReason: GravatarQuickEditorDismissReason) -> Unit = {},
) {
    val navController = rememberNavController()

    NavHost(
        navController,
        startDestination = QuickEditorPage.SPLASH.name,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
    ) {
        composable(QuickEditorPage.SPLASH.name) {
            SplashPage(
                email = gravatarQuickEditorParams.email,
                token = authToken,
                onDoneClicked = onDoneClicked,
            ) {
                navController.navigateAndPopupTo(QuickEditorPage.EDITOR.name, QuickEditorPage.SPLASH.name)
            }
        }
        addAvatarPickerGraph(
            gravatarQuickEditorParams = gravatarQuickEditorParams,
            handleExpiredSession = false,
            navController = navController,
            updateHandler = updateHandler,
            onSessionExpired = { onDismiss(GravatarQuickEditorDismissReason.InvalidToken) },
            onDoneClicked = onDoneClicked,
        )
    }
}

@Suppress("LongParameterList")
private fun NavGraphBuilder.addAvatarPickerGraph(
    navController: NavHostController,
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    handleExpiredSession: Boolean,
    updateHandler: UpdateHandler,
    onSessionExpired: () -> Unit,
    onDoneClicked: () -> Unit,
) {
    navigation(
        route = QuickEditorPage.EDITOR.name,
        startDestination = EditorNavDestinations.AVATAR_SELECTION.name,
    ) {
        composable(
            route = EditorNavDestinations.AVATAR_SELECTION.name,
            enterTransition = { fadeIn() },
            popEnterTransition = { fadeIn() + expandVertically() },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start) + shrinkVertically()
            },
        ) {
            QuickEditor(
                gravatarQuickEditorParams = gravatarQuickEditorParams,
                handleExpiredSession = handleExpiredSession,
                updateHandler = updateHandler,
                onSessionExpired = onSessionExpired,
                onAltTextTapped = { email, avatarId ->
                    navController.navigate(
                        route = "${EditorNavDestinations.ALT_TEXT.name}/$email/$avatarId",
                    )
                },
                onDoneClicked = onDoneClicked,
            )
        }
        composable(
            route = "${EditorNavDestinations.ALT_TEXT.name}/{email}/{avatarId}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("avatarId") { type = NavType.StringType },
            ),
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End) },
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
        ) {
            val email = requireNotNull(it.arguments?.getString("email"))
            val avatarId = requireNotNull(it.arguments?.getString("avatarId"))
            AltTextPage(
                email = email,
                avatarId = avatarId,
                onBackPressed = { navController.popBackStack() },
            )
        }
    }
}

private fun NavHostController.navigateAndPopupTo(route: String, popUpTo: String) {
    navigate(route = route) {
        popUpTo(popUpTo) { inclusive = true }
    }
}
