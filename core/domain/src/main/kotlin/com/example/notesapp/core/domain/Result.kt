package com.example.notesapp.core.domain

// `Result.Error` (la clase anidada) oculta la interfaz `Error` del mismo paquete,
// por eso el bound se califica con el nombre completo.
sealed interface Result<out D, out E : com.example.notesapp.core.domain.Error> {
    data class Success<out D>(val data: D) : Result<D, Nothing>
    data class Error<out E : com.example.notesapp.core.domain.Error>(val error: E) : Result<Nothing, E>

    companion object {
        fun <D> success(data: D): Result<D, Nothing> = Success(data)
        fun <E : com.example.notesapp.core.domain.Error> error(error: E): Result<Nothing, E> =
            Error(error)
    }
}

// Un typealias no admite bounds en sus parametros; el bound de Result se valida al usarlo.
typealias EmptyResult<E> = Result<Unit, E>
