package io.averkhoglyad.shortcut.common.util

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class FlowTest : FreeSpec({

    "windowed" {
        val original = flow {
            var i = 0
            while(coroutineContext.isActive) {
                delay(50)
                repeat(10) {
                    emit(i++)
                }
                delay(50)
            }
        }

        val result: List<List<Int>> = original
            .windowed(100.milliseconds)
            .take(5)
            .toList()

        result shouldHaveSize 5
        result.forEach {
            it shouldHaveSize 10
        }

        result.flatMap { it }
            .forEachIndexed { index, el -> el shouldBeEqual index }
    }
})
