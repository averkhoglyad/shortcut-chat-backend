package io.averkhoglyad.shortcut.users.outbox

import io.averkhoglyad.shortcut.users.core.persistence.repository.OutboxMessageRepository
import io.kotest.core.spec.style.FreeSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.instant
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.mockk.*
import java.time.Clock
import java.time.Instant
import java.time.Period

class MessageOutboxCleanerImplTest: FreeSpec({

    val repository = mockk<OutboxMessageRepository>()
    val clock: Clock = mockk<Clock>()

    "cleanOld call repository correctly" {
        checkAll(nowAndPeriodPairs) { (now, period) ->
            // given
            every { clock.instant() } returns now
            every { repository.deleteByPublishedAtLessThan(any()) } just runs
            val cleaner = MessageOutboxCleanerImpl(repository, period, clock)

            // when
            cleaner.cleanOld()

            // then
            verify { clock.instant() }
            verify { repository.deleteByPublishedAtLessThan(now - period) }
            confirmVerified(repository, clock)

            // afterTest
            clearAllMocks()
        }
    }
})

private val nowAndPeriodPairs: Arb<Pair<Instant, Period>> = Arb.bind(
    Arb.instant(Instant.EPOCH, Instant.now()),
    Arb.int(10..1000)
) { now, days ->
    now to Period.ofDays(days)
}

