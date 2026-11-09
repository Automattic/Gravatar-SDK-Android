package com.gravatar.quickeditor.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import com.gravatar.Gravatar

/**
 * An [ImageRequest] for [data] that identifies the SDK the way its API calls do.
 *
 * Coil loads through the host app's [coil.ImageLoader], which the SDK's interceptors never see, so
 * avatars and icons would otherwise reach gravatar.com as `okhttp/<version>` — the anonymous
 * default the SDK sets a User-Agent to avoid. Carrying the header on the request rather than
 * supplying an `ImageLoader` of our own leaves the host's cache, interceptors and dispatchers
 * alone: the only thing the SDK adds is the name.
 *
 * @param data What to load — a URL, or a drawable resource for the built-in placeholders.
 * @param builder Applied last, for anything else the call site needs of the request.
 */
@Composable
internal fun gravatarImageRequest(data: Any?, builder: ImageRequest.Builder.() -> Unit = {}): ImageRequest =
    gravatarImageRequest(LocalContext.current, data, builder)

internal fun gravatarImageRequest(
    context: Context,
    data: Any?,
    builder: ImageRequest.Builder.() -> Unit = {},
): ImageRequest = ImageRequest.Builder(context)
    .data(data)
    .setHeader("User-Agent", Gravatar.userAgent)
    .apply(builder)
    .build()
