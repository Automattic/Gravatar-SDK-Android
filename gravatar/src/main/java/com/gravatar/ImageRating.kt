package com.gravatar

/**
 * Gravatar allows users to self-rate their images so that they can indicate if an image is appropriate
 * for a certain audience. By default, only ‘General’ rated images are displayed unless you indicate that
 * you would like to see higher ratings.
 *
 * @property rating The rating to be used in the Gravatar URL.
 */
public enum class ImageRating(public val rating: String) {
    /** Suitable for display on all websites with any audience type. */
    General("g"),

    /** May contain rude gestures, provocatively dressed individuals, the lesser swear words, or mild violence. */
    ParentalGuidance("pg"),

    /** May contain such things as harsh profanity, intense violence, nudity, or hard drug use. */
    Restricted("r"),

    /** May contain hardcore sexual imagery or extremely disturbing violence. */
    X("x"),
}
