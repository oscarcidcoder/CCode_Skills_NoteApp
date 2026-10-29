package com.example.notesapp.core.presentation

import com.example.notesapp.core.domain.DataError
import com.example.notesapp.core.domain.UiText
import com.example.notesapp.R

fun DataError.toUiText(): UiText {
    return when (this) {
        DataError.Network.NO_INTERNET -> UiText.StringResource(R.string.error_no_internet)
        DataError.Network.SERVER_ERROR -> UiText.StringResource(R.string.error_server)
        DataError.Network.UNAUTHORIZED -> UiText.StringResource(R.string.error_unauthorized)
        DataError.Network.REQUEST_TIMEOUT -> UiText.StringResource(R.string.error_timeout)
        DataError.Network.NOT_FOUND -> UiText.StringResource(R.string.error_not_found)
        DataError.Network.CONFLICT -> UiText.StringResource(R.string.error_conflict)
        DataError.Network.BAD_REQUEST -> UiText.StringResource(R.string.error_bad_request)
        DataError.Network.TOO_MANY_REQUESTS -> UiText.StringResource(R.string.error_too_many_requests)
        DataError.Network.PAYLOAD_TOO_LARGE -> UiText.StringResource(R.string.error_payload_too_large)
        DataError.Network.SERVICE_UNAVAILABLE -> UiText.StringResource(R.string.error_service_unavailable)
        DataError.Network.SERIALIZATION -> UiText.StringResource(R.string.error_serialization)
        DataError.Local.DISK_FULL -> UiText.StringResource(R.string.error_disk_full)
        DataError.Local.NOT_FOUND -> UiText.StringResource(R.string.error_not_found)
        else -> UiText.StringResource(R.string.error_unknown)
    }
}

fun UiText.getString(resources: android.content.res.Resources): String {
    return when (this) {
        is UiText.DynamicString -> value
        is UiText.StringResource -> resources.getString(id, *args)
    }
}