package io.averkhoglyad.shortcut.mail.sender.core.mailer

import io.averkhoglyad.shortcut.mail.sender.test.emailMessages
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.should
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage

class MailSendServiceTest : FreeSpec({

    val emailSender = mockk<MailSender>()
    val messageFactory = mockk<MailMessageFactory>()
    val fromEmail = Arb.email().next()

    afterTest {
        clearAllMocks()
    }

    val service = MailSendService(
        emailSender = emailSender,
        messageFactory = messageFactory,
        from = fromEmail
    )

    "send" - {
        "throws IllegalArgumentException if no receiver email" {
            val message = emailMessages.next().copy(to = emptyList())
            shouldThrow<IllegalArgumentException> {
                service.send(message)
            }
        }

        "create MailMessage and pass to MailSender" {
            checkAll(emailMessages) { message ->
                // given
                val mailMessageForSender = SimpleMailMessage()
                every { messageFactory.create(any(), any()) } returns mailMessageForSender
                every { emailSender.send(any()) } just runs

                // when
                service.send(message)

                // then
                verify { messageFactory.create(fromEmail, message) }

                val mailMessageSlot = slot<SimpleMailMessage>()
                verify { emailSender.send(capture(mailMessageSlot)) }
                mailMessageSlot.captured should {
                    it shouldBeSameInstanceAs mailMessageForSender
                }

                // afterTest
                clearAllMocks()
            }
        }
    }
})
