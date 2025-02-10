package com.twapps.serverstatuschecker.util

object CollectionUtil {
    fun<T> List<T>.withoutFirst(match: (T) -> Boolean): List<T> {
        val matchedIndex = this.indexOfFirst(match)
        return when (matchedIndex) {
            -1 -> this
            else -> this.filterIndexed { index, t -> index != matchedIndex }
        }
    }
}