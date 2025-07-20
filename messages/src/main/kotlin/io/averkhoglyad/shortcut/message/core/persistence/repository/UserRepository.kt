package io.averkhoglyad.shortcut.message.core.persistence.repository

import io.averkhoglyad.shortcut.message.core.persistence.entity.UserEntity
import org.springframework.data.repository.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface UserRepository : Repository<UserEntity, UUID> {

    @Transactional(readOnly = true)
    fun findById(id: UUID): UserEntity?

    @Transactional
    fun save(user: UserEntity): UserEntity

}