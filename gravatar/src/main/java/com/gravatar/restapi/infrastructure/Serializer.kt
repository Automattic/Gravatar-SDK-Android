package com.gravatar.restapi.infrastructure

import com.squareup.moshi.Moshi

internal object Serializer {
    @JvmStatic
    internal val moshiBuilder: Moshi.Builder = Moshi.Builder()
        .add(URIAdapter())

    @JvmStatic
    internal val moshi: Moshi by lazy {
        moshiBuilder.build()
    }
}
