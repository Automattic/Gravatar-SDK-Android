package com.gravatar

import android.content.Context
import android.content.pm.PackageManager
import com.gravatar.di.container.GravatarSdkContainer

/**
 * Entry point for initializing the Gravatar SDK.
 */
public object Gravatar {
    /**
     * Initializes the Gravatar SDK with the given API key.
     *
     * @param apiKey The API key to use when making requests to the Gravatar backend.
     */
    public fun apiKey(apiKey: String): Gravatar {
        GravatarSdkContainer.instance.apiKey = apiKey
        return this
    }

    /**
     * Initializes the Gravatar SDK with the given context.
     * The context is used to get the application name and version, which identify the app in the
     * `X-Source` header and the User-Agent of every request.
     *
     * Prefer [appIdentifier] when the launcher label is not the name you want on the wire: it is
     * localized, and build types usually suffix it, so the same app reaches the backend under
     * several names.
     *
     * @param context The context from the app.
     */
    public fun context(context: Context): Gravatar {
        GravatarSdkContainer.instance.appName = getApplicationName(context)
        GravatarSdkContainer.instance.appVersion = getApplicationVersion(context)
        return this
    }

    /**
     * Identifies the app calling the SDK, for the `X-Source` header and the User-Agent.
     *
     * Takes precedence over whatever [context] derived, so call this one when the app has a stable
     * name it is already known by server-side.
     *
     * @param name The app name, e.g. `wp-android`.
     * @param version The app version, e.g. `27.6`. Omitted from the User-Agent when null.
     */
    public fun appIdentifier(name: String, version: String? = null): Gravatar {
        GravatarSdkContainer.instance.appName = name
        GravatarSdkContainer.instance.appVersion = version
        return this
    }

    /**
     * The User-Agent the SDK sends, e.g. `wp-android/27.6 GravatarAndroid/2.5.0`.
     *
     * Reflects whatever [appIdentifier] or [context] last set, and falls back to the SDK token
     * alone. Exposed so callers that make their own requests to Gravatar, or that download through
     * a different stack, can identify themselves the same way.
     */
    public val userAgent: String
        get() = GravatarSdkContainer.instance.userAgent

    private fun getApplicationName(context: Context): String {
        val applicationInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA,
        )
        return context.packageManager.getApplicationLabel(applicationInfo).toString()
    }

    @Suppress("SwallowedException")
    private fun getApplicationVersion(context: Context): String? = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    } catch (e: PackageManager.NameNotFoundException) {
        // The app asking about its own package, so this should not happen, but it is not worth
        // failing initialization over a missing version.
        null
    }
}
