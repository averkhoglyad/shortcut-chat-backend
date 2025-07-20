package io.averkhoglyad.shortcut.common.util

import java.util.*

val UUID_ZERO_VALUE = UUID(0, 0)
val UUID_MIN_VALUE = UUID(Long.MIN_VALUE, Long.MIN_VALUE)
val UUID_MAX_VALUE = UUID(Long.MAX_VALUE, Long.MAX_VALUE)

val UUID.isMin
    get() = this == UUID_MIN_VALUE
val UUID.isMax
    get() = this == UUID_MAX_VALUE
val UUID.isZero
    get() = this == UUID_ZERO_VALUE

fun String.toUUID(): UUID = UUID.fromString(this)
