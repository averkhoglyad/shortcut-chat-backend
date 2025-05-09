package io.averkhoglyad.shortcut.users.core.service.message

import io.averkhoglyad.shortcut.users.outbox.OutboxMessage

interface MessageFactory<S, R> {

    fun create(user: S): OutboxMessage<R>

}