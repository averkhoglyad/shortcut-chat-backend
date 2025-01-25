package io.averkhoglyad.shortcut.mail.sender.core.mailer

import io.averkhoglyad.shortcut.mail.sender.test.emailMessages
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.email
import io.kotest.property.checkAll

class MailMessageConverterFactoryImplTest : FreeSpec({

    val factory = MailMessageConverterFactoryImpl()

    "create" - {
        "returns SimpleMailMessage with correct data" {
            checkAll(arguments) { (from, message) ->
                val result = factory.create(from, message)

                result.from shouldBe from
                result.to shouldContainExactly message.to.toTypedArray()
                result.subject shouldBe message.subject
                result.text shouldBe message.body
            }
        }
    }
})

private val arguments = Arb.bind(
    Arb.email(),
    emailMessages
) { from, msg ->
    from to msg
}
