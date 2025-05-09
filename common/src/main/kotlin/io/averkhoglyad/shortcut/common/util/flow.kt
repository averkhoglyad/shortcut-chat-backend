package io.averkhoglyad.shortcut.common.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration

fun tickerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
    delay(initialDelay)
    while (coroutineContext.isActive) {
        emit(Unit)
        delay(period)
    }
}

@ExperimentalCoroutinesApi
fun <E> Flow<E>.windowed(period: Duration): Flow<List<E>> = this
    .runningFold(mutableListOf<E>()) { acc, el -> acc.apply { add(el) } }
    .transformLatest {
        delay(period)
        val result = it.toList()
        it.clear()
        emit(result)
    }
