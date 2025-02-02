package io.averkhoglyad.shortcut.mail.sender.core.persistence

import io.averkhoglyad.shortcut.mail.sender.core.mailer.EmailMessage
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.time.Instant

private const val ONE_MONTH_IN_SECONDS: Long = 5 * 60// * 60 * 24 * 30

@RedisHash("messages", timeToLive = ONE_MONTH_IN_SECONDS)
data class MessageEntity(
    @Id
    val id: String,
    val payload: EmailMessage,
    val status: Status,
    val createdAt: Instant,
    val updatedAt: Instant,
    val exception: Exception? = null,
) {
    enum class Status {
        CREATED, COMPLETED, FAILED
    }
}

