package com.gravatar

import android.content.Context
import android.content.pm.PackageManager
import com.gravatar.di.container.GravatarSdkContainer
import com.gravatar.logger.Logger

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
     * Does nothing once [appIdentifier] has named the app, in any call order: a derived label is a
     * fallback and must not displace a deliberate identifier.
     *
     * @param context The context from the app.
     */
    public fun context(context: Context): Gravatar {
        if (GravatarSdkContainer.instance.hasExplicitAppIdentifier) {
            Logger.w(LOG_TAG, "Ignoring context(): appIdentifier() already named this app.")
            return this
        }
        GravatarSdkContainer.instance.appName = getApplicationName(context)
        GravatarSdkContainer.instance.appVersion = getApplicationVersion(context)
        return this
    }

    /**
     * Identifies the app calling the SDK, for the `X-Source` header and the User-Agent.
     *
     * Takes precedence over whatever [context] derived, whichever order the two are called in, so
     * call this one when the app has a stable name it is already known by server-side. A later
     * [context] call is then ignored.
     *
     * Both values end up in a header, so this rejects anything that cannot be sent as one rather
     * than letting OkHttp fail every request later with an opaque error. The name is held to the
     * stricter bar of a User-Agent product token — no whitespace, no `/`, unaccented ASCII — so
     * that the string a server keys a rule on is exactly the string you passed. A label that only
     * [context] could produce is cleaned up instead; an identifier you chose is not guessed at.
     *
     * @param name The app name, e.g. `wp-android`.
     * @param version The app version, e.g. `27.6`. Omitted from the User-Agent when null, which
     * also discards a version [context] had already derived — this call states the whole identity.
     * @throws IllegalArgumentException if either value is blank or holds a character that cannot
     * be sent as a header, such as a newline or anything non-ASCII.
     */
    public fun appIdentifier(name: String, version: String? = null): Gravatar {
        requireProductToken(name)
        version?.let { requireHeaderSafe(it, "version") }
        GravatarSdkContainer.instance.appName = name
        GravatarSdkContainer.instance.appVersion = version
        GravatarSdkContainer.instance.hasExplicitAppIdentifier = true
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

    private fun requireProductToken(name: String) {
        require(name.isNotBlank()) { "The app name cannot be blank." }
        val token = GravatarSdkContainer.toProductToken(name)
        require(token == name) {
            "The app name must be a User-Agent product token, so it cannot hold whitespace, " +
                "`/`, `(`, `)` or `;`, and must be unaccented ASCII. Got \"$name\"" +
                token?.let { "; did you mean \"$it\"?" }.orEmpty()
        }
    }

    private fun requireHeaderSafe(value: String, field: String) {
        require(value.isNotBlank()) { "The app $field cannot be blank." }
        require(GravatarSdkContainer.isHeaderSafe(value)) {
            "The app $field must be printable ASCII to be sent as a header, but was \"$value\"."
        }
    }

    private fun getApplicationName(context: Context): String {
        val applicationInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA,
        )
        return context.packageManager.getApplicationLabel(applicationInfo).toString()
    }

    @Suppress("SwallowedException")
    private fun getApplicationVersion(context: Context): String? = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName?.takeIf { version ->
            // `versionName` is arbitrary manifest text, and a value OkHttp cannot send would fail
            // every request. Unlike [appIdentifier], the caller did not choose this string, so it
            // is dropped rather than thrown on — the name alone still identifies the app.
            GravatarSdkContainer.isHeaderSafe(version).also { safe ->
                if (!safe) Logger.w(LOG_TAG, "Ignoring versionName \"$version\": not header-safe.")
            }
        }
    } catch (e: PackageManager.NameNotFoundException) {
        // The app asking about its own package, so this should not happen, but it is not worth
        // failing initialization over a missing version.
        null
    }

    private const val LOG_TAG = "Gravatar"
}
