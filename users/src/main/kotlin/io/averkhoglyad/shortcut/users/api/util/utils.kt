package io.averkhoglyad.shortcut.users.api.util

import io.averkhoglyad.shortcut.users.api.exception.badRequest
import io.averkhoglyad.shortcut.users.api.exception.conflict
import io.averkhoglyad.shortcut.users.api.exception.internalError
import io.averkhoglyad.shortcut.users.api.exception.notFound
import io.averkhoglyad.shortcut.users.core.data.EntityResult


fun <E> EntityResult<E>.unwrap(): E = when (this) {
    is EntityResult.Success<E> -> this.entity
    is EntityResult.NotFound<E> -> notFound()
    is EntityResult.Invalid<E> -> badRequest()
    is EntityResult.Conflict<E> -> conflict()
    is EntityResult.Failed<E> -> internalError()
}
