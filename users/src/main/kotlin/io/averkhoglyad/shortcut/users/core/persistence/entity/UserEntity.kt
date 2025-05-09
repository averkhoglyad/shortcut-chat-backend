package io.averkhoglyad.shortcut.users.core.persistence.entity

import io.averkhoglyad.shortcut.common.persistence.id.IdentifiedEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("users")
class UserEntity: IdentifiedEntity<UUID> {

    @Id
    override var id: UUID? = null
    var name: String = ""
    var email: String = ""

}