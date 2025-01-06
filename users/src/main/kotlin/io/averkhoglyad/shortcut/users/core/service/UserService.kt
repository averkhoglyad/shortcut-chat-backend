package io.averkhoglyad.shortcut.users.core.service

import io.averkhoglyad.shortcut.users.core.converter.UserConverter
import io.averkhoglyad.shortcut.users.core.model.User
import io.averkhoglyad.shortcut.users.core.persistence.repository.UserRepository
import io.averkhoglyad.shortcut.users.core.service.message.SendCreatedUserNotificationMessageFactoryImpl
import io.averkhoglyad.shortcut.users.core.service.message.UserCreatedMessageFactoryImpl
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(
    private val repository: UserRepository,
    private val converter: UserConverter,
    private val userCreatedMessageFactory: UserCreatedMessageFactoryImpl,
    private val sendCreatedUserNotificationMessageFactory: SendCreatedUserNotificationMessageFactoryImpl,
    private val messageOutboxService: OutboxService
) {

    @Transactional(readOnly = true)
    fun find(id: UUID): User {
        var entity = repository.findById(id)
            ?: throw RuntimeException("User with id $id not found")
        return converter.fromEntity(entity)
    }

    @Transactional
    fun create(user: User): User {
        require(user.id == null)

        return converter.toEntity(user)
            .let { repository.save(it) }
            .let { converter.fromEntity(it) }
            .also { sendNotification(it) }
            .also { emitUserCreatedLifecycleEvent(it) }
    }

    private fun sendNotification(user: User) {
        messageOutboxService.saveMessage(
            message = userCreatedMessageFactory.create(user)
        )
    }

    private fun emitUserCreatedLifecycleEvent(user: User) {
        messageOutboxService.saveMessage(
            message = sendCreatedUserNotificationMessageFactory.create(user)
        )
    }

    @Transactional
    fun update(user: User): User {
        requireNotNull(user.id)

        val entity = repository.findById(user.id)
            ?.let { converter.toEntity(user, it) }
            ?: throw RuntimeException("User with id ${user.id} not found")

        return repository.save(entity)
            .let { converter.fromEntity(it) }
            .also { emitUserUpdatedLifecycleEvent(it) }
    }

    private fun emitUserUpdatedLifecycleEvent(user: User) {
        // TODO: Add user updated lifecycle event
//        val message = userUpdatedMessageFactory.create(user)
//        messageOutboxService.saveMessage(message)
    }
}
