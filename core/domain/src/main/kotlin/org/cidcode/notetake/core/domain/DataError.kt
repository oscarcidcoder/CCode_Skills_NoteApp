package org.cidcode.notetake.core.domain

/**
 * Errors that originate in the data layer.
 */
sealed interface DataError : Error {

    enum class Local : DataError {
        DISK_FULL,
        NOT_FOUND,
        UNKNOWN
    }
}
