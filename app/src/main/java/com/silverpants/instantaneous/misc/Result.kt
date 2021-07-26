package com.silverpants.instantaneous.misc

import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>() {
        init {
            FirebaseCrashlytics.getInstance().recordException(exception)
        }
    }

    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }
}

/**
 * `true` if [Result] is of type [Result.Success] & holds non-null [Result.Success.data].
 */
val Result<*>.succeeded
    get() = this is Result.Success && data != null

fun <T> Result<T>.successOr(fallback: T): T {
    return (this as? Result.Success<T>)?.data ?: fallback
}

val <T> Result<T>.data: T?
    get() = (this as? Result.Success)?.data

/**
 * Updates value of [liveData] if [Result] is of type [Result.Success]
 */
inline fun <reified T> Result<T>.updateOnSuccess(liveData: MutableLiveData<T>) {
    if (this is Result.Success) {
        liveData.value = data
    }
}