package com.friendship41.gatewayserver.common

import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.server.ServerWebExchange

@Component
class GlobalErrorAttributes: ErrorAttributes {
    val GLOBAL_ERROR_ATTRIBUTE = "${GlobalErrorAttributes::class.java.name}.ERROR"

    override fun getErrorAttributes(request: ServerRequest, options: ErrorAttributeOptions): MutableMap<String, Any> {
        val a = options.isIncluded(ErrorAttributeOptions.Include.MESSAGE)
        val b = options.isIncluded(ErrorAttributeOptions.Include.STACK_TRACE)

        val attrs = request.exchange().attributes
        val currException = attrs[GLOBAL_ERROR_ATTRIBUTE]

        return super.getErrorAttributes(request, options)
    }

    override fun getError(request: ServerRequest): Throwable = request
            .attribute(GLOBAL_ERROR_ATTRIBUTE)
            .map { it as Throwable }
            .orElseThrow{ IllegalStateException("Missing exception attribute in ServerWebExchange") }

    override fun storeErrorInformation(error: Throwable?, exchange: ServerWebExchange) {
        exchange.attributes.putIfAbsent(GLOBAL_ERROR_ATTRIBUTE, error)
    }

}
