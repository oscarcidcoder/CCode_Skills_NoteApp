package org.cidcode.notetake.core.domain

/**
 * Typed success/failure wrapper usable in every layer.
 * Expected failures are returned as [Error] — never thrown.
 */
sealed interface Result<out D, out E : org.cidcode.notetake.core.domain.Error> {
    data class Success<out D>(val data: D) : Result<D, Nothing>
    data class Error<out E : org.cidcode.notetake.core.domain.Error>(val error: E) :
        Result<Nothing, E>
}

typealias EmptyResult<E> = Result<Unit, E>
