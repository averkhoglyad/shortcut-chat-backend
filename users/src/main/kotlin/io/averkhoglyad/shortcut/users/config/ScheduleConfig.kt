package io.averkhoglyad.shortcut.users.config

import io.averkhoglyad.shortcut.common.util.slf4j
import io.averkhoglyad.shortcut.users.outbox.MessageOutboxCleaner
import io.averkhoglyad.shortcut.users.outbox.MessageOutboxHandler
import net.javacrumbs.shedlock.core.LockProvider
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@Configuration
@EnableScheduling
@EnableSchedulerLock(
    defaultLockAtLeastFor = "\${scheduler.default-lock.lock-at-least-for:PT0S}",
    defaultLockAtMostFor = "\${scheduler.default-lock.lock-at-most-for:PT10M}"
)
@Profile("!test")
class ScheduleConfig(
    private val messageOutboxHandler: MessageOutboxHandler,
    private val messageOutboxCleaner: MessageOutboxCleaner
) {

    private val logger by slf4j()

    @Bean
    fun lockProvider(jdbc: JdbcTemplate): LockProvider = JdbcTemplateLockProvider(jdbc)

    @Scheduled(
        initialDelayString = "\${scheduler.message-outbox-handler.initial-delay}",
        fixedDelayString = "\${scheduler.message-outbox-handler.fixed-delay}"
    )
    @SchedulerLock(
        name = "message-outbox-handler",
        lockAtLeastFor = "\${scheduler.message-outbox-handler.lock-at-least-for:PT1S}",
        lockAtMostFor = "\${scheduler.message-outbox-handler.lock-at-most-for:PT10M}"
    )
    fun messageOutboxHandler() {
        runCatching { messageOutboxHandler.handleNext() }
            .onFailure { logger.error("Error during message-outbox-handler cron-job execution: ", it) }
    }

    @Scheduled(cron = "\${scheduler.message-outbox-cleaner.cron}")
    @SchedulerLock(
        name = "message-outbox-cleaner",
        lockAtLeastFor = "\${scheduler.message-outbox-cleaner.lock-at-least-for:PT1M}",
        lockAtMostFor = "\${scheduler.message-outbox-cleaner.lock-at-most-for:PT10M}"
    )
    fun messageOutboxCleaner() {
        runCatching { messageOutboxCleaner.cleanOld() }
            .onFailure { logger.error("Error during message-outbox-cleaner cron-job execution: ", it) }
    }
}