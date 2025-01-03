package io.averkhoglyad.shortcut.users.core.service.message

import io.averkhoglyad.shortcut.users.core.model.Message

interface MessageFactory<S, R> {

    fun create(user: S): Message<R>

}