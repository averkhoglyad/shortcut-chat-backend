package io.averkhoglyad.shortcut.notification.integration

import io.averkhoglyad.shortcut.notification.data.ChatMembers
import io.averkhoglyad.shortcut.notification.data.UserRef
import org.springframework.lang.NonNull
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import java.util.*

interface ChatRepository {

    fun members(chatIds: Collection<UUID>): Map<UUID, Collection<UserRef>>

}

@Component
class ChatRepositoryImpl(
    private val chatMembersEndpoint: ChatMembersEndpoint
): ChatRepository {

    override fun members(chatIds: Collection<UUID>): Map<UUID, Collection<UserRef>> {
        return chatMembersEndpoint.members(chatIds)
            .associate { it -> it.id to it.members }
    }
}

@HttpExchange("/members")
interface ChatMembersEndpoint {
    @GetExchange
    @NonNull
    fun members(@RequestParam("chatId") chatIds: Collection<UUID>): Collection<ChatMembers>
}
