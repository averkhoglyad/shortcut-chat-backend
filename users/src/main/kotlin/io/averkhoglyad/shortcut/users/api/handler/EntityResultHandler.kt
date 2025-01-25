package io.averkhoglyad.shortcut.users.api.handler

import io.averkhoglyad.shortcut.users.api.exception.badRequest
import io.averkhoglyad.shortcut.users.api.exception.conflict
import io.averkhoglyad.shortcut.users.api.exception.internalError
import io.averkhoglyad.shortcut.users.api.exception.notFound
import io.averkhoglyad.shortcut.users.core.data.EntityResult
import org.springframework.core.MethodParameter
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubclassOf

@RestControllerAdvice
@Order(10)
class EntityResultHandler : ResponseBodyAdvice<Any> {

    override fun supports(
        returnType: MethodParameter,
        converterType: Class<out HttpMessageConverter<*>?>
    ): Boolean =
        returnType.classIsAnnotatedWith<RestController>() && returnType.isSubtypeOf<EntityResult<*>>()

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? =
        when (body) {
            is EntityResult<*> -> body.unwrap()
            else -> body
        }
}

private inline fun <reified E : Annotation> MethodParameter.classIsAnnotatedWith(): Boolean =
    declaringClass.kotlin.hasAnnotation<E>()

private inline fun <reified E : Any> MethodParameter.isSubtypeOf(): Boolean {
    return parameterType.kotlin.isSubclassOf(E::class)
}

private fun <E> EntityResult<E>.unwrap(): E = when (this) {
    is EntityResult.Success<E> -> this.entity
    is EntityResult.NotFound<E> -> notFound()
    is EntityResult.Invalid<E> -> badRequest()
    is EntityResult.Conflict<E> -> conflict()
    is EntityResult.Failed<E> -> internalError()
}
