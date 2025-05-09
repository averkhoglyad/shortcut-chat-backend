package io.averkhoglyad.shortcut.common.web.handler

import io.averkhoglyad.shortcut.common.data.EntityResult
import io.averkhoglyad.shortcut.common.web.exception.badRequest
import io.averkhoglyad.shortcut.common.web.exception.conflict
import io.averkhoglyad.shortcut.common.web.exception.internalError
import io.averkhoglyad.shortcut.common.web.exception.notFound
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import kotlin.reflect.full.isSubclassOf

@ResponseBody
class EntityResultHandler : ResponseBodyAdvice<Any> {

    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>?>): Boolean =
        returnType.isSubtypeOf<EntityResult<*>>()

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? {
        return when (body) {
            is EntityResult<*> -> body.unwrap()
            else -> body
        }
    }
}

private inline fun <reified E : Any> MethodParameter.isSubtypeOf(): Boolean =
    parameterType.kotlin.isSubclassOf(E::class)

fun <E> EntityResult<E>.unwrap(): E = when (this) {
    is EntityResult.Success<E> -> this.entity
    is EntityResult.NotFound -> throw notFound()
    is EntityResult.Invalid<E> -> throw badRequest()
    is EntityResult.Conflict<E> -> throw conflict()
    is EntityResult.Failed<E> -> throw internalError()
}
