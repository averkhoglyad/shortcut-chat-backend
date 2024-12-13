package io.averkhoglyad.shortcut.users.core.persistence.repository

import io.averkhoglyad.shortcut.users.core.persistence.entity.UserEntity
import org.springframework.data.repository.Repository
import java.util.UUID

interface UserRepository: Repository<UserEntity, UUID> {

    fun findById(id: UUID): UserEntity?

    fun save(user: UserEntity): UserEntity

}