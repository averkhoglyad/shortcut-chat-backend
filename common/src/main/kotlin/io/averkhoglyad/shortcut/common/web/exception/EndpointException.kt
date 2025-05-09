@file:Suppress("NOTHING_TO_INLINE")

package io.averkhoglyad.shortcut.common.web.exception

import org.springframework.http.HttpStatus

class EndpointException(
    val status: HttpStatus,
    message: String = "",
    val details: String = "",
    cause: Throwable? = null
) : Exception(message, cause)

inline fun notFound(message: String = "", details: String = ""): EndpointException =
    endpointError(
        status = HttpStatus.NOT_FOUND,
        message = message,
        details = details
    )

inline fun badRequest(message: String = "", details: String = ""): EndpointException =
    endpointError(
        status = HttpStatus.BAD_REQUEST,
        message = message,
        details = details
    )

inline fun internalError(message: String = "", details: String = ""): EndpointException =
    endpointError(
        status = HttpStatus.INTERNAL_SERVER_ERROR,
        message = message,
        details = details
    )

inline fun conflict(message: String = "", details: String = ""): EndpointException =
    endpointError(
        status = HttpStatus.CONFLICT,
        message = message,
        details = details
    )

inline fun endpointError(status: HttpStatus, message: String = "", details: String = ""): EndpointException =
    EndpointException(
        status = status,
        message = message,
        details = details
    )
