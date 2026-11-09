package com.gravatar.di.container

import com.gravatar.BuildConfig
import com.gravatar.moshiadapers.URIJsonAdapter
import com.gravatar.restapi.apis.AvatarsApi
import com.gravatar.restapi.apis.ProfilesApi
import com.gravatar.services.interceptors.AuthenticationInterceptor
import com.gravatar.services.interceptors.AvatarUploadTimeoutInterceptor
import com.gravatar.services.interceptors.SdkVersionInterceptor
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import java.text.Normalizer

internal class GravatarSdkContainer private constructor() {
    companion object {
        val instance: GravatarSdkContainer by lazy {
            GravatarSdkContainer()
        }

        private const val SDK_PRODUCT = "GravatarAndroid"

        private val COMBINING_MARKS = Regex("\\p{M}+")

        /** Everything RFC 9110 does not allow in a `token`, in runs so they collapse to one dash. */
        private val NON_TOKEN = Regex("[^A-Za-z0-9!#\$%&'*+\\-.^_`|~]+")

        /**
         * Whether [value] can be sent as an HTTP header value.
         *
         * OkHttp rejects anything outside tab and printable ASCII, and it does so from inside the
         * interceptor, which fails the request rather than the call that supplied the value. Guards
         * [appVersion], which reaches a header as it was given: `Gravatar.appIdentifier` rejects an
         * unusable one and `Gravatar.context` drops it. [appName] needs no such guard, since it is
         * only ever sent as [toProductToken] rendered it.
         *
         * Whitespace is deliberately allowed: it is legal in a header value. Only a product token
         * needs a narrower set.
         */
        internal fun isHeaderSafe(value: String): Boolean = value.all { it == '\t' || it in ' '..'~' }

        /**
         * [label] reduced to a User-Agent product token, or null when nothing usable survives.
         *
         * A product token is a `token`, so it cannot hold whitespace, `/`, `(`, `)` or `;` — a
         * label like `A/B Tester` would otherwise parse as the product `A`, and a server rule
         * keyed on the app name would not match. Accents are normalized away rather than stripped
         * blindly, so `Café` identifies itself as `Cafe` instead of losing its last letter; a
         * label with nothing left after that, such as one written entirely in kana, becomes null
         * and the User-Agent falls back to the SDK token alone.
         */
        internal fun toProductToken(label: String): String? = Normalizer
            .normalize(label, Normalizer.Form.NFD)
            .replace(COMBINING_MARKS, "")
            .replace(NON_TOKEN, "-")
            .trim('-')
            .takeIf { it.isNotEmpty() }
    }

    internal val moshi = Moshi.Builder()
        .add(URIJsonAdapter())
        .build()

    val dispatcherMain: CoroutineDispatcher = Dispatchers.Main
    val dispatcherDefault = Dispatchers.Default
    val dispatcherIO = Dispatchers.IO

    var apiKey: String? = null

    var appName: String? = null
        set(value) {
            field = value
            appNameToken = value?.let { toProductToken(it) }
        }

    /**
     * [appName] as a product token, or null when no usable token can be made of it.
     *
     * This, not [appName], is what goes on the wire — both in the User-Agent and in `X-Source` —
     * so the two headers always name the app the same way and a label that cannot be sent at all
     * simply is not. Computed on write rather than on every request.
     */
    var appNameToken: String? = null
        private set

    var appVersion: String? = null

    /**
     * Whether [appName] and [appVersion] came from `Gravatar.appIdentifier` rather than being
     * derived from a `Context`. An explicit identifier is a deliberate choice, so `Gravatar.context`
     * leaves it alone: otherwise an app that identifies itself in `Application.onCreate` would
     * silently lose that name the next time anything called `context()`, which is exactly the
     * per-build-type, localized naming the identifier exists to avoid.
     */
    var hasExplicitAppIdentifier: Boolean = false

    /**
     * The User-Agent sent with every request, e.g. `wp-android/27.6 GravatarAndroid/2.5.0`.
     *
     * Without a usable app identifier this is just the SDK token, which still beats OkHttp's
     * default `okhttp/<version>` — that one makes SDK traffic indistinguishable from any other
     * client and leaves nothing for a server to attribute a request to. The app appears as
     * [appNameToken], since a product token cannot hold everything a launcher label can.
     */
    val userAgent: String
        get() = buildString {
            appNameToken?.let { token ->
                append(token)
                appVersion?.let { append("/").append(it) }
                append(" ")
            }
            append(SDK_PRODUCT).append("/").append(BuildConfig.SDK_VERSION)
        }

    fun getProfilesApi(okHttpClient: OkHttpClient? = null, oauthToken: String? = null): ProfilesApi =
        ProfilesApi(client = okHttpClient.buildOkHttpClient(oauthToken))

    fun getAvatarsApi(okHttpClient: OkHttpClient?, oauthToken: String?): AvatarsApi =
        AvatarsApi(client = okHttpClient.buildOkHttpClient(oauthToken))

    private fun OkHttpClient?.buildOkHttpClient(oauthToken: String?) = (this ?: OkHttpClient())
        .newBuilder()
        .addInterceptor(AuthenticationInterceptor(oauthToken))
        .addInterceptor(AvatarUploadTimeoutInterceptor())
        .addInterceptor(SdkVersionInterceptor())
        .build()
}
