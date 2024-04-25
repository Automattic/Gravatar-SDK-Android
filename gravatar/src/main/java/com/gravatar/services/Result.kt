package com.gravatar.services

/**
 * Class representing the result of a network operation.
 */
public sealed class Result<T, E> {
    /**
     * Represents a successful fetch operation.
     *
     * @property value The fetched data
     */
    public data class Success<T, E>(public val value: T) : Result<T, E>()

    /**
     * Represents a failed fetch operation.
     *
     * @property error The error that occurred
     */
    public data class Failure<T, E>(public val error: E) : Result<T, E>()

    /**
     * Shortcut function to ignore a failure.
     *
     * @return The value if the result is a [Success] or null otherwise
     */
    public fun valueOrNull(): T? = when (this) {
        is Success -> value
        is Failure -> null
    }
}
