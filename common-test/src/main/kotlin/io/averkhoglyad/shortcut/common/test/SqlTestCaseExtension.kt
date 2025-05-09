package io.averkhoglyad.shortcut.common.test

import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.extensions.spring.testContextManager
import org.springframework.context.ApplicationContext
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import javax.sql.DataSource

class SqlTestCaseExtension(
    private val before: List<String> = emptyList(),
    private val after: List<String> = emptyList()
) : TestCaseExtension {

    override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
        return withTestApplicationContext {
            val dataSource = getBean(DataSource::class.java)
            resourceDatabasePopulatorFor(before)
                .execute(dataSource)

            val result = execute(testCase)

            resourceDatabasePopulatorFor(after)
                .execute(dataSource)

            return@withTestApplicationContext result
        }
    }
}

fun sql(before: List<String> = emptyList(), after: List<String> = emptyList()): TestCaseExtension {
    return SqlTestCaseExtension(before, after)
}

suspend fun executeSql(vararg scripts: String) {
    executeSql(listOf(*scripts))
}

suspend fun executeSql(scripts: Iterable<String>) {
    withTestApplicationContext {
        val dataSource = getBean(DataSource::class.java)
        resourceDatabasePopulatorFor(scripts)
            .execute(dataSource)
    }
}

private suspend inline fun <R> withTestApplicationContext(crossinline block: suspend ApplicationContext.() -> R): R {
    return testApplicationContext().block()
}

private suspend fun testApplicationContext(): ApplicationContext = testContextManager()
    .testContext
    .applicationContext

private fun ApplicationContext.resourceDatabasePopulatorFor(scripts: Iterable<String>): ResourceDatabasePopulator {
    return scripts
        .map { environment.resolveRequiredPlaceholders(it) }
        .map { getResource(it) }
        .toTypedArray()
        .let { ResourceDatabasePopulator(*it) }
}
