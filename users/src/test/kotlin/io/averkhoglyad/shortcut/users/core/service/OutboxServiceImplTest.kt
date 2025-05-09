package io.averkhoglyad.shortcut.users.core.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.averkhoglyad.shortcut.users.outbox.OutboxMessage
import io.averkhoglyad.shortcut.users.core.persistence.entity.OutboxMessageEntity
import io.averkhoglyad.shortcut.users.core.persistence.repository.OutboxMessageRepository
import io.averkhoglyad.shortcut.users.outbox.OutboxServiceImpl
import io.averkhoglyad.shortcut.users.test.firstArg
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.mockk.*

class OutboxServiceImplTest: FreeSpec({

    val repository = mockk<OutboxMessageRepository>()
    val objectMapper = mockk<ObjectMapper>()

    val service = OutboxServiceImpl(
        repository = repository,
        objectMapper = objectMapper
    )

    afterTest {
        clearAllMocks()
    }

    "saveMessage correctly serializes and passes message to repository" {
        // given
        val message = nextMessage()
        val serializedMessageBody = byteArrayOf(1)

        every { objectMapper.writeValueAsBytes(any()) } returns serializedMessageBody
        every { repository.save(any()) } answers firstArg()

        // when
        service.saveMessage(message)

        // then
        val entitySlot = slot<OutboxMessageEntity>()
        verify { repository.save(capture(entitySlot)) }
        entitySlot.captured should {
            it.type shouldBe message.type
            it.version shouldBe message.version
            it.body shouldBeSameInstanceAs serializedMessageBody
        }
    }
})

private fun nextMessage() = OutboxMessage<Any>(
    type = Arb.string().next(),
    version = Arb.string().next(),
    body = Any()
)
