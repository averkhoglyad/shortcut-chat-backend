package io.averkhoglyad.shortcut.chat

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(TestcontainersConfiguration::class)
class ChatAppTest(val ctx: ApplicationContext) : FreeSpec({

    "context loads" {
        ctx.getBean(ChatApp::class.java)
            .shouldNotBeNull()
    }
})