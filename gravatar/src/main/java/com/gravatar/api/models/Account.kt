/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */
package com.gravatar.api.models

import com.google.gson.annotations.SerializedName

/**
 *
 *
 * @param url
 * @param name
 * @param domain
 * @param display
 * @param iconUrl
 * @param username
 * @param verified
 * @param shortname
 */

public data class Account(
    @SerializedName("url")
    val url: kotlin.String,
    @SerializedName("name")
    val name: kotlin.String,
    @SerializedName("domain")
    val domain: kotlin.String? = null,
    @SerializedName("display")
    val display: kotlin.String? = null,
    @SerializedName("iconUrl")
    val iconUrl: kotlin.String? = null,
    @SerializedName("username")
    val username: kotlin.String? = null,
    @SerializedName("verified")
    val verified: kotlin.String? = null,
    @SerializedName("shortname")
    val shortname: kotlin.String? = null,
)
