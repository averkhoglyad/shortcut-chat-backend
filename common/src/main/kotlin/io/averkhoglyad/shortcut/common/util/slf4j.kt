package io.averkhoglyad.shortcut.common.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

fun slf4j(): Slf4j<Any> = Slf4jByInstance

fun slf4j(name: String): Slf4j<Any?> = Slf4jByName(name)

fun slf4j(clazz: KClass<*>): Slf4j<Any?> = Slf4jByClass(clazz.java)

fun slf4j(clazz: Class<*>): Slf4j<Any?> = Slf4jByClass(clazz)

// Slf4j
interface Slf4j<R> {
    operator fun getValue(thisRef: R, property: KProperty<*>): Logger
}

private object Slf4jByInstance : Slf4j<Any> {
    override operator fun getValue(thisRef: Any, property: KProperty<*>): Logger = LoggerFactory.getLogger(thisRef::class.java)
}

private class Slf4jByClass(private val clazz: Class<*>) : Slf4j<Any?> {
    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Logger = LoggerFactory.getLogger(clazz)
}

private class Slf4jByName(private val name: String) : Slf4j<Any?> {
    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Logger = LoggerFactory.getLogger(name)
}

//
fun Logger.trace(msg: () -> String) {
    if (isTraceEnabled) trace(msg())
}

fun Logger.debug(msg: () -> String) {
    if (isDebugEnabled) debug(msg())
}

fun Logger.info(msg: () -> String) {
    if (isInfoEnabled) info(msg())
}

fun Logger.warn(msg: () -> String) {
    if (isWarnEnabled) warn(msg())
}

fun Logger.error(msg: () -> String) {
    if (isErrorEnabled) error(msg())
}
