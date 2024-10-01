package com.gravatar

import java.net.URLEncoder
import java.util.Objects

/**
 * Class that represents the query options for an avatar.
 *
 * @property preferredSize Size of the avatar, must be between 1 and 2048. Optional: default to 80
 * @property defaultAvatarOption Default avatar image. Optional: default to Gravatar logo
 * @property rating Image rating. Optional: default to General, suitable for display on all websites with any audience
 * @property forceDefaultAvatar Force default avatar image. Optional: default to false
 */
public class AvatarQueryOptions private constructor(
    public val preferredSize: Int? = null,
    public val defaultAvatarOption: DefaultAvatarOption? = null,
    public val rating: ImageRating? = null,
    public val forceDefaultAvatar: Boolean? = null,
) {
    override fun hashCode(): Int {
        return Objects.hash(
            preferredSize,
            defaultAvatarOption,
            rating,
            forceDefaultAvatar,
        )
    }

    override fun toString(): String {
        return "AvatarQueryOptions(preferredSize=$preferredSize, defaultAvatarOption=$defaultAvatarOption, " +
            "rating=$rating, forceDefaultAvatar=$forceDefaultAvatar)"
    }

    override fun equals(other: Any?): Boolean {
        return other is AvatarQueryOptions && other.preferredSize == preferredSize &&
            other.defaultAvatarOption == defaultAvatarOption && other.rating == rating &&
            other.forceDefaultAvatar == forceDefaultAvatar
    }

    /**
     * Builder for [AvatarQueryOptions].
     */
    public class Builder {
        /**
         * Size of the avatar, must be between 1 and 2048. Optional: default to 80
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        public var preferredSize: Int? = null

        /**
         * Default avatar image. Optional: default to Gravatar logo
         */
        @set:JvmSynthetic
        public var defaultAvatarOption: DefaultAvatarOption? = null

        /**
         * Image rating. Optional: default to General, suitable for display on all websites with any audience
         */
        @set:JvmSynthetic
        public var rating: ImageRating? = null

        /**
         * Force default avatar image. Optional: default to false
         */
        @set:JvmSynthetic
        public var forceDefaultAvatar: Boolean? = null

        /**
         * Set the size of the avatar.
         */
        public fun setPreferredSize(preferredSize: Int?): Builder = apply { this.preferredSize = preferredSize }

        /**
         * Set the default avatar image.
         */
        public fun setDefaultAvatarOption(defaultAvatarOption: DefaultAvatarOption?): Builder =
            apply { this.defaultAvatarOption = defaultAvatarOption }

        /**
         * Set the image rating.
         */
        public fun setRating(rating: ImageRating?): Builder = apply { this.rating = rating }

        /**
         * Set whether to force the default avatar image.
         */
        public fun setForceDefaultAvatar(forceDefaultAvatar: Boolean?): Builder =
            apply { this.forceDefaultAvatar = forceDefaultAvatar }

        /**
         * Builds the [AvatarQueryOptions] instance.
         */
        public fun build(): AvatarQueryOptions = AvatarQueryOptions(
            preferredSize = preferredSize,
            defaultAvatarOption = defaultAvatarOption,
            rating = rating,
            forceDefaultAvatar = forceDefaultAvatar,
        )
    }
}

/**
 * Creates a new [AvatarQueryOptions] instance.
 */
@JvmSynthetic // Hide from Java callers who should use Builder.
public fun AvatarQueryOptions(initializer: AvatarQueryOptions.Builder.() -> Unit): AvatarQueryOptions {
    return AvatarQueryOptions.Builder().apply(initializer).build()
}

/**
 * Function to convert the [AvatarQueryOptions] to a string with the query parameters.
 */
public fun AvatarQueryOptions?.asQueryParameters(): String {
    val queryList = mutableListOf<String>()
    this?.defaultAvatarOption?.let {
        queryList.add("default=${URLEncoder.encode(it.queryParam(), "UTF-8")}")
    } // eg. default monster, "d=monsterid"
    this?.preferredSize?.let {
        queryList.add("size=$it")
    } // eg. size 42, "s=42"
    this?.rating?.let {
        queryList.add("rating=${it.rating}")
    } // eg. rated pg, "r=pg"
    this?.forceDefaultAvatar?.let {
        queryList.add("forcedefault=${if (it) "y" else "n"}")
    } // eg. force yes, "f=y"
    return if (queryList.isEmpty()) "" else queryList.joinToString("&", "?")
}
