package com.gravatar.quickeditor.ui.alttext

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.gravatar.quickeditor.R
import com.gravatar.quickeditor.ui.components.QEButton
import com.gravatar.quickeditor.ui.components.QESectionTitle
import com.gravatar.quickeditor.ui.extensions.QESnackbarHost
import com.gravatar.quickeditor.ui.extensions.SnackbarType
import com.gravatar.quickeditor.ui.extensions.showQESnackbar
import com.gravatar.ui.GravatarTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URI
import java.net.URL

/** Gravatar Alt Text help URL */
private const val GRAVATAR_ALT_TEXT_HELP_URL: String =
    "https://support.gravatar.com/profiles/avatars/#add-alt-text-to-avatars"

@Composable
internal fun AltTextPage(
    email: String,
    avatarId: String,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AltTextViewModel = viewModel(
        factory = AltTextViewModelFactory(email, avatarId),
    ),
) {
    BackHandler {
        onBackPressed()
    }

    val context = LocalContext.current
    val snackState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsState()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Main.immediate) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actions.collect { action ->
                    when (action) {
                        is AltTextAction.AvatarCantBeLoaded,
                        is AltTextAction.AltTextUpdated,
                        -> {
                            onBackPressed()
                        }

                        is AltTextAction.AltTextUpdateFailed -> {
                            scope.launch {
                                snackState.showQESnackbar(
                                    message = context.getString(
                                        R.string.gravatar_qe_avatar_picker_alt_text_update_error,
                                    ),
                                    snackbarType = SnackbarType.Error,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    GravatarTheme {
        Box(
            modifier = modifier
                .padding(16.dp)
                .wrapContentSize(),
        ) {
            state.let { altTextState ->
                AltTextPage(
                    altTextState = altTextState,
                    onEvent = viewModel::onEvent,
                )
                QESnackbarHost(
                    modifier = Modifier
                        .align(Alignment.BottomStart),
                    hostState = snackState,
                )
            }
        }
    }
}

@Composable
internal fun AltTextPage(
    altTextState: AltTextUiState,
    onEvent: (AltTextEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current

    Surface(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .animateContentSize()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = RoundedCornerShape(8.dp),
                )
                .padding(16.dp),
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    QESectionTitle(
                        title = stringResource(R.string.gravatar_qe_avatar_alt_text_section_title),
                        modifier = Modifier,
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.gravatar_alt_text_help),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = stringResource(id = R.string.gravatar_qe_avatar_alt_text_section_what_is),
                        modifier = Modifier.clickable {
                            uriHandler.openUri(GRAVATAR_ALT_TEXT_HELP_URL)
                        },
                    )
                }

                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .height(IntrinsicSize.Min),
                ) {
                    val cornerRadius = 8.dp
                    val avatarSize = 96.dp
                    val sizePx = with(LocalDensity.current) { avatarSize.roundToPx() }
                    AsyncImage(
                        model = altTextState.imageUrlWithSize(sizePx),
                        contentDescription = stringResource(
                            id = R.string.gravatar_qe_selectable_avatar_content_description,
                        ),
                        modifier = Modifier
                            .size(avatarSize)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.surfaceDim,
                                RoundedCornerShape(cornerRadius),
                            )
                            .clip(RoundedCornerShape(cornerRadius)),
                    )
                    BasicTextField(
                        value = altTextState.altText,
                        onValueChange = { newAltText ->
                            onEvent(AltTextEvent.AvatarAltTextChange(newAltText))
                        },
                        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier
                            .fillMaxSize(),
                        decorationBox = { innerTextField ->
                            Column(
                                modifier = Modifier
                                    .padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
                            ) {
                                Box(
                                    modifier = Modifier.weight(1f),
                                ) {
                                    if (altTextState.altText.isEmpty()) {
                                        Text(
                                            text = stringResource(
                                                R.string.gravatar_qe_avatar_alt_text_section_placeholder,
                                            ),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                    innerTextField()
                                }
                                Text(
                                    text = (altTextState.altTextMaxLength - altTextState.altText.count()).toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                    modifier = Modifier
                                        .align(Alignment.End),
                                )
                            }
                        },
                    )
                }
                QEButton(
                    buttonText = stringResource(R.string.gravatar_qe_avatar_alt_text_save_button),
                    onClick = { onEvent(AltTextEvent.AvatarAltTextSaveTapped) },
                    modifier = Modifier.padding(top = 16.dp),
                    enabled = altTextState.isSaveButtonEnabled,
                    loading = altTextState.isUpdating,
                )
            }
        }
    }
}

private fun AltTextUiState.imageUrlWithSize(sizePx: Int) = avatarUrl?.toURL()?.let { url ->
    URL(url.protocol, url.host, url.path.plus("?size=$sizePx"))
}?.toString().orEmpty()

@Composable
@Preview(showBackground = true)
private fun AltTextPagePreview() {
    GravatarTheme {
        AltTextPage(
            altTextState = AltTextUiState(
                avatarUrl = URI.create("https://gravatar.com/avatar/test"),
                isUpdating = false,
                altText = "alt",
                altTextMaxLength = 125,
            ),
            onEvent = { },
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun AltTextPageEmptyAltTextPreview() {
    GravatarTheme {
        AltTextPage(
            altTextState = AltTextUiState(
                avatarUrl = URI.create("https://gravatar.com/avatar/test"),
                isUpdating = false,
                altText = "",
                altTextMaxLength = 125,
            ),
            onEvent = { },
        )
    }
}
