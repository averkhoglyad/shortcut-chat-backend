package io.averkhoglyad.shortcut.common.util

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.ints.shouldBeZero
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class FlowTest : FreeSpec({

    "tickerFlow" - {
        "with initial delay" - {
            "should not emit before initial delay" {
                runTest {
                    val initialDelay = 500.milliseconds
                    val period = 1000.milliseconds
                    var ticks = 0

                    launch {
                        tickerFlow(period, initialDelay)
                            .take(1)
                            .collect { ticks++ }
                    }

                    advanceTimeBy(initialDelay - 1.milliseconds)
                    ticks.shouldBeZero()
                }
            }

            "should emit first value after initial delay" {
                runTest {
                    val initialDelay = 500.milliseconds
                    val period = 1000.milliseconds
                    var ticks = 0

                    launch {
                        tickerFlow(period, initialDelay)
                            .take(1)
                            .collect { ticks++ }
                    }

                    advanceTimeBy(initialDelay + 1.milliseconds)
                    ticks shouldBe 1
                }
            }
        }

        "with regular period" - {
            "should emit values at correct intervals" {
                runTest {
                    val period = 200.milliseconds
                    var ticks = 0;

                    launch {
                        tickerFlow(period)
                            .take(3)
                            .collect { ticks++ }
                    }

                    // first tick
                    advanceTimeBy(1.milliseconds)
                    ticks shouldBe 1

                    // second tick after first period
                    advanceTimeBy(period)
                    ticks shouldBe 2

                    // third tick after next period
                    advanceTimeBy(period)
                    ticks shouldBe 3
                }
            }
        }

        "when cancelled" - {
            "should stop emitting values" {
                runTest {
                    val period = 100.milliseconds
                    var ticks = 0

                    val job = launch {
                        tickerFlow(period).collect { ticks++ }
                    }

                    advanceTimeBy(period * 3)
                    ticks shouldBe 3

                    job.cancelAndJoin()

                    advanceTimeBy(period * 3)
                    ticks shouldBe 3
                }
            }
        }
    }

    "windowed" - {
        "emits empty list on empty flow" {
            runTest {
                // given
                val flow = emptyFlow<Int>()

                // when
                val result = flow.windowed(100.milliseconds).toList()

                // then
                result shouldBe emptyList()
            }
        }

        "groups elements within the time window" {
            runTest {
                // given
                val flow = flow {
                    emit(1)
                    delay(50)
                    emit(2)
                    delay(60) // 110ms total - new window
                    emit(3)
                    delay(50) // 160ms total
                    emit(4)
                }

                // when
                val result = flow.windowed(100.milliseconds).toList()

                // then
                result shouldBe listOf(
                    listOf(1, 2), // first 100ms window
                    listOf(3, 4), // next 100ms window
                )
            }
        }

        "emits partial window when flow completes" {
            runTest {
                // given
                val flow = flow {
                    emit(1)
                    delay(50)
                    emit(2)
                    delay(30) // 80ms total - flow completes before window ends
                }

                // when
                val result = flow.windowed(100.milliseconds).toList()

                // then
                result shouldBe listOf(listOf(1, 2))
            }
        }

        "handles rapid emissions within window" {
            runTest {
                // given
                val original = flow {
                    var i = 0
                    repeat(5) {
                        delay(50)
                        repeat(10) {
                            emit(i++)
                            delay(1)
                        }
                        delay(50)
                    }
                }

                // when
                val result: List<List<Int>> = original
                    .windowed(100.milliseconds)
                    .toList()

                // then
                result shouldHaveSize 5
                result.forEach {
                    it shouldHaveSize 10
                }

                result.flatMap { it }
                    .forEachIndexed { index, el -> el shouldBeEqual index }
            }
        }

        "creates new window after period" {
            runTest {
                // given
                val flow = flow {
                    emit(1)
                    delay(101)
                    emit(2)
                    delay(101)
                    emit(3)
                }

                // when
                val result = flow.windowed(100.milliseconds).toList()

                // then
                result shouldBe listOf(
                    listOf(1),
                    listOf(2),
                    listOf(3)
                )
            }
        }
    }
})
