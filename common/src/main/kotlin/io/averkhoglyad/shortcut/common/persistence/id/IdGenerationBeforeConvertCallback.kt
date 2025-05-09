package io.averkhoglyad.shortcut.common.persistence.id

import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback
import java.util.function.Supplier

class IdGenerationBeforeConvertCallback<I>(
    private val idGenerator: () -> I
) : BeforeConvertCallback<IdentifiedEntity<I>> {

    constructor(generator: Supplier<I>) : this(generator::get)

    override fun onBeforeConvert(aggregate: IdentifiedEntity<I>): IdentifiedEntity<I> {
        if (aggregate.id == null) {
            aggregate.id = idGenerator.invoke()
        }
        return aggregate
    }
}
