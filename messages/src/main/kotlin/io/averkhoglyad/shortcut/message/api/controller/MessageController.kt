package io.averkhoglyad.shortcut.message.api.controller

import io.averkhoglyad.shortcut.common.web.handler.unwrap
import io.averkhoglyad.shortcut.message.api.data.SliceResponse
import io.averkhoglyad.shortcut.message.core.model.Message
import io.averkhoglyad.shortcut.message.core.model.MessageRequest
import io.averkhoglyad.shortcut.message.core.model.UserRef
import io.averkhoglyad.shortcut.message.core.service.MessageService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("messages")
class MessageController(
    private val service: MessageService
) {

    @GetMapping(params = ["!lastId"])
    fun list(
        @RequestParam("chat") chatId: UUID,
        @RequestParam("pageSize") pageSize: Int = 10,
    ): SliceResponse<Message> {
        return service.listForChat(chatId, pageSize)
            .asSliceResponse()
    }

    @GetMapping(params = ["lastId"])
    fun list(
        @RequestParam("chat") chatId: UUID,
        @RequestParam("pageSize") pageSize: Int = 10,
        @RequestParam("lastId") lastId: UUID
    ): SliceResponse<Message> {
        return service.listForChat(chatId, lastId, pageSize)
            .asSliceResponse()
    }

    @PostMapping
    fun create(@RequestHeader("X-User-Id") userId: UUID,
               @RequestBody message: MessageRequest): Message {
        return service.create(message, UserRef(userId))
            .unwrap()
    }
}

private fun List<Message>.asSliceResponse(): SliceResponse<Message> =
    SliceResponse(
        data = this,
        next = this.lastOrNull()?.id?.toString()
    )