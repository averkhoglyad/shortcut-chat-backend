package io.averkhoglyad.shortcut.users.config

import io.averkhoglyad.shortcut.users.outbox.MessageOutboxHandler
import net.javacrumbs.shedlock.core.LockProvider
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.slf4j.LoggerFactory
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
    private val messageOutboxHandler: MessageOutboxHandler
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun lockProvider(jdbc: JdbcTemplate): LockProvider {
        return JdbcTemplateLockProvider(jdbc)
    }

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
        wrapUnsafe { messageOutboxHandler.handleNext() }
    }

    @Scheduled(cron = "\${scheduler.message-outbox-cleaner.cron}")
    @SchedulerLock(
        name = "message-outbox-cleaner",
        lockAtLeastFor = "\${scheduler.message-outbox-cleaner.lock-at-least-for:PT1M}",
        lockAtMostFor = "\${scheduler.message-outbox-cleaner.lock-at-most-for:PT10M}"
    )
    fun messageOutboxCleaner() {
        wrapUnsafe { messageOutboxHandler.cleanOld() }
    }

    private inline fun wrapUnsafe(block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            logger.error("Error during cron-job execution: ", e);
        }
    }
}