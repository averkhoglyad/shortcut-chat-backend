package io.averkhoglyad.shortcut.users.core.service

import io.averkhoglyad.shortcut.users.core.converter.UserConverter
import io.averkhoglyad.shortcut.users.core.data.EntityResult
import io.averkhoglyad.shortcut.users.core.model.User
import io.averkhoglyad.shortcut.users.core.persistence.entity.UserEntity
import io.averkhoglyad.shortcut.users.core.persistence.repository.UserRepository
import io.averkhoglyad.shortcut.users.core.service.message.SendCreatedUserNotificationMessageFactoryImpl
import io.averkhoglyad.shortcut.users.core.service.message.UserCreatedMessageFactoryImpl
import io.averkhoglyad.shortcut.users.util.slf4j
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

    private val logger by slf4j()

    @Transactional(readOnly = true)
    fun find(id: UUID): EntityResult<User> {
        return repository.findById(id)
            ?.let { EntityResult.Success(it.toModel()) }
            ?: EntityResult.NotFound(id)
    }

    @Transactional
    fun create(user: User): EntityResult<User> {
        require(user.id == null)

        return repository.save(user.toEntity())
            .toModel()
            .also { emitUserCreatedLifecycleEvent(it) }
            .also { emitUserCreatedStreamingEvent(it) }
            .also { sendNotification(it) }
            .let { EntityResult.Success(it) }
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

    private fun emitUserCreatedStreamingEvent(user: User) {
        // TODO: Add user creates streaming event
        logger.info("User ${user.id} created - streaming event")
//        messageOutboxService.saveMessage(
//            message = sendCreatedUserNotificationMessageFactory.create(user)
//        )
    }

    @Transactional
    fun update(user: User): EntityResult<User> {
        requireNotNull(user.id)

        val entity = repository.findById(user.id)
            ?: return EntityResult.NotFound(user.id)

        return repository.save(user.toEntity(entity))
            .toModel()
            .also { emitUserUpdatedLifecycleEvent(it) }
            .also { emitUserUpdatedStreamingEvent(it) }
            .let { EntityResult.Success(it) }
    }

    private fun emitUserUpdatedLifecycleEvent(user: User) {
        // TODO: Add user updated lifecycle event
        logger.info("User ${user.id} updated - business event")
//        val message = userUpdatedMessageFactory.create(user)
//        messageOutboxService.saveMessage(message)
    }

    private fun emitUserUpdatedStreamingEvent(user: User) {
        // TODO: Add user updated streaming event
        logger.info("User ${user.id} updated - streaming event")
//        messageOutboxService.saveMessage(
//            message = sendCreatedUserNotificationMessageFactory.create(user)
//        )
    }

    private fun User.toEntity(): UserEntity = converter.toEntity(this)

    private fun User.toEntity(entity: UserEntity): UserEntity = converter.toEntity(this, entity)

    private fun UserEntity.toModel(): User = converter.fromEntity(this)
}

