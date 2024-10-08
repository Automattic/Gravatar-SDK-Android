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
     * The context is used to get the application name.
     *
     * @param context The context from the app.
     */
    public fun context(context: Context): Gravatar {
        GravatarSdkContainer.instance.appName = getApplicationName(context)
        return this
    }

    private fun getApplicationName(context: Context): String {
        val applicationInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA,
        )
        return context.packageManager.getApplicationLabel(applicationInfo).toString()
    }
}
