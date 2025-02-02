package io.averkhoglyad

import org.apache.http.entity.ContentType
import us.abstracta.jmeter.javadsl.JmeterDsl.*
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger

private val now = LocalDateTime.now()
private val inc = AtomicInteger(100_000)

fun main() {
    val baseUrl = "http://127.0.0.1:8000"
    val threadGroup = rpsThreadGroup()
        .initThreads(10)
        .maxThreads(10)
//        .rampTo(10.0, Duration.ofSeconds(5))
//        .rampTo(100.0, Duration.ofSeconds(5))
//        .rampTo(1000.0, Duration.ofSeconds(5))
        .rampToAndHold(200.0, Duration.ofSeconds(1), Duration.ofSeconds(60))
        .rampTo(0.0, Duration.ofSeconds(1))
        .children(
            httpDefaults().url(baseUrl),
//            httpSampler("/actuator/health")
            httpSampler("/users")
                .post({ generateUserCreateRequestBody() }, ContentType.APPLICATION_JSON),
        )

    testPlan(threadGroup).run()
//    testPlan(threadGroup, htmlReporter("E:\\tmp\\jmeter")).run()
}

private fun generateUserCreateRequestBody(): String = inc.getAndIncrement()
    .let { i -> "{ \"name\": \"user-${now}-${i}\", \"email\": \"user_${now}_${i}@fake.mail\" }" }
