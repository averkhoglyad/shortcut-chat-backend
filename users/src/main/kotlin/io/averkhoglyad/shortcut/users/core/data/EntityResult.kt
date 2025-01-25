package io.averkhoglyad.shortcut.users.core.data

import java.util.UUID

sealed class EntityResult<E> {
    data class Success<E>(val entity: E) : EntityResult<E>()
    data class NotFound<E>(val id: UUID) : EntityResult<E>()
    data class Invalid<E>(val message: String) : EntityResult<E>()
    data class Conflict<E>(val message: String) : EntityResult<E>()
    data class Failed<E>(val ex: Exception) : EntityResult<E>()
}
