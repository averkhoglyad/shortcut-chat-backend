package io.averkhoglyad.shortcut.users.api.exception

import org.springframework.http.HttpStatus

class EndpointException(
    val status: HttpStatus,
    message: String = "",
    val details: String = "",
    cause: Throwable? = null
) : Exception(message, cause)

