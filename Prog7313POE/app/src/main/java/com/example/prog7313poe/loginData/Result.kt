package com.example.prog7313poe.loginData

/**
 * A generic class that holds a value with its loading status.
 */
sealed class Result<out T : Any> {

    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()

    /**
     * Helper to wrap synchronous calls into a [Result].
     */
    companion object {
        inline fun <T : Any> of(block: () -> T): Result<T> =
            try {
                Success(block())
            } catch (e: Exception) {
                Error(e)
            }
    }

    override fun toString(): String = when (this) {
        is Success<*> -> "Success[data=$data]"
        is Error -> "Error[exception=$exception]"
    }
}
