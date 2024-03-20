package com.gravatar.services

/**
 * Error types for Gravatar image upload
 */
public enum class ErrorType {
    /** server returned an error */
    SERVER,

    /** network request timed out */
    TIMEOUT,

    /** network is not available */
    NETWORK,

    /** User or hash not found */
    NOT_FOUND,

    /** An unknown error occurred */
    UNKNOWN,
}
