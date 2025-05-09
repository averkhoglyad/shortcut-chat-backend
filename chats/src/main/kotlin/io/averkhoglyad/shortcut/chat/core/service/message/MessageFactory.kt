package io.averkhoglyad.shortcut.chat.core.service.message

import io.averkhoglyad.shortcut.chat.outbox.OutboxMessage

interface MessageFactory<S, R> {

    fun create(body: S): OutboxMessage<R>

}