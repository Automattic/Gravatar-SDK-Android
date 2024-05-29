package com.gravatar

import com.gravatar.di.container.GravatarSdkContainer

/**
 * Entry point for initializing the Gravatar SDK.
 */
public object Gravatar {
    /**
     * Initializes the Gravatar SDK with the given API key.
     *
     * @param gravatarApiKey The API key to use when making requests to the Gravatar backend.
     */
    public fun initialize(gravatarApiKey: String) {
        GravatarSdkContainer.instance.apiKey = gravatarApiKey
    }
}
