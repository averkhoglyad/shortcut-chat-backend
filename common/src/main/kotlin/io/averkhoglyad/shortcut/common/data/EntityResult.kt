package io.averkhoglyad.shortcut.common.data

sealed class EntityResult<out E> {

    data class Success<E>(val entity: E) : EntityResult<E>()
    object NotFound : EntityResult<Nothing>()
    data class Invalid<E>(val message: String) : EntityResult<E>()
    data class Conflict<E>(val message: String) : EntityResult<E>()
    data class Failed<E>(val ex: Exception) : EntityResult<E>()

}