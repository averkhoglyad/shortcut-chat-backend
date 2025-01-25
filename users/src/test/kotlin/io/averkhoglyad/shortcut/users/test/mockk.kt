package io.averkhoglyad.shortcut.users.test

import io.mockk.Call
import io.mockk.MockKAnswerScope

inline fun <reified R> firstArg(): MockKAnswerScope<*, *>.(Call) -> R = { it.invocation.args[0] as R }