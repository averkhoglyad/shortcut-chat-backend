package io.averkhoglyad.shortcut.users.core.persistence.meta

import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class IdGenerator {

    fun generate(): UUID {
        return Generators.timeBasedEpochGenerator().generate()
    }
}
