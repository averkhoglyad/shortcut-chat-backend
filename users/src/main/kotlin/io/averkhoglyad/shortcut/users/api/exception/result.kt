@file:Suppress("NOTHING_TO_INLINE")
package io.averkhoglyad.shortcut.users.api.exception

import org.springframework.http.HttpStatus


inline fun notFound(message: String = "", details: String = ""): Nothing {
    throw EndpointException(
        status = HttpStatus.NOT_FOUND,
        message = message,
        details = details
    )
}

inline fun badRequest(message: String = "", details: String = ""): Nothing {
    throw EndpointException(
        status = HttpStatus.BAD_REQUEST,
        message = message,
        details = details
    )
}

inline fun internalError(message: String = "", details: String = ""): Nothing {
    throw EndpointException(
        status = HttpStatus.INTERNAL_SERVER_ERROR,
        message = message,
        details = details
    )
}

inline fun conflict(message: String = "", details: String = ""): Nothing {
    throw EndpointException(
        status = HttpStatus.CONFLICT,
        message = message,
        details = details
    )
}

inline fun endpointError(status: HttpStatus, message: String = "", details: String = ""): Nothing {
    throw EndpointException(
        status = status,
        message = message,
        details = details
    )
}
