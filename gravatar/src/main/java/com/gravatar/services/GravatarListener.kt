package com.gravatar.services

/**
 * Generic Listener for Gravatar API call
 */
public interface GravatarListener<T> {
    /**
     * Called when the Gravatar API call is successful
     */
    public fun onSuccess(response: T)

    /**
     * Called when the Gravatar API call fails
     *
     * @param errorType The type of error that occurred
     */
    public fun onError(errorType: ErrorType)
}
