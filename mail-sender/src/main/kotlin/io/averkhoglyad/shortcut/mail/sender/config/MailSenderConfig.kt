package io.averkhoglyad.shortcut.mail.sender.config

import io.averkhoglyad.shortcut.mail.sender.core.mailer.NoopMailSender
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.MailSender

@Configuration
class MailSenderConfig {

    private val logger = LoggerFactory.getLogger(MailSenderConfig::class.java)

    @Bean
    @ConditionalOnMissingBean
    fun mailSender(): MailSender {
        logger.warn("No MailSender is initialized, NoopMailSender is used")
        return NoopMailSender()
    }
}