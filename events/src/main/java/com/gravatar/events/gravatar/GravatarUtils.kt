package com.gravatar.events.gravatar

import java.net.URL

fun parseGravatarHash(urlString: String): String? {
    try {
        val url = URL(urlString)
        if (url.host == "gravatar.com") {
            val path = url.path.trim('/')
            return path.ifEmpty {
                null
            }
        }
    } catch (e: Exception) {
        // URL parsing failed
    }
    return null
}