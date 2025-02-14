package com.twapps.serverstatuschecker.util

import com.twapps.serverstatuschecker.util.Failable.Failure.Companion.toFailure
import com.twapps.serverstatuschecker.util.Failable.Success.Companion.toSuccess
import kotlinx.serialization.Serializable

@Serializable
sealed class Failable<T: Any> {
    @Serializable
    class Success<T: Any> constructor(
        val value: T,
    ) : Failable<T>() {
        companion object {
            fun<T: Any> T.toSuccess(): Success<T> = Success(this)
        }
    }

    @Serializable
    class Failure<T: Any> constructor(
        val error: String?
    ): Failable<T>() {
        companion object {
            fun<T: Any> Throwable.toFailure(): Failure<T> = Failure("${this::class.simpleName}: ${this.message}")
        }
    }
}

suspend fun <T: Any> tryOrFailSuspend(block: suspend () -> T): Failable<T> {
    return try {
        block().toSuccess()
    } catch (exception: Exception) {
        exception.toFailure()
    }
}

fun <T: Any> tryOrFail(block: () -> T): Failable<T> {
    return try {
        block().toSuccess()
    } catch (exception: Exception) {
        exception.toFailure()
    }
}

suspend fun <T: Any> tryOrFailFailable(block: suspend () -> Failable<T>): Failable<T> {
    return try {
        return block()
    } catch (exception: Exception) {
        exception.toFailure()
    }
}

suspend fun <T: Any> tryOrNull(block: suspend () -> T): T? {
    return try {
        block()
    } catch (exception: Exception) {
        exception.printStackTrace()
        null
    }
}