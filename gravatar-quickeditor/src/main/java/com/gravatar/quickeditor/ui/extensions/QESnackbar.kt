package com.gravatar.quickeditor.ui.extensions

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

internal data class QESnackbarVisuals(
    val snackbarType: SnackbarType,
    override val actionLabel: String?,
    override val duration: SnackbarDuration,
    override val message: String,
    override val withDismissAction: Boolean,
) : SnackbarVisuals

@Composable
internal fun QESnackbarHost(hostState: SnackbarHostState, modifier: Modifier = Modifier) {
    SnackbarHost(
        modifier = modifier
            .padding(20.dp),
        hostState = hostState,
    ) { snackbarData ->
        val qeSnackbarVisuals = snackbarData.visuals as? QESnackbarVisuals
        val containerColor =
            qeSnackbarVisuals?.snackbarType?.containerColor ?: MaterialTheme.colorScheme.inverseSurface
        val contentColor =
            qeSnackbarVisuals?.snackbarType?.contentColor ?: MaterialTheme.colorScheme.inverseOnSurface
        Snackbar(
            snackbarData = snackbarData,
            containerColor = containerColor,
            dismissActionContentColor = contentColor,
            actionContentColor = contentColor,
            contentColor = contentColor,
            actionColor = contentColor,
        )
    }
}

internal suspend fun SnackbarHostState.showQESnackbar(
    message: String,
    actionLabel: String? = null,
    withDismissAction: Boolean = false,
    snackbarType: SnackbarType = SnackbarType.Info,
    duration: SnackbarDuration =
        if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
): QESnackbarResult {
    val visuals = QESnackbarVisuals(
        message = message,
        withDismissAction = withDismissAction,
        actionLabel = actionLabel,
        duration = duration,
        snackbarType = snackbarType,
    )

    // Only show the snackbar if the visuals are different from the current snackbar
    return if (currentSnackbarData?.visuals != visuals) {
        QESnackbarResult.fromSnackbarResult(showSnackbar(visuals))
    } else {
        QESnackbarResult.Skipped
    }
}

internal val SnackbarType.containerColor: Color
    @Composable get() = when (this) {
        SnackbarType.Info -> MaterialTheme.colorScheme.inverseSurface
        SnackbarType.Error -> MaterialTheme.colorScheme.errorContainer
    }

internal val SnackbarType.contentColor: Color
    @Composable get() = when (this) {
        SnackbarType.Info -> MaterialTheme.colorScheme.inverseOnSurface
        SnackbarType.Error -> MaterialTheme.colorScheme.onErrorContainer
    }

internal enum class SnackbarType {
    Info,
    Error,
}

internal enum class QESnackbarResult {
    Dismissed,
    ActionPerformed,
    Skipped,
    ;

    companion object {
        fun fromSnackbarResult(snackbarResult: SnackbarResult): QESnackbarResult {
            return when (snackbarResult) {
                SnackbarResult.Dismissed -> Dismissed
                SnackbarResult.ActionPerformed -> ActionPerformed
            }
        }
    }
}
