package com.gravatar.services

/**
 * Exception that will be thrown when an error occurs during Gravatar operations.
 *
 * @property errorType The type of error that occurred.
 * @property originalException The original exception that caused this exception.
 */
public class GravatarException internal constructor(
    public val errorType: ErrorType,
    public val originalException: Exception? = null,
) : RuntimeException()
