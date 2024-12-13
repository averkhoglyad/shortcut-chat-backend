package io.averkhoglyad.shortcut.users.core.persistence.meta

import io.averkhoglyad.shortcut.users.core.persistence.entity.IdentifiedEntity
import org.springframework.core.annotation.Order
import org.springframework.data.relational.auditing.RelationalAuditingCallback
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback
import org.springframework.stereotype.Component

@Component
@Order(RelationalAuditingCallback.AUDITING_ORDER + 10)
class IdGenerationBeforeConvertCallback(
    private val idGenerator: IdGenerator
) : BeforeConvertCallback<IdentifiedEntity> {

    override fun onBeforeConvert(aggregate: IdentifiedEntity): IdentifiedEntity {
        if (aggregate.id == null) {
            aggregate.id = idGenerator.generate()
        }
        return aggregate
    }
}
