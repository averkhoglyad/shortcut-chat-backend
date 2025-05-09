package io.averkhoglyad.shortcut.common.test

import java.sql.Timestamp
import java.time.Instant

fun Any.asInstantColumn(): Instant {
    return when (this) {
        is Instant -> this
        is Timestamp -> this.toInstant()
        else -> throw IllegalStateException()
    }
}
