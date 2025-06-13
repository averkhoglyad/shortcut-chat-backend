package io.averkhoglyad.shortcut.notification.data

import com.fasterxml.jackson.annotation.JsonGetter
import io.averkhoglyad.shortcut.common.util.UUID_ZERO_VALUE
import java.time.Instant
import java.util.*

sealed interface ChatLifecycleEvent {
    val chat: ChatRef
    val createdAt: Instant

    @get:JsonGetter("@type")
    val type: String
        get() = this.javaClass.simpleName
}

class DebugEvent(
    override val chat: ChatRef = ChatRef(UUID_ZERO_VALUE),
    override val createdAt: Instant = Instant.now(),
): ChatLifecycleEvent

data class ChatCreated(
    val id: UUID,
    val name: String,
    val owner: UserRef?,
    val members: Collection<UserRef>,
    override val createdAt: Instant,
) : ChatLifecycleEvent {
    override val chat: ChatRef = ChatRef(id)
}

data class MessagePublished(
    val id: UUID,
    val text: String,
    override val chat: ChatRef,
    val author: UserRef,
    override val createdAt: Instant,
) : ChatLifecycleEvent

data class ChatRef(val id: UUID)

data class UserRef(val id: UUID)
