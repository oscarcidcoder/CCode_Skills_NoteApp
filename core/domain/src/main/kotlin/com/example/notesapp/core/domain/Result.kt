package com.example.notesapp.core.domain

sealed interface Result<out D, out E : Error> {
    data class Success<out D>(val data: D) : Result<D, Nothing>
    data class Error<out E : Error>(val error: E) : Result<Nothing, E>

    companion object {
        fun <D> success(data: D): Result<D, Nothing> = Success(data)
        fun <E : Error> error(error: E): Result<Nothing, E> = Error(error)
    }
}

typealias EmptyResult<E : Error> = Result<Unit, E>