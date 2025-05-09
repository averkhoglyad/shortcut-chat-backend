package io.averkhoglyad.shortcut.chat.api.controller

import io.averkhoglyad.shortcut.chat.core.model.ChatMembers
import io.averkhoglyad.shortcut.chat.core.service.ChatService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/members")
class ChatMembersController(
    private val service: ChatService
) {

    @GetMapping
    fun members(@RequestParam("chatId") chatIds: Collection<UUID>): Collection<ChatMembers> {
        return service.findMembersByIds(chatIds)
    }
}