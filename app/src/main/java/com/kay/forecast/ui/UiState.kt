package com.kay.forecast.ui

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class UiState<out R> {

    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val exception: Throwable) : UiState<Nothing>()
    object Loading : UiState<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }
}

/**
 * `true` if [UiState] is of type [Success] & holds non-null [Success.data].
 */
val UiState<*>.succeeded
    get() = this is UiState.Success && data != null


val UiState<*>.isLoading
    get() = this is UiState.Loading

fun <T> UiState<T>.successOr(fallback: T): T {
    return (this as? UiState.Success<T>)?.data ?: fallback
}