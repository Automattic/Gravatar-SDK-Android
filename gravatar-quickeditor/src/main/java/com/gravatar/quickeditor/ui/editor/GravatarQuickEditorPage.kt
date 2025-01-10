package com.gravatar.quickeditor.ui.editor

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gravatar.quickeditor.QuickEditorContainer
import com.gravatar.quickeditor.ui.alttext.AltTextPage
import com.gravatar.quickeditor.ui.avatarpicker.AvatarPicker
import com.gravatar.quickeditor.ui.navigation.EditorNavDestinations
import com.gravatar.quickeditor.ui.navigation.QuickEditorPage
import com.gravatar.quickeditor.ui.oauth.OAuthPage
import com.gravatar.quickeditor.ui.oauth.OAuthParams
import com.gravatar.quickeditor.ui.splash.SplashPage
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Raw composable component for the Quick Editor.
 * This can be used to show the Quick Editor in Activity, Fragment or BottomSheet.
 *
 * @param gravatarQuickEditorParams The Quick Editor parameters.
 * @param oAuthParams The OAuth parameters.
 * @param onAvatarSelected The callback for the avatar update.
 *                       Can be invoked multiple times while the Quick Editor is open
 * @param onDismiss The callback for the dismiss action.
 *                  [GravatarQuickEditorError] will be non-null if the dismiss was caused by an error.
 */
@Composable
internal fun GravatarQuickEditorPage(
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    oAuthParams: OAuthParams,
    onAvatarSelected: () -> Unit,
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
            SplashPage(email = gravatarQuickEditorParams.email) { isAuthorized ->
                if (isAuthorized) {
                    navController.navigateAndClean(QuickEditorPage.EDITOR.name)
                } else {
                    navController.navigateAndClean(QuickEditorPage.OAUTH.name)
                }
            }
        }
        composable(route = QuickEditorPage.OAUTH.name, enterTransition = { EnterTransition.None }) {
            OAuthPage(
                oAuthParams = oAuthParams,
                email = gravatarQuickEditorParams.email,
                onAuthError = { onDismiss(GravatarQuickEditorDismissReason.OauthFailed) },
                onAuthSuccess = {
                    navController.navigateAndClean(QuickEditorPage.EDITOR.name)
                },
            )
        }
        addAvatarPickerGraph(
            gravatarQuickEditorParams = gravatarQuickEditorParams,
            handleExpiredSession = true,
            navController = navController,
            onAvatarSelected = onAvatarSelected,
            onSessionExpired = {
                navController.navigateAndClean(QuickEditorPage.OAUTH.name)
            },
        )
    }
}

/**
 * Raw composable component for the Quick Editor.
 * This can be used to show the Quick Editor in Activity, Fragment or BottomSheet.
 *
 * @param gravatarQuickEditorParams The Quick Editor parameters.
 * @param authToken The authentication token.
 * @param onAvatarSelected The callback for the avatar update.
 *                       Can be invoked multiple times while the Quick Editor is open
 * @param onDismiss The callback for the dismiss action.
 *                  [GravatarQuickEditorError] will be non-null if the dismiss was caused by an error.
 */
@Composable
internal fun GravatarQuickEditorPage(
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    authToken: String,
    onAvatarSelected: () -> Unit,
    onDismiss: (dismissReason: GravatarQuickEditorDismissReason) -> Unit = {},
) {
    val navController = rememberNavController()

    DisposableEffect(authToken) {
        QuickEditorContainer.getInstance().useInMemoryTokenStorage()

        onDispose {
            QuickEditorContainer.getInstance().resetUseInMemoryTokenStorage()
        }
    }

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
            ) {
                navController.navigateAndClean(QuickEditorPage.EDITOR.name)
            }
        }
        addAvatarPickerGraph(
            gravatarQuickEditorParams = gravatarQuickEditorParams,
            handleExpiredSession = false,
            navController = navController,
            onAvatarSelected = onAvatarSelected,
            onSessionExpired = { onDismiss(GravatarQuickEditorDismissReason.InvalidToken) },
        )
    }
}

private fun NavGraphBuilder.addAvatarPickerGraph(
    navController: NavHostController,
    gravatarQuickEditorParams: GravatarQuickEditorParams,
    handleExpiredSession: Boolean,
    onAvatarSelected: () -> Unit,
    onSessionExpired: () -> Unit,
) {
    navigation(
        route = QuickEditorPage.EDITOR.name,
        startDestination = EditorNavDestinations.AVATAR_SELECTION.name,
    ) {
        composable(route = EditorNavDestinations.AVATAR_SELECTION.name) {
            AvatarPicker(
                gravatarQuickEditorParams = gravatarQuickEditorParams,
                handleExpiredSession = handleExpiredSession,
                onAvatarSelected = onAvatarSelected,
                onSessionExpired = onSessionExpired,
                onAltTextTapped = { email, avatarId, altText, avatarUrl ->
                    val encodedUrl = URLEncoder.encode(avatarUrl, StandardCharsets.UTF_8.toString())
                    navController.navigate(
                        route = "${EditorNavDestinations.ALT_TEXT.name}/$email/$avatarId/$altText/$encodedUrl",
                    )
                },
            )
        }
        composable(
            route = "${EditorNavDestinations.ALT_TEXT.name}/{email}/{avatarId}/{altText}/{avatarUrl}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("avatarId") { type = NavType.StringType },
                navArgument("altText") { type = NavType.StringType },
                navArgument("avatarUrl") { type = NavType.StringType },
            ),
        ) {
            val email = requireNotNull(it.arguments?.getString("email"))
            val avatarId = requireNotNull(it.arguments?.getString("avatarId"))
            val altText = requireNotNull(it.arguments?.getString("altText"))
            val avatarUrl = requireNotNull(it.arguments?.getString("avatarUrl"))
            AltTextPage(
                email = email,
                avatarId = avatarId,
                altText = altText,
                avatarUrl = avatarUrl,
                onBackPressed = { navController.popBackStack() },
            )
        }
    }
}

private fun NavHostController.navigateAndClean(route: String) {
    navigate(route = route) {
        popUpTo(graph.startDestinationId) { inclusive = true }
    }
    graph.setStartDestination(route)
}
