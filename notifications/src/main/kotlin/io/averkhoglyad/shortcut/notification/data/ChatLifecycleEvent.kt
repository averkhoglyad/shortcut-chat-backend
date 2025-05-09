package io.averkhoglyad.shortcut.notification.data

import com.fasterxml.jackson.annotation.JsonGetter
import io.averkhoglyad.shortcut.common.util.UUID_ZERO_VALUE
import java.time.Instant
import java.util.*

sealed interface ChatLifecycleEvent {
    val chat: ChatRef
    @get:JsonGetter("@type")
    val type: String
        get() = this.javaClass.simpleName
}

object DebugEvent: ChatLifecycleEvent {
    override val chat: ChatRef = ChatRef(UUID_ZERO_VALUE)
}

data class ChatCreated(
    val id: UUID,
    val name: String,
    val owner: UserRef?,
    val members: Collection<UserRef>,
    val createdAt: Instant,
) : ChatLifecycleEvent {
    override val chat: ChatRef = ChatRef(id)
}

data class MessagePublished(
    val id: UUID,
    val text: String,
    override val chat: ChatRef,
    val author: UserRef,
    val createdAt: Instant,
) : ChatLifecycleEvent

data class ChatRef(val id: UUID)

data class UserRef(val id: UUID)
