package io.averkhoglyad.shortcut.chat.outbox

import io.averkhoglyad.shortcut.chat.core.persistence.repository.OutboxMessageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Period

// TODO: Must be moved to some common transactional-outbox lib
interface MessageOutboxCleaner {

    fun cleanOld()

}

@Component
class MessageOutboxCleanerImpl(
    private val repository: OutboxMessageRepository,
    private val validPeriod: Period,
    private val clock: Clock
) : MessageOutboxCleaner {

    @Autowired
    constructor(repository: OutboxMessageRepository,
                @Value("\${scheduler.message-outbox-cleaner.valid-period}")
                validPeriod: Period): this(repository, validPeriod, Clock.systemUTC())

    override fun cleanOld() {
        repository.deleteByPublishedAtLessThan(clock.instant() - validPeriod)
    }
}
