package io.averkhoglyad.shortcut.chat.api.controller

import io.averkhoglyad.shortcut.chat.api.data.SliceResponse
import io.averkhoglyad.shortcut.chat.core.model.ChatDetails
import io.averkhoglyad.shortcut.chat.core.model.ChatListItem
import io.averkhoglyad.shortcut.chat.core.model.ChatRequest
import io.averkhoglyad.shortcut.chat.core.service.ChatService
import io.averkhoglyad.shortcut.common.web.handler.unwrap
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/chats")
class ChatController(
    private val service: ChatService
) {

    @GetMapping(params = ["!lastId"])
    fun list(
        @RequestHeader("X-User-Id") userId: UUID,
        @RequestParam("pageSize") pageSize: Int = 10,
    ): SliceResponse<ChatListItem> {
        return service.listForUser(userId, pageSize)
            .asSliceResponse()
    }

    @GetMapping(params = ["lastId"])
    fun list(
        @RequestHeader("X-User-Id") userId: UUID,
        @RequestParam("pageSize") pageSize: Int = 10,
        @RequestParam("lastId") lastId: UUID
    ): SliceResponse<ChatListItem> {
        return service.listForUser(userId, lastId, pageSize)
            .asSliceResponse()
    }

    @GetMapping("/{id}")
    fun details(@PathVariable id: UUID): ChatDetails {
        return service.findById(id)
            .unwrap()
    }

    @PostMapping
    fun create(@RequestBody chat: ChatRequest): ChatDetails {
        return service.create(chat)
            .unwrap()
    }

    // TODO: update method
}

private fun List<ChatListItem>.asSliceResponse() =
    SliceResponse(data = this, next = this.lastOrNull()?.id?.toString())