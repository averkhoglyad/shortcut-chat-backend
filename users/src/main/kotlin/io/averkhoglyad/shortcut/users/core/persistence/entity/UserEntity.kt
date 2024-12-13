package io.averkhoglyad.shortcut.users.core.persistence.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("users")
class UserEntity: IdentifiedEntity {

    @Id
    override var id: UUID? = null
    var name: String = ""
    var email: String = ""

}