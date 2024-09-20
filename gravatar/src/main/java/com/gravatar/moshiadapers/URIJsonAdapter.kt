package com.gravatar.moshiadapers

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.net.URI

internal class URIJsonAdapter {
    @ToJson
    public fun toJson(uri: URI): String {
        return uri.toString()
    }

    @FromJson
    public fun fromJson(uriString: String): URI {
        return URI(uriString)
    }
}
